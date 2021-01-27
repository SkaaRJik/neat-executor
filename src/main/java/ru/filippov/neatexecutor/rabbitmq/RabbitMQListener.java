package ru.filippov.neatexecutor.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.manager.prediction.WindowPrediction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.filippov.neatexecutor.config.RabbitConfig;
import ru.filippov.neatexecutor.entity.*;
import org.neat4j.neat.manager.train.NEATTrainingForService;
import org.neat4j.neat.utils.NetTopology;
import org.neat4j.neat.utils.NetTopologyAnalyzer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;
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

    @RabbitListener(queues = "${rabbitmq.input.queue.experiment:experiment}")
    public void consumeNewNeatConfig(NeatConfigEntity neatConfigEntity) throws IOException {

        /*final NeatConfigEntity neatConfigEntity = objectMapper.readValue(message.getBody(), NeatConfigEntity.class);
*/

        try {
            log.info("Training");
            AIConfig aiConfig = NEATTrainingForService.parseNeatSetting(neatConfigEntity.getNeatSettings());
            NEATTrainingForService neatGaTrainingManager = new NEATTrainingForService(aiConfig, neatConfigEntity.getNormalizedData());

            this.rabbitMQWriter.sendMessage("status", new ExperimentStatusDto(neatConfigEntity.getProjectId(), "TRAINING"));

            neatGaTrainingManager.run();

            TrainResult trainResults = neatGaTrainingManager.getTrainResults();





            ExecutorService executor = Executors.newFixedThreadPool(2);
            Callable<NetTopology> netTopologyCallable = new NetTopologyAnalyzer(neatGaTrainingManager.getBestChromosome());
            Future<NetTopology> netTopologyFuture = executor.submit(netTopologyCallable);
            log.info("Prediction");
            Callable<WindowPredictionResult> windowPrediction = new WindowPrediction(
                    neatConfigEntity.getNeatConfigId(),
                    neatConfigEntity.getProjectId(),
                    neatGaTrainingManager.getBestChromosome(),
                    neatConfigEntity,
                    this.rabbitMQWriter,
                    this.rabbitConfig.RABBITMQ_OUTPUT_STATUS_ROUTING_KEY);
            Future<WindowPredictionResult> future = executor.submit(windowPrediction);

            WindowPredictionResult windowPredictionResult = future.get();
            NetTopology netTopology = netTopologyFuture.get();



            executor.shutdown();

            ServiceResult serviceResult = new ServiceResult(neatConfigEntity.getProjectId(), trainResults, netTopology, windowPredictionResult);

            this.rabbitMQWriter.sendMessage(rabbitConfig.RABBITMQ_OUTPUT_RESULT_ROUTING_KEY, serviceResult);




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
            log.error("[NeatDataListener].consumeNewNeatConfig()", e);
        } catch (Exception e) {
            log.error("[NeatDataListener].consumeNewNeatConfig()", e);
        }


    }
}
