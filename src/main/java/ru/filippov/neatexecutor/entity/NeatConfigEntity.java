package ru.filippov.neatexecutor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeatConfigEntity {

    private Long neatConfigId;
    private Long projectId;
    private String dataFilename;
    private List<ColumnsDto> columns;
    private Integer trainEndIndex;
    private Integer testEndIndex;
    private List<NeatSetting> neatSettings;
    private Short predictionWindowSize;
    private Short predictionPeriod;
    private ProjectConfig.NormalizedDataDto normalizedData;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NeatSetting {
        @JsonProperty("params")
        private List<NeatSettingValue> params;

        public List<NeatSettingValue> getParams() {
            return params;
        }

        public void setParams(List<NeatSettingValue> params) {
            this.params = params;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NeatSettingValue {
        @JsonProperty("name")
        private String name;
        @JsonProperty("value")
        private Object value;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }











}
