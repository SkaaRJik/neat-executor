package ru.filippov.neatexecutor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectConfig {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NormalizedDataDto {
        Double minRange;
        Double maxRange;
        List<List<Double>> data;
        List<Double> mins;
        List<Double> maxs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataIndexesDto {

        Integer trainEndIndex;


        Integer testEndIndex;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedColumnsDto {
        Integer inputs;
        Integer outputs;
        List<HashMap<String, String>> headers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PredictionParamsDto {
        Short windowSize;
        Short predictionPeriod;
    }

    private NormalizedDataDto normalizedData;

    private List<Map<String, Object>> settings;

    private DataIndexesDto dataIndexes;

    private SelectedColumnsDto selectedColumns;

    private PredictionParamsDto predictionParams;




}
