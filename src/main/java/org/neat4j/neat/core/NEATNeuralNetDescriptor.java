/*
 * Created on Sep 29, 2004
 *
 */
package org.neat4j.neat.core;

import org.neat4j.neat.core.control.NEAT;
import org.neat4j.neat.nn.core.Learnable;
import org.neat4j.neat.nn.core.NeuralNetDescriptor;
import org.neat4j.neat.nn.core.NeuralNetLayerDescriptor;
import org.neat4j.neat.nn.core.NeuralNetType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author MSimmerson
 *
 * Describes the structure of the NEAT network
 */
public class NEATNeuralNetDescriptor implements NeuralNetDescriptor {
	private ArrayList layerDescriptors = new ArrayList();
	private static final NEAT netType = new NEAT();
	private int netInputs;
	private Learnable learnable;
	private boolean recurrent;

	public NEATNeuralNetDescriptor(int netInputs, Learnable learnable) {
		this(netInputs, learnable, false);
	}
	
	public NEATNeuralNetDescriptor(int netInputs, Learnable learnable, boolean recurrent) {
		this.netInputs = netInputs;
		this.learnable = learnable;
		this.recurrent = recurrent;
	}

	public void addLayerDescriptor(NeuralNetLayerDescriptor descriptor) {
		this.layerDescriptors.add(descriptor);
	}


	public NeuralNetType neuralNetType() {
		return (netType);
	}


	public int numInputs() {
		return (this.netInputs);
	}


	public Collection layerDescriptors() {
		return (this.layerDescriptors);
	}


	public Learnable learnable() {
		return (this.learnable);
	}

	/* (non-Javadoc)
	 *
	 */
	public boolean isRecurrent() {
		return (this.recurrent);
	}
}
