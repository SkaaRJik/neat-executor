package org.neat4j.neat.manager.prediction;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.filippov.neatexecutor.entity.WindowPredictionResult;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class PredictionResultCsvConverter {

    public byte[] convert(WindowPredictionResult windowPredictionResult) {
        List<Map<String, Object>> factorSigns = windowPredictionResult.getFactorSigns();
        List<Map<String, Object>> targetSigns = windowPredictionResult.getTargetSigns();



        StringBuilder stringBuilder = new StringBuilder(factorSigns.get(0).get("name").toString());
        for (int i = 1; i < factorSigns.size(); i++) {
            stringBuilder.append(";");
            stringBuilder.append(factorSigns.get(i).get("name"));
        }
        for (int i = 0; i < targetSigns.size(); i++) {
            stringBuilder.append(";");
            stringBuilder.append(targetSigns.get(i).get("name"));
        }

        stringBuilder.append("\n");

        int maxRowIndex = ((List<Double>)targetSigns.get(0).get("data")).size();

        for (int i = 0; i < maxRowIndex; i++) {
            for (int j = 0; j < factorSigns.size(); j++) {
                stringBuilder.append(((List<Double>)factorSigns.get(j).get("data")).get(i));
                stringBuilder.append(";");
            }
            for (int j = 0; j < targetSigns.size(); j++) {
                stringBuilder.append(((List<Double>)targetSigns.get(j).get("data")).get(i));
                if(j+1 < targetSigns.size()){
                    stringBuilder.append(";");
                }
            }
            stringBuilder.append("\n");
        }



        return stringBuilder.toString().getBytes();
    }


}
