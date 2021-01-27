package org.neat4j.neat.utils;

import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.core.NEATNeuron;
import org.neat4j.neat.core.NEATNodeGene;
import org.neat4j.neat.ga.core.Chromosome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class NetTopologyAnalyzer implements Callable<NetTopology> {

    private NEATNeuralNet net;

    public NetTopologyAnalyzer(Chromosome chromosome) throws InitialisationFailedException {
       this.net = (NEATNeuralNet) NEATNeuralNet.createNet(chromosome);
    }

    private List<List<NeuronDto>> analyseNeuronStructure(NEATNeuralNet net) {
        int maxDepth = 1;
        int maxWidth = 0;
        int i;
        int row = 0;
        int col = 0;
        List<NEATNeuron> neurons = net.getNeurons();

        NEATNeuron neuron;

        // will only need the first few entries, but htis will cope with wierd structures
        int[] nDepthWidth = new int[neurons.size()];
        int inputs = net.netDescriptor().numInputs();

        for (i = 0; i < neurons.size(); i++) {
            if (neurons.get(i).neuronDepth() >= 0 && neurons.get(i).neuronType() != NEATNodeGene.TYPE.INPUT) {
                if (neurons.get(i).neuronType() == NEATNodeGene.TYPE.OUTPUT) {
                    nDepthWidth[0]++;
                } else if (neurons.get(i).neuronType() == NEATNodeGene.TYPE.HIDDEN) {
                    if (neurons.get(i).neuronDepth() > (maxDepth - 1)) {
                        maxDepth = neurons.get(i).neuronDepth() + 1;
                    }
                    nDepthWidth[neurons.get(i).neuronDepth()]++;
                }
                if (nDepthWidth[neurons.get(i).neuronDepth()] > maxWidth) {
                    maxWidth = nDepthWidth[neurons.get(i).neuronDepth()];
                }
            }
        }
        // and one for the inputs
        maxDepth++;
        // ensure array is wide enough
        if (inputs > maxWidth) {
            maxWidth = inputs;
        }

        List<List<NEATNeuron>> neuronStructure = new ArrayList<>(maxDepth);
        for (int j = 0; j < maxDepth; j++) {
            neuronStructure.add(new ArrayList<>(maxWidth));
        }


        nDepthWidth = new int[neurons.size()];

        for (i = 0; i < neurons.size(); i++) {
            neuron = neurons.get(i);
            if (neuron.neuronDepth() >= 0) {
                if(neuron.neuronType() == NEATNodeGene.TYPE.INPUT){
                    row = 0;
                } else {
                    row = maxDepth - 1 - neuron.neuronDepth();
                }

                neuronStructure.get(row).add(neuron);
            }
        }

        return neuronStructure
                .stream()
                .map(neatNeurons -> neatNeurons.stream()
                        .map(NeuronDto::fromNEATNeuron)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<NeuronConnection> analyseNeuronConnections(NEATNeuralNet net) {

        return Arrays.stream(net.getConnections())
                .map(NeuronConnection::fromSynapse)
                .collect(Collectors.toList());


    }

    @Override
    public NetTopology call() throws Exception {
        List<List<NeuronDto>> neuronsLayers = this.analyseNeuronStructure(this.net);
        List<NeuronConnection> neuronConnections = this.analyseNeuronConnections(this.net);
        return new NetTopology(neuronsLayers, neuronConnections);
    }
}
