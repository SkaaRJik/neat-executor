package ru.filippov.neatexecutor.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrainResult {
    private double trainError;
    private double testError;
    private long timeSpent;

}
