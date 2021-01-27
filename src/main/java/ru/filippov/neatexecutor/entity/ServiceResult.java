package ru.filippov.neatexecutor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neat4j.neat.utils.NetTopology;

import java.util.Map;

@Data
@NoArgsConstructor
public class ServiceResult {
    private Long configId;
    private TrainResult train;
    private WindowPredictionResult prediction;
    private NetTopology model;

    public ServiceResult(Long configId, TrainResult trainResult, NetTopology netTopology, WindowPredictionResult windowPredictionResult) {
        this.configId = configId;
        this.train = trainResult;
        this.model = netTopology;
        this.prediction = windowPredictionResult;
    }


}
