package ru.filippov.neatexecutor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ExperimentConfigEntity {

    private Long experimentId;
    private Long projectId;
    private String username;
    private String projectFolder;
    private String dataFilename;
    private List<ColumnsDto> columns;
    private Integer trainEndIndex;
    private Integer testEndIndex;
    private List<NeatSetting> neatSettings;
    private Short predictionWindowSize;
    private Short predictionPeriod;
    @ToString.Exclude
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
