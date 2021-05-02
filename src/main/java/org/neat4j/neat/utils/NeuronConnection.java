package org.neat4j.neat.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.neat4j.neat.nn.core.Synapse;

@Data
@AllArgsConstructor
public class NeuronConnection {

    private int fromId;
    private int toId;
    private double weight;
    private boolean enabled;

    public static NeuronConnection fromSynapse(Synapse synapse){
        return new NeuronConnection(synapse.getFrom().getID(), synapse.getTo().getID(), synapse.getWeight(), synapse.isEnabled());
    }

}
