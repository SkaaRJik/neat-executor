package org.neat4j.neat.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neat4j.neat.core.NEATNeuron;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeuronDto {
    private double lastActivation;
    private double bias;
    private double[] weights;
    private String activationFunction;
    private int id;
    private String type;
    private String label;

    public static NeuronDto fromNEATNeuron(NEATNeuron neatNeuron){
        return new NeuronDto(
                neatNeuron.getLastActivation(),
                neatNeuron.getBias(),
                neatNeuron.getWeights(),
                neatNeuron.getActivationFunction().getFunctionName(),
                neatNeuron.getID(),
                neatNeuron.getType().name(),
                neatNeuron.getLabel()
                );
    }


}
