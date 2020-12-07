package org.neat4j.neat.manager.prediction;

import org.apache.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.NEATChromosome;
import org.neat4j.neat.core.NEATConfig;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.core.control.NEATNetManagerForService;
import org.neat4j.neat.data.core.DataKeeper;
import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.data.set.InputImpl;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.manager.train.NEATTrainingForService;
import ru.filippov.neatexecutor.entity.ColumnsDto;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;
import ru.filippov.neatexecutor.entity.ProjectConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WindowPrediction implements Runnable {
    private final static Logger logger = Logger.getLogger(WindowPrediction.class);
/*    DataKeeper dataKeeper;
    DataKeeper[] dataForWindow;
    DataKeeper[] dataFromWindows;*/
    AIConfig baseAiConfig;
    ProjectConfig.NormalizedDataDto normalizedData;
    int windowsSize = 0;
    int totalParams = 0;
    int yearPrediction = 0;
    int numOfInputs = 0;
    int numOfOutputs = 0;
    NEATTrainingForService[] trainer;
    Double[][] predictedInputDatas;
    List<List<Double>> outputData;
    Double predictionError = 0.0;
    Chromosome trainedModel;
    Double timeSpend;
    Long startTime;
    NEATNeuralNet neatNeuralNet;


    WindowTrainThread[] inputThreads;

    public WindowPrediction(Chromosome trainedModel, NeatConfigEntity neatConfigEntity) throws IOException, InitialisationFailedException {
        this.windowsSize = neatConfigEntity.getPredictionWindowSize();
        this.yearPrediction = neatConfigEntity.getPredictionPeriod();
        this.trainedModel = trainedModel;
        this.initialise(neatConfigEntity);
    }

    public NEATTrainingForService getTrainer(int index){
        return this.trainer[index];
    }

    public void initialise(NeatConfigEntity neatConfigEntity) throws IOException, InitialisationFailedException {

        AIConfig aiConfig = NEATTrainingForService.parseNeatSetting(neatConfigEntity.getNeatSettings());

        this.neatNeuralNet = NEATNeuralNet.createNet(new NEATConfig(aiConfig), this.trainedModel);

        this.normalizedData = neatConfigEntity.getNormalizedData();
        this.numOfInputs = (int) aiConfig.getConfigElementByName("INPUT.NODES");
        this.numOfOutputs = (int) aiConfig.getConfigElementByName("OUTPUT.NODES");
        this.totalParams = this.numOfInputs +  this.numOfOutputs;
        aiConfig.updateConfig("INPUT.NODES", windowsSize);
        aiConfig.updateConfig("OUTPUT.NODES", 1);
        aiConfig.updateConfig("FEATURE.SELECTION", false);
        this.baseAiConfig = aiConfig;
        inputThreads = new WindowTrainThread[totalParams];
        this.trainer = new NEATTrainingForService[totalParams];
        predictedInputDatas = new Double[normalizedData.getColumns().get(0).getData().size()-windowsSize+yearPrediction][totalParams];
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

    public ProjectConfig.NormalizedDataDto prepareDataForWindow(int index){
        ColumnsDto columnsDto = this.normalizedData.getColumns().get(index);

        List<ColumnsDto> columns = new ArrayList<>(windowsSize+1);

        for (int i = 0; i < windowsSize + 1; i++) {
            columns.add(new ColumnsDto());
            columns.get(i).setData(new ArrayList<>(columnsDto.getData().size()));
            columns.get(i).setColumnName(String.valueOf(i));
            if(i >= windowsSize) {

                columns.get(i).setColumnType("Output");
            } else {
                columns.get(i).setColumnType("Input");
            }
        }

        for (int i = 0; i < columnsDto.getData().size() - windowsSize - 1; i++) {

            int columnIndex = 0;


            for (int j = i; j < i + windowsSize; j++) {
                columns.get(columnIndex++).getData().add(columnsDto.getData().get(j));
            }
            columns.get(columnIndex).getData().add(columnsDto.getData().get(i + windowsSize + 1));
        }


        return new ProjectConfig.NormalizedDataDto(null,
                columns,
                (int) (columns.get(0).getData().size() * 0.75),
                columns.get(0).getData().size()
        );


        /*List<List<Double>> windowData = new ArrayList<>(dataKeeper.getData().size()- windowsSize);
        List<Double> legend = new ArrayList<>(dataKeeper.getData().size()- windowsSize);
        List<String> headers = new ArrayList<>(windowsSize +1);
        List<Double> windowRow;
        for (int i = 0; i < dataKeeper.getData().size()- windowsSize; i++) {
            windowRow = new ArrayList<>(windowsSize +1);
            for (int j = i; j <= i+ windowsSize; j++) {
                if(i == 0) {
                    headers.add(String.valueOf(j+1));
                }
                windowRow.add(dataKeeper.getData().get(j).get(index));
            }

            legend.add(dataKeeper.getLegend() == null ? Double.valueOf(i+1) : dataKeeper.getLegend().get(i+windowsSize));
            windowData.add(windowRow);
        }

        DataKeeper windowDataKeeper = new DataKeeper(windowData, dataKeeper.getDataScaler());
        windowDataKeeper.setLegend(legend);
        windowDataKeeper.setInputs(windowsSize);
        windowDataKeeper.setOutputs(1);
        windowDataKeeper.setHeaders(headers);
        windowDataKeeper.setLegendHeader(dataKeeper.getLegendHeader() == null ? "Год" : dataKeeper.getHeaders().get(index));
        windowDataKeeper.calculateIndex(0.75);*/
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();

        train(0);

       /* Thread[] threads = new Thread[this.totalParams];
        for (int i = 0; i < this.totalParams; i++) {
            threads[i] = train(i);
        }
        try {
            for (int i = 0; i < this.totalParams; i++) {
                    threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            predict(neatNeuralNet);
        } catch (InitialisationFailedException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        this.outputData.forEach(doubles -> {
            doubles.forEach(aDouble -> stringBuilder.append(aDouble + " "));
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            stringBuilder.append("\n");
        });
        System.out.println(stringBuilder.toString());*/


    }

    public Thread train(int index){
        AIConfig aiConfig = new NEATConfig(this.baseAiConfig);
        Thread thread = new Thread(() -> {

            ProjectConfig.NormalizedDataDto normalizedDataDto = prepareDataForWindow(index);
            try {
                trainer[index] = new NEATTrainingForService(aiConfig, normalizedDataDto, 1);
                inputThreads[index] = new WindowTrainThread(index, trainer[index]);
                inputThreads[index].startTraining();
                predictFactorSign(index, aiConfig);
            } catch (InitialisationFailedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }

    private void predictFactorSign(int index, AIConfig config) throws InitialisationFailedException {
        //List<Chromosome> bestEverChromosomes = ;
        Chromosome bestChromosome = trainer[index].getBestChromosome();
        List<Double> inputs = new ArrayList<>(windowsSize+yearPrediction);
        List<Double> data = this.normalizedData.getColumns().get(index).getData();
        for (int i = data.size()-windowsSize; i < data.size(); i++) {
            inputs.add(data.get(i));
        }

        NEATNeuralNet neatNeuralNet = NEATNeuralNet.createNet(config, bestChromosome);
        //double[] inputPattern = new double[windowsSize];
        NetworkInput input = null;
        NetworkOutputSet execute = null;
        Double[] inputTemp  = new Double[windowsSize];

        for (int i = 0 ; i < bestChromosome.getOutputValues().size() ; i++) {
            predictedInputDatas[i][index] = bestChromosome.getOutputValues().get(i).get(0);
        }

        for (int i = 0; i < yearPrediction; i++) {
            for (int j = 0; j < windowsSize; j++) {
                inputTemp[j] = inputs.get(i+j);
            }
            input = new InputImpl(inputTemp);
            execute = neatNeuralNet.execute(input);
            inputs.add(execute.nextOutput().getNetOutputs().get(0));
            predictedInputDatas[bestChromosome.getOutputValues().size()+i][index] = execute.nextOutput().getNetOutputs().get(0);
        }


    }



    public List<Double> getPredictedInputs(int index){
        List<Double> list = new ArrayList<>(predictedInputDatas.length);
        for (int j = 0; j < predictedInputDatas.length; j++) {
            list.add(predictedInputDatas[j][index]);
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

    private void predict(NEATNeuralNet neatNeuralNet) throws InitialisationFailedException {


        for (int i = 0; i < predictedInputDatas.length; i++) {
            for (int j = 0; j < predictedInputDatas[i].length; j++) {
                if(predictedInputDatas[i][j] == null){
                    throw new InitialisationFailedException("WindowPrediction model should be trained firstly");
                }
            }
        }

        NetworkInput input = null;
        NetworkOutputSet os = null;
        this.outputData = new ArrayList<>(predictedInputDatas.length + yearPrediction + yearPrediction);

        double error = 0;
        int n = 0;
        List<Double> netOutputs = null;

        Double[][] dataKeeper = new Double[predictedInputDatas.length + yearPrediction + yearPrediction][this.totalParams];

        for (int i = 0; i < normalizedData.getColumns().size(); i++) {
            for (int j = 0; j < normalizedData.getColumns().get(i).getData().size(); j++) {
                dataKeeper[j][i] = normalizedData.getColumns().get(i).getData().get(j);
            }
        }

        for (int i = 0; i < windowsSize; i++) {



            input = new InputImpl(dataKeeper[i], this.numOfInputs);
            os = neatNeuralNet.execute(input);
            netOutputs = os.nextOutput().getNetOutputs();
            this.outputData.add(netOutputs);
            for (int j = 0; j < netOutputs.size(); j++) {
                double diff = netOutputs.get(j) - dataKeeper[i][numOfInputs+j];
                error += diff * diff;
                n++;
            }
        }

        //0.038117398190327154
        for (int i = 0; i < predictedInputDatas.length; i++) {
            input = new InputImpl(predictedInputDatas[i]);
            input = new InputImpl(predictedInputDatas[i], this.numOfInputs);
            os = neatNeuralNet.execute(input);
            netOutputs = os.nextOutput().getNetOutputs();
            this.outputData.add(netOutputs);
            if(i+windowsSize <  normalizedData.getColumns().size()) {
                for (int j = 0; j < netOutputs.size(); j++) {
                    double diff = netOutputs.get(j) - dataKeeper[i + windowsSize][numOfInputs+j];
                    error += diff * diff;
                    n++;
                }
            }
        }
        error = Math.sqrt(error/n);
        this.predictionError = error;


        for (int i = 0; i < yearPrediction; i++) {
            input = new InputImpl(predictedInputDatas[i]);
            os = neatNeuralNet.execute(input);
            this.outputData.add(os.nextOutput().getNetOutputs());
        }

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
