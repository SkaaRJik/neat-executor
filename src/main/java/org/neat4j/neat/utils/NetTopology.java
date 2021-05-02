package org.neat4j.neat.utils;

import java.util.List;

public class NetTopology {

    private List<List<NeuronDto>> neuronsLayers;
    private List<NeuronConnection> connections;


    public NetTopology(List<List<NeuronDto>> neuronsLayers, List<NeuronConnection> connections) {
        this.neuronsLayers = neuronsLayers;
        this.connections = connections;
    }

    public List<List<NeuronDto>> getNeuronsLayers() {
        return neuronsLayers;
    }

    public void setNeuronsLayers(List<List<NeuronDto>> neuronsLayers) {
        this.neuronsLayers = neuronsLayers;
    }

    public List<NeuronConnection> getConnections() {
        return connections;
    }

    public void setConnections(List<NeuronConnection> connections) {
        this.connections = connections;
    }
}
