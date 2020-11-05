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
        NetworkDataSet trainDataSet = null;
        NetworkDataSet testDataSet = null;



        try {
            List<String> inputHeaders = new ArrayList<>(inputsCount);
            List<String> outputHeaders = new ArrayList<>(outputsCount);

            List<List<Double>> inputColumns = new ArrayList<>(inputsCount);
            List<List<Double>> outputColumns = new ArrayList<>(outputsCount);
            this.dataToParse.getColumns().forEach(stringObjectMap -> {
                final String columnType = (String) stringObjectMap.get("columnType");
                switch (columnType){
                    case "Input":
                        inputHeaders.add((String) stringObjectMap.get("columnName"));
                        inputColumns.add((List<Double>) stringObjectMap.get("data"));
                        break;
                    case "Output":
                        outputHeaders.add((String) stringObjectMap.get("columnName"));
                        outputColumns.add((List<Double>) stringObjectMap.get("data"));
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
            double[] trainPattern = new double[trainEndIndex];
            double[] testPattern = new double[testEndIndex-trainEndIndex];
            int trainPatternIndex = 0;
            int testPatternIndex = 0;
            for (int j = 0; j < inputColumns.size(); j++) {
                if( i < trainEndIndex) {
                    trainPattern[trainPatternIndex++] = inputColumns.get(i).get(j);
                } else if(i >= trainEndIndex && i < testEndIndex){
                    testPattern[testPatternIndex++] = inputColumns.get(i).get(j);
                }
            }
            trainIps.add(new InputImpl(trainPattern));
            testIps.add(new InputImpl(testPattern));
        }

        for (int i = 0; i < outputColumns.get(0).size(); i++) {
            double[] trainPattern = new double[trainEndIndex];
            double[] testPattern = new double[testEndIndex-trainEndIndex];
            int trainPatternIndex = 0;
            int testPatternIndex = 0;
            for (int j = 0; j < inputColumns.size(); j++) {
                if( i < trainEndIndex) {
                    trainPattern[trainPatternIndex++] = inputColumns.get(i).get(j);
                } else if(i >= trainEndIndex && i < testEndIndex){
                    testPattern[testPatternIndex++] = inputColumns.get(i).get(j);
                }
            }
            trainOps.add(new ExpectedOutputImpl(trainPattern));
            testOps.add(new ExpectedOutputImpl(testPattern));
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
