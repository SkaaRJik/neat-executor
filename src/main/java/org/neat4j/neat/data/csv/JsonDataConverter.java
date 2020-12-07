package org.neat4j.neat.data.csv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.neat.data.core.*;
import org.neat4j.neat.data.set.*;
import ru.filippov.neatexecutor.entity.ProjectConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JsonDataConverter {
    private static final Logger logger = LogManager.getLogger(JsonDataConverter.class);
    ProjectConfig.NormalizedDataDto dataToParse;

    int inputsCount;
    int outputsCount;
    int trainEndIndex;
    int testEndIndex;


    public JsonDataConverter(ProjectConfig.NormalizedDataDto data, int inputsCount, int outputsCount, int trainEndIndex, int testEndIndex) {
        this.dataToParse = data;
        this.inputsCount = inputsCount;
        this.outputsCount = outputsCount;
        this.trainEndIndex = trainEndIndex;
        this.testEndIndex = testEndIndex;
    }


    public List<NetworkDataSet> loadData() {
        return createDataSets();
    }

    private List<NetworkDataSet> createDataSets() {
        logger.debug("Creating data sets");

        try {
            List<String> inputHeaders = new ArrayList<>(inputsCount);
            List<String> outputHeaders = new ArrayList<>(outputsCount);

            List<List<Double>> inputColumns = new ArrayList<>(inputsCount);
            List<List<Double>> outputColumns = new ArrayList<>(outputsCount);
            this.dataToParse.getColumns().forEach(stringObjectMap -> {
                final String columnType = stringObjectMap.getColumnType();
                switch (columnType){
                    case "Input":
                        inputHeaders.add(stringObjectMap.getColumnName());
                        inputColumns.add(stringObjectMap.getData());
                        break;
                    case "Output":
                        outputHeaders.add(stringObjectMap.getColumnName());
                        outputColumns.add(stringObjectMap.getData());
                        break;
                }
            });

            return this.fillInputsAndOutputsWithColumnData(
                    inputColumns,
                    outputColumns,
                    inputHeaders,
                    outputHeaders,
                    trainEndIndex,
                    testEndIndex
            );

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        logger.debug("Creating data sets...Done");
        return null;
    }

    private List<NetworkDataSet>  fillInputsAndOutputsWithColumnData(List<List<Double>> inputColumns, List<List<Double>> outputColumns, List<String> inputHeaders, List<String> outputHeaders, int trainEndIndex, int testEndIndex) {

        List<NetworkOutput> trainOps = new ArrayList();
        List<NetworkOutput> testOps = new ArrayList();
        List<NetworkInput> trainIps = new ArrayList();
        List<NetworkInput> testIps = new ArrayList();

        for (int i = 0; i < inputColumns.get(0).size(); i++) {
            double[] pattern = new double[inputColumns.size()];
            int patternIndex = 0;
            for (int j = 0; j < inputColumns.size(); j++) {
                pattern[patternIndex++] = inputColumns.get(j).get(i);
            }
            if( i < trainEndIndex) {
                trainIps.add(new InputImpl(pattern));
            } else if(i >= trainEndIndex && i < testEndIndex){
                testIps.add(new InputImpl(pattern));
            }
        }



        for (int i = 0; i < outputColumns.get(0).size(); i++) {
            double[] pattern = new double[outputColumns.size()];
            int patternIndex = 0;
            for (int j = 0; j < outputColumns.size(); j++) {
                pattern[patternIndex++] = outputColumns.get(j).get(i);
            }
            if( i < trainEndIndex) {
                trainOps.add(new ExpectedOutputImpl(pattern));
            } else if(i >= trainEndIndex && i < testEndIndex){
                testOps.add(new ExpectedOutputImpl(pattern));
            }
        }
        

        NetworkInputSet ipTrainSet = new InputSetImpl(inputHeaders, trainIps);
        ExpectedOutputSet opTrainSet = new ExpectedOutputSetImpl(outputHeaders, trainOps);

        NetworkInputSet ipTestSet = new InputSetImpl(inputHeaders, testIps);
        ExpectedOutputSet opTestSet = new ExpectedOutputSetImpl(outputHeaders, testOps);


        NetworkDataSet trainDataSet = new DataSetImpl(ipTrainSet, opTrainSet);
        NetworkDataSet testDataSet = new DataSetImpl(ipTestSet, opTestSet);

        return List.of(trainDataSet, testDataSet);

    }
}
