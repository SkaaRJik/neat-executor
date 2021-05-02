package ru.filippov.neatexecutor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neat4j.neat.utils.NetTopology;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResult {
    private Long experimentId;
    private List<Double> trainErrors;
    private List<Double> testErrors;
    private Double predictionError;
    private String predictionResultFile;
    Map<String, List<Map<String,Object>>> windowTrainStatistic;
    private NetTopology model;
    private String status;
}
