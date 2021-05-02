package ru.filippov.neatexecutor.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.manager.prediction.PredictionResultCsvConverter;
import org.neat4j.neat.manager.prediction.WindowPrediction;
import org.neat4j.neat.manager.train.NEATTrainingForService;
import org.neat4j.neat.utils.NetTopology;
import org.neat4j.neat.utils.NetTopologyAnalyzer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.filippov.neatexecutor.config.RabbitConfig;
import ru.filippov.neatexecutor.entity.ExperimentConfigEntity;
import ru.filippov.neatexecutor.entity.ProjectConfig;
import ru.filippov.neatexecutor.entity.ServiceResult;
import ru.filippov.neatexecutor.entity.WindowPredictionResult;
import ru.filippov.neatexecutor.samba.SambaWorker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Log4j2
public class RabbitMQListener {

    @Autowired
    private RabbitMQWriter rabbitMQWriter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitConfig rabbitConfig;

    @Autowired
    private SambaWorker sambaWorker;

    @RabbitListener(queues = "${rabbitmq.input.predictionData.queue.queueName:prediction-data}")
    public void consumeNewNeatConfig(ExperimentConfigEntity experimentConfigEntity) throws IOException {

        /*final NeatConfigEntity neatConfigEntity = objectMapper.readValue(message.getBody(), NeatConfigEntity.class);
*/
        ServiceResult serviceResult = new ServiceResult(
                experimentConfigEntity.getExperimentId(),
                null,
                null,
                null,
                null,
                null,
                null,
                "PREDICTION_SERVICE_ERROR");




        try {
            byte[] bytes = sambaWorker.readFile(experimentConfigEntity.getDataFilename());
            experimentConfigEntity.setNormalizedData(new ProjectConfig.NormalizedDataDto(bytes, experimentConfigEntity.getColumns(), experimentConfigEntity.getTrainEndIndex(), experimentConfigEntity.getTestEndIndex()));
            log.info("Training");
            AIConfig aiConfig = NEATTrainingForService.parseNeatSetting(experimentConfigEntity.getNeatSettings());
            NEATTrainingForService neatGaTrainingManager = new NEATTrainingForService(aiConfig, experimentConfigEntity.getNormalizedData());
            neatGaTrainingManager.run();





            ExecutorService executor = Executors.newFixedThreadPool(2);
            Callable<NetTopology> netTopologyCallable = new NetTopologyAnalyzer(neatGaTrainingManager.getBestChromosome());
            Future<NetTopology> netTopologyFuture = executor.submit(netTopologyCallable);
            log.info("Prediction");
            Callable<WindowPredictionResult> windowPrediction = new WindowPrediction(
                    neatGaTrainingManager.getBestChromosome(),
                    experimentConfigEntity);
            Future<WindowPredictionResult> future = executor.submit(windowPrediction);

            WindowPredictionResult windowPredictionResult = future.get();
            NetTopology netTopology = netTopologyFuture.get();
            executor.shutdown();


            PredictionResultCsvConverter predictionResultCsvConverter = new PredictionResultCsvConverter();
            byte[] csvData = predictionResultCsvConverter.convert(windowPredictionResult);

            String experimentResultFilename = sambaWorker.writePredictionResultFile(csvData, experimentConfigEntity.getProjectName(), experimentConfigEntity.getExperimentId(), experimentConfigEntity.getUsername());
            Map<String, List<Map<String,Object>>> windowTrainStatistic = new HashMap<>(2);
            List<Map<String, Object>> factorSigns = windowPredictionResult.getFactorSigns();
            List<Map<String, Object>> targetSigns = windowPredictionResult.getTargetSigns();
            factorSigns.forEach(stringObjectMap -> {stringObjectMap.remove("data");});
            targetSigns.forEach(stringObjectMap -> {stringObjectMap.remove("data");});


            windowTrainStatistic.put("factorSigns", factorSigns);
            windowTrainStatistic.put("targetSigns", targetSigns);

            serviceResult = new ServiceResult(
                    experimentConfigEntity.getExperimentId(),
                    neatGaTrainingManager.getTrainErrors(),
                    neatGaTrainingManager.getTestErrors(),
                    windowPredictionResult.getPredictionError(),
                    experimentResultFilename,
                    windowTrainStatistic,
                    netTopology,
                    "PREDICTED");






            /*List<List<NEATNeuron>> lists = this.analyseNeuronStructure((NEATNeuralNet) NEATNeuralNet.createNet(neatGaTrainingManager.getBestChromosome()));
            lists.forEach(neatNeurons -> {
                neatNeurons.forEach(neatNeuron -> System.out.print(neatNeuron.getLabel()+" | "));
                System.out.println();
            });
            *//*Arrays.stream(neatGaTrainingManager.getBestChromosome().genes()).forEach(gene -> {
                if(gene instanceof NEATNodeGene){
                    if(((NEATNodeGene) gene).getType() == NEATNodeGene.TYPE.INPUT){
                        System.out.println(((NEATNodeGene) gene).getLabel());
                    }
                }
            });*/
            /*Set<String> labels = new HashSet<>(20);

            List<Gene> genes = new ArrayList<>(neatGaTrainingManager.getBestChromosome().size());
            Arrays.stream(neatGaTrainingManager.getBestChromosome().genes()).forEach(gene -> {
                if(gene instanceof NEATNodeGene){
                    if(((NEATNodeGene) gene).getType() == NEATNodeGene.TYPE.INPUT){
                        if(((NEATNodeGene) gene).getDepth() >= 0) {
                            genes.add(gene);
                            labels.add(((NEATNodeGene) gene).getLabel());
                        }
                    } else {
                        genes.add(gene);
                    }
                } else {
                    genes.add(gene);
                }
            });
            List<ColumnsDto> collect = neatConfigEntity.getNormalizedData().getColumns().stream().filter(columnsDto -> labels.contains(columnsDto.getColumnName())).collect(Collectors.toList());

            neatConfigEntity.getNormalizedData().setColumns(collect);

            Chromosome chromosome = new NEATChromosome(genes.toArray(Gene[]::new));

            new WindowPrediction(chromosome, neatConfigEntity).run();*/







        } catch (InitialisationFailedException e) {
            log.error("[NeatDataListener].consumeNewNeatConfig(), experimentConfigEntity = " + experimentConfigEntity.toString(), e);
        } catch (Exception e) {
            log.error("[NeatDataListener].consumeNewNeatConfig(), experimentConfigEntity = " + experimentConfigEntity.toString(), e);
        } finally {
            this.rabbitMQWriter.sendMessage(rabbitConfig.RABBITMQ_OUTPUT_PREDICTION_RESULT_ROUTING_KEY, serviceResult);
        }


    }
}
