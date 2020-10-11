package ru.filippov.neatexecutor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
    public static class NormalizedDataDto implements Serializable {
        
        private Map<String, Object> normalizationServiceData;

        private List<Map<String, Object>> columns;
        
        private Integer trainEndIndex;
        
        private Integer testEndIndex;

    }

    
    private NormalizedDataDto normalizedData;

    private List<Map<String, Object>> settings;

    private Short windowSize;

    private Short predictionPeriod;




}
