package org.neat4j.neat.manager.prediction;

import org.apache.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.NEATConfig;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.data.set.InputImpl;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.manager.train.NEATTrainingForService;
import ru.filippov.neatexecutor.entity.ColumnsDto;
import ru.filippov.neatexecutor.entity.ExperimentConfigEntity;
import ru.filippov.neatexecutor.entity.ProjectConfig;
import ru.filippov.neatexecutor.entity.WindowPredictionResult;
import ru.filippov.neatexecutor.exception.IncorrectFileFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class WindowPrediction implements Callable<WindowPredictionResult> {
    private final static Logger logger = Logger.getLogger(WindowPrediction.class);

    private AIConfig baseAiConfig;
    private ProjectConfig.NormalizedDataDto normalizedData;
    private int windowsSize = 0;
    private int totalParams = 0;
    private int yearPrediction = 100;
    private int numOfInputs = 0;
    private int numOfOutputs = 0;
    private NEATTrainingForService[] trainer;
    private Double[][] predictedInputData;
    private Double[][] parsedData;


    private List<List<Double>> outputData;
    private Double predictionError = 0.0;
    private Chromosome trainedModel;
    private Long timeSpend;
    private Long startTime;
    private NEATNeuralNet neatNeuralNet;

    private List<Map<String, Object>> factorSigns;
    private List<Map<String, Object>> targetSigns;


    private WindowTrainThread[] inputThreads;


    public WindowPrediction(Chromosome trainedModel, ExperimentConfigEntity experimentConfigEntity) throws IOException, InitialisationFailedException {
        this.windowsSize = experimentConfigEntity.getPredictionWindowSize();
        this.yearPrediction = experimentConfigEntity.getPredictionPeriod();
        this.trainedModel = trainedModel;
        this.initialise(experimentConfigEntity);
    }

    public NEATTrainingForService getTrainer(int index){
        return this.trainer[index];
    }

    public void initialise(ExperimentConfigEntity experimentConfigEntity) throws IOException, InitialisationFailedException {

        AIConfig aiConfig = NEATTrainingForService.parseNeatSetting(experimentConfigEntity.getNeatSettings());

        this.neatNeuralNet = NEATNeuralNet.createNet(new NEATConfig(aiConfig), this.trainedModel);

        this.normalizedData = experimentConfigEntity.getNormalizedData();
        this.numOfInputs = (int) aiConfig.getConfigElementByName("INPUT.NODES");
        this.numOfOutputs = (int) aiConfig.getConfigElementByName("OUTPUT.NODES");
        this.totalParams = this.numOfInputs +  this.numOfOutputs;
        aiConfig.updateConfig("INPUT.NODES", windowsSize);
        aiConfig.updateConfig("OUTPUT.NODES", 1);
        aiConfig.updateConfig("FEATURE.SELECTION", false);
        this.baseAiConfig = aiConfig;
        inputThreads = new WindowTrainThread[totalParams];
        this.trainer = new NEATTrainingForService[totalParams];
        predictedInputData = new Double[normalizedData.getColumns().get(0).getData().size()-windowsSize+yearPrediction][totalParams];

        this.parsedData = new Double[normalizedData.getColumns().get(0).getData().size()][totalParams];

        int inputIndex = 0;
        this.factorSigns = new ArrayList<>(normalizedData.getColumns().size());
        this.targetSigns = new ArrayList<>(normalizedData.getColumns().size());
        for (int i = 0; i < normalizedData.getColumns().size(); i++) {
            if("Input".equals(normalizedData.getColumns().get(i).getColumnType())){
                Map<String, Object> columnInfo = new HashMap<>(5);
                columnInfo.put("name", normalizedData.getColumns().get(i).getColumnName());
                columnInfo.put("columnType", normalizedData.getColumns().get(i).getColumnType());
                this.factorSigns.add(columnInfo);
                for (int j = 0; j < normalizedData.getColumns().get(i).getData().size(); j++) {
                    this.parsedData[j][inputIndex] = normalizedData.getColumns().get(i).getData().get(j);
                }
                inputIndex++;
            }
        }

        for (int i = 0; i < normalizedData.getColumns().size(); i++) {
            if("Output".equals(normalizedData.getColumns().get(i).getColumnType())){
                Map<String, Object> columnInfo = new HashMap<>(5);
                columnInfo.put("name", normalizedData.getColumns().get(i).getColumnName());
                columnInfo.put("columnType", normalizedData.getColumns().get(i).getColumnType());


                Map<String, Object> targetColumnInfo = new HashMap<>(5);
                targetColumnInfo.put("name", normalizedData.getColumns().get(i).getColumnName());
                targetColumnInfo.put("data", new ArrayList<Double>(normalizedData.getColumns().size() + yearPrediction));

                this.factorSigns.add(columnInfo);
                this.targetSigns.add(targetColumnInfo);
                for (int j = 0; j < normalizedData.getColumns().get(i).getData().size(); j++) {
                    this.parsedData[j][inputIndex] = normalizedData.getColumns().get(i).getData().get(j);
                }
                inputIndex++;
            }
        }



        /*if(dataKeeper.getData().size() - windowsSize <= 3) throw new InitialisationFailedException("Размера набора данных недостаточно для заданного размера окна\n" +
                "Выборка будет состоять из " + (dataKeeper.getData().size() - windowsSize) + " элементов, необходимо хотя бы 4");
        this.dataKeeper = dataKeeper;

        this.inputs = dataKeeper.getInputs()+dataKeeper.getOutputs();
        this.dataFromWindows = new DataKeeper[this.inputs];
        dataForWindow = new DataKeeper[this.inputs];
        config.updateConfig("INPUT.NODES", String.valueOf(windowsSize));
        config.updateConfig("OUTPUT.NODES", "1");
        configForWindow = new AIConfig[this.inputs];
        trainer = new NEATTrainingForJavaFX[inputs];
        inputThreads = new ru.filippov.prediction.WindowTrainThread[inputs];
        predictionInputEnded = new SimpleObjectProperty[inputs];
        predictionOutputEnded = new SimpleObjectProperty<>(false);
        for (int i = 0; i < inputs; i++) {
            trainer[i] = new NEATTrainingForJavaFX();
            configForWindow[i] = new NEATConfig((NEATConfig) config);
            predictionInputEnded[i] = new SimpleObjectProperty<>(false);
        }
        predictedInputDatas = new Double[dataKeeper.getData().size()-windowsSize+yearPrediction][inputs];*/
        //predictedWindowDatas = new Double[dataKeeper.getData().size()-windowsSize][inputs];
    }

    public ProjectConfig.NormalizedDataDto prepareDataForWindow(int index) throws IOException, IncorrectFileFormatException {

        Double[] columnData = new Double[this.parsedData.length];


        for (int j = 0; j < this.parsedData.length; j++) {
            columnData[j] = this.parsedData[j][index];
        }


        List<ColumnsDto> columns = new ArrayList<>(windowsSize+1);

        for (int i = 0; i < windowsSize + 1; i++) {
            columns.add(new ColumnsDto());
            columns.get(i).setData(new ArrayList<Double>());
            columns.get(i).setColumnName(String.valueOf(i));
            if(i >= windowsSize) {
                columns.get(i).setColumnType("Output");
            } else {
                columns.get(i).setColumnType("Input");
            }
        }

        for (int i = 0; i < columnData.length - windowsSize; i++) {

            int columnIndex = 0;

            for (int j = i; j < i + windowsSize; j++) {
                columns.get(columnIndex++).getData().add(columnData[j]);
            }
            columns.get(columnIndex).getData().add(columnData[i + windowsSize]);
        }


        return new ProjectConfig.NormalizedDataDto(null,
                columns,
                (int) (columns.get(0).getData().size() * 0.75),
                columns.get(0).getData().size()
        );
    }

    @Override
    public WindowPredictionResult call() throws ExecutionException, InterruptedException {

        startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> futureList = new ArrayList<>();
        for(int i = 0; i < this.totalParams; i++){
            //сабмитим Callable таски, которые будут
            //выполнены пулом потоков
            Future<?> future = executor.submit(train(i));
            //добавляя Future в список,
            //мы сможем получить результат выполнения
            futureList.add(future);
        }

        for(Future<?> fut : futureList){
            fut.get();
        }

        executor.shutdown();
        WindowPredictionResult windowPredictionResult = null;
        try {
            windowPredictionResult = predict(neatNeuralNet);
        } catch (InitialisationFailedException e) {
            logger.error("WindowPrediction.call", e);
        }

        return windowPredictionResult;
    }

    public Runnable train(int index){
        AIConfig aiConfig = new NEATConfig(this.baseAiConfig);
        Runnable runnable = () -> {

            try {
                ProjectConfig.NormalizedDataDto normalizedDataDto = prepareDataForWindow(index);
                trainer[index] = new NEATTrainingForService(aiConfig, normalizedDataDto, 1);
                inputThreads[index] = new WindowTrainThread(index, trainer[index]);
                inputThreads[index].startTraining();
                predictFactorSign(index, aiConfig);
            } catch (InitialisationFailedException e) {
                logger.error(String.format("WindowPrediction.train [index]=%d", index), e);
            } catch (IOException e) {
                logger.error(String.format("WindowPrediction.train [index]=%d", index), e);
            } catch (InterruptedException e) {
                logger.error(String.format("WindowPrediction.train [index]=%d", index), e);
            } catch (ExecutionException e) {
                logger.error(String.format("WindowPrediction.train [index]=%d", index), e);
            } catch (IncorrectFileFormatException e) {
                logger.error(String.format("WindowPrediction.train [index]=%d", index), e);
            }
        };
        return runnable;
    }

    private void predictFactorSign(int index, AIConfig config) throws InitialisationFailedException {
        //List<Chromosome> bestEverChromosomes = ;
        Chromosome bestChromosome = trainer[index].getBestChromosome();
        List<Double> inputs = new ArrayList<>(windowsSize+yearPrediction);

        for (int i = this.parsedData.length-windowsSize; i < this.parsedData.length; i++) {
            inputs.add(this.parsedData[i][index]);
        }

        NEATNeuralNet neatNeuralNet = NEATNeuralNet.createNet(config, bestChromosome);
        //double[] inputPattern = new double[windowsSize];
        NetworkInput input = null;
        NetworkOutputSet execute = null;
        Double[] inputTemp  = new Double[windowsSize];
        List<Double> columnValues = new ArrayList<>(bestChromosome.getOutputValues().size() + yearPrediction);
        for (int i = 0 ; i < bestChromosome.getOutputValues().size() ; i++) {
            predictedInputData[i][index] = bestChromosome.getOutputValues().get(i).get(0);
            columnValues.add( predictedInputData[i][index]);
        }

        for (int i = 0; i < yearPrediction; i++) {
            for (int j = 0; j < windowsSize; j++) {
                inputTemp[j] = inputs.get(i+j);
            }
            input = new InputImpl(inputTemp);
            execute = neatNeuralNet.execute(input);
            inputs.add(execute.nextOutput().getNetOutputs().get(0));
            predictedInputData[bestChromosome.getOutputValues().size()+i][index] = execute.nextOutput().getNetOutputs().get(0);
            columnValues.add( predictedInputData[bestChromosome.getOutputValues().size()+i][index] );
        }

        this.factorSigns.get(index).put("data", columnValues);
        this.factorSigns.get(index).put("trainError", trainer[index].getBestChromosome().getTrainError());
        this.factorSigns.get(index).put("testError", trainer[index].getBestChromosome().getValidationError());


    }



    public List<Double> getPredictedInputs(int index){
        List<Double> list = new ArrayList<>(predictedInputData.length);
        for (int j = 0; j < predictedInputData.length; j++) {
            list.add(predictedInputData[j][index]);
        }
        return list;
    }



    /*public void predict(AIConfig config) throws InitialisationFailedException, IOException, ClassNotFoundException {


        Chromosome chromo = (Chromosome) NEATChromosome.readObject(config.configElement("AI.SOURCE"));
        int configInputs = Integer.parseInt(config.getConfigElementByName("INPUT.NODES"));
        int configOutputs = Integer.parseInt(config.configElement("OUTPUT.NODES"));
        if(configInputs != dataKeeper.getInputs() || configOutputs != dataKeeper.getOutputs()){
            throw new IllegalArgumentException("Data and Model Mismatch!\n Trained model: Inputs = " +configInputs + " Outputs = " + configOutputs + "\n" +
                    "Data: Inputs = " +dataKeeper.getInputs() + " Outputs = " + dataKeeper.getOutputs());
        }
        NEATNeuralNet neatNeuralNet = NEATNeuralNet.createNet(config, chromo);
        predict(neatNeuralNet);


    }*/

    private WindowPredictionResult predict(NEATNeuralNet neatNeuralNet) throws InitialisationFailedException {


        for (int i = 0; i < predictedInputData.length; i++) {
            for (int j = 0; j < predictedInputData[i].length; j++) {
                if(predictedInputData[i][j] == null){
                    throw new InitialisationFailedException("WindowPrediction model should be trained firstly");
                }
            }
        }

        NetworkInput input = null;
        NetworkOutputSet os = null;
        this.outputData = new ArrayList<>(predictedInputData.length);

        double error = 0;
        int n = 0;
        List<Double> netOutputs = null;

        //Заполняем null значениями период [0-windowSize], так как этот диапазон выпадает из прогнозирования из-за метода окон
        for (int i = 0; i < windowsSize; i++) {
            ArrayList<Double> nullOutputs = new ArrayList<>(this.numOfOutputs);
            for (int j = 0; j < this.numOfOutputs; j++) {
                nullOutputs.add(null);
            }
            this.outputData.add(nullOutputs);
        }

        //0.038117398190327154
        for (int i = 0; i < this.parsedData.length - windowsSize; i++) {
            input = new InputImpl(predictedInputData[i], this.numOfInputs);
            os = neatNeuralNet.execute(input);
            netOutputs = os.nextOutput().getNetOutputs();
            this.outputData.add(netOutputs);
            if(i+windowsSize <  this.parsedData.length) {
                for (int j = 0; j < netOutputs.size(); j++) {
                    double diff = netOutputs.get(j) - this.parsedData[i + windowsSize][numOfInputs+j];
                    error += diff * diff;
                    n++;
                }
            }
        }
        error = Math.sqrt(error/n);
        this.predictionError = error;


        for (int i = 0; i < yearPrediction; i++) {
            input = new InputImpl(predictedInputData[i]);
            os = neatNeuralNet.execute(input);
            netOutputs = os.nextOutput().getNetOutputs();
            this.outputData.add(netOutputs);
        }

        long timeSpent = System.currentTimeMillis() - this.startTime;
        for (List<Double> outs: this.outputData) {
            for (int i = 0; i < outs.size(); i++) {
                ((List<Double>)this.targetSigns.get(i).get("data")).add(outs.get(i));
            }
        }

        for (int i = 0; i < this.factorSigns.size(); i++) {
            List<Double> data = (List<Double>) this.factorSigns.get(i).get("data");
            List<Double> newData = new ArrayList<>(data.size() + windowsSize);
            for (int j = 0; j < windowsSize; j++) {
                newData.add(null);
            }
            for (Double value: data) {
                newData.add(value);
            }

            this.factorSigns.get(i).put("data",newData);
        }

        WindowPredictionResult windowPredictionResult = new WindowPredictionResult(timeSpent, this.predictionError, this.factorSigns, this.targetSigns);

        return windowPredictionResult;

    }


    /*public void predict(Chromosome trainedModel, AIConfig config) throws InitialisationFailedException {
        int confInputs = Integer.parseInt(config.configElement("INPUT.NODES"));
        int confOutputs = Integer.parseInt(config.configElement("OUTPUT.NODES"));

        if(confInputs != dataKeeper.getInputs() || confOutputs != dataKeeper.getOutputs()){
            throw new IllegalArgumentException("Data and Model Mismatch!\n Trained model: Inputs = " +confInputs + " Outputs = " + confInputs + "\n" +
                    "Data: Inputs = " +dataKeeper.getInputs() + " Outputs = " + dataKeeper.getOutputs());
        }

        NEATNeuralNet neatNeuralNet = this.initNet(config, trainedModel);
        predict(neatNeuralNet);

    }*/

}
