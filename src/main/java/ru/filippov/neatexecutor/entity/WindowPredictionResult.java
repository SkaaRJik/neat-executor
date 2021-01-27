package ru.filippov.neatexecutor.entity;

import java.util.List;
import java.util.Map;

public class WindowPredictionResult {

    long timeSpent;
    Double predictionError;
    List<Map<String, Object>> factorSigns;
    List<Map<String, Object>> targetSigns;
    
    public WindowPredictionResult(long timeSpent, Double predictionError, List<Map<String, Object>> factorSigns, List<Map<String, Object>> targetSigns) {
        this.timeSpent = timeSpent;
        this.predictionError = predictionError;
        this.factorSigns = factorSigns;
        this.targetSigns = targetSigns;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Double getPredictionError() {
        return predictionError;
    }

    public void setPredictionError(Double predictionError) {
        this.predictionError = predictionError;
    }

    public List<Map<String, Object>> getFactorSigns() {
        return factorSigns;
    }

    public void setFactorSigns(List<Map<String, Object>> factorSigns) {
        this.factorSigns = factorSigns;
    }

    public List<Map<String, Object>> getTargetSigns() {
        return targetSigns;
    }

    public void setTargetSigns(List<Map<String, Object>> targetSigns) {
        this.targetSigns = targetSigns;
    }

    @Override
    public String toString() {
        return "WindowPredictionResult{" +
                "timeSpent=" + timeSpent +
                ", predictionError=" + predictionError +
                ", factorSigns=" + factorSigns +
                ", targetSigns=" + targetSigns +
                '}';
    }
}
