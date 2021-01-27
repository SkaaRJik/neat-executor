/*
 * Created on 23-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.neat4j.neat.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.neat.nn.core.ActivationFunction;
import org.neat4j.neat.nn.core.Neuron;
import org.neat4j.neat.nn.core.Synapse;

import java.util.ArrayList;

/**
 * @author MSimmerson
 *
 * Specific NEAT neuron
 */
public class NEATNeuron implements Neuron {
	@JsonIgnore
	private static final Logger logger = LogManager.getLogger(NEATNeuron.class);

	private double lastActivation;
	private double bias;
	private double[] weights;
	private ActivationFunction activationFunction;
	private int id;
	private NEATNodeGene.TYPE type;
	private int depth;
	private String label;
	@JsonIgnore
	private ArrayList<NEATNeuron> sourceNeurons;
	@JsonIgnore
	private ArrayList<Synapse> incomingSynapses;
	@JsonIgnore
	private ArrayList<Synapse> outSynapses;

	public NEATNeuron(ActivationFunction function, int id, NEATNodeGene.TYPE type, String label) {
		this.activationFunction = function;
		this.id = id;
		this.type = type;
		this.sourceNeurons = new ArrayList<>();
		this.incomingSynapses = new ArrayList<>();
		this.outSynapses = new ArrayList<>();
		this.depth = -1;
		this.label = label;
	}
	
	public void addSourceNeuron(NEATNeuron neuron) {
		this.sourceNeurons.add(neuron);
	}
	
	public void addIncomingSynapse(Synapse synapse) {
		this.incomingSynapses.add(synapse);
	}
	
	public ArrayList<Synapse> incomingSynapses() {
		return (this.incomingSynapses);
	}
	
	public ArrayList<NEATNeuron> sourceNeurons() {
		return (this.sourceNeurons);
	}
	
	public double lastActivation() {
		return (this.lastActivation);
	}
	
	/**
	 * If it is an input neuron, returns the input, else will run through the specified activation function.
	 * 
	 */
	public double activate(double[] nInputs) {
		double neuronIp = 0;
		int i = 0;
		double weight;
		double input;
		Synapse synapse;
		// acting as a bias neuron
		this.lastActivation = -1;
		try{


		if (this.type != NEATNodeGene.TYPE.INPUT) {
			if (nInputs.length > 0) {
				for (i = 0; i < nInputs.length; i++) {
					input = nInputs[i];
					synapse = this.incomingSynapses.get(i);
					if (synapse.isEnabled()) {
						weight = synapse.getWeight();
						neuronIp += (input * weight);
					}
				}
				neuronIp += (-1 * this.bias);
				this.lastActivation = this.activationFunction.activate(neuronIp);
			}
		} else {
			//neuronIp = nInputs[0];
			this.lastActivation = nInputs[0];
		}
		} catch (IndexOutOfBoundsException e) {
			logger.error(this.incomingSynapses);
			throw e;
		}
		
		return (this.lastActivation);
	}

	public ActivationFunction function() {
		return (this.activationFunction);
	}

	public void modifyWeights(double[] weightMods, double[] momentum, boolean mode) {
		System.arraycopy(weightMods, 0, this.weights, 0, this.weights.length);

	}

	public void modifyBias(double biasMod, double momentum, boolean mode) {
		this.bias = biasMod;
	}

	public double[] weights() {
		return (this.weights);
	}

	public double bias() {
		return (this.bias);
	}

	public double[] lastWeightDeltas() {
		return null;
	}

	public double lastBiasDelta() {
		return 0;
	}

	@Override
	public int getID() {
		return this.id;
	}

	public NEATNodeGene.TYPE neuronType() {
		return (this.type);
	}
	
	public int neuronDepth() {
		return (this.depth);
	}

	public void setNeuronDepth(int depth) {
		this.depth = depth;
	}

	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return "NEATNeuron{" +
				"\n  id=" + id +
				"\n  type=" + type +
				"\n  label='" + label  +
				"\n  activationFunction=" + activationFunction.getFunctionName() +
				"\n  bias=" + bias +
				"\n}";
	}


	public ArrayList<Synapse> getOutSynapses() {
		return outSynapses;
	}

	public void addOutSynapse(Synapse synapse){
		this.outSynapses.add(synapse);
	}

	public static Logger getLogger() {
		return logger;
	}

	public double getLastActivation() {
		return lastActivation;
	}

	public void setLastActivation(double lastActivation) {
		this.lastActivation = lastActivation;
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	public double[] getWeights() {
		return weights;
	}

	public void setWeights(double[] weights) {
		this.weights = weights;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NEATNodeGene.TYPE getType() {
		return type;
	}

	public void setType(NEATNodeGene.TYPE type) {
		this.type = type;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<NEATNeuron> getSourceNeurons() {
		return sourceNeurons;
	}

	public void setSourceNeurons(ArrayList<NEATNeuron> sourceNeurons) {
		this.sourceNeurons = sourceNeurons;
	}

	public ArrayList<Synapse> getIncomingSynapses() {
		return incomingSynapses;
	}

	public void setIncomingSynapses(ArrayList<Synapse> incomingSynapses) {
		this.incomingSynapses = incomingSynapses;
	}

	public void setOutSynapses(ArrayList<Synapse> outSynapses) {
		this.outSynapses = outSynapses;
	}
}
