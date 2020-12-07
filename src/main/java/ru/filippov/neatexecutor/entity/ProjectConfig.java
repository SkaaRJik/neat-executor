package ru.filippov.neatexecutor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectConfig {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NormalizedDataDto implements Serializable {
        
        private Map<String, Object> normalizationServiceData;

        private List<ColumnsDto> columns;
        
        private Integer trainEndIndex;
        
        private Integer testEndIndex;

    }

    
    private NormalizedDataDto normalizedData;

    private List<Map<String, Object>> settings;

    private Short windowSize;

    private Short predictionPeriod;




}
