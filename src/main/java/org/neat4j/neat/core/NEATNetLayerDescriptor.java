/*
 * Created on Sep 29, 2004
 *
 */
package org.neat4j.neat.core;

import org.neat4j.neat.nn.core.ActivationFunction;
import org.neat4j.neat.nn.core.NeuralNetLayerDescriptor;

/**
 * @author MSimmerson
 *
 */
public class NEATNetLayerDescriptor implements NeuralNetLayerDescriptor {
	private int layerSize;
	private int layerInputSize;
	private int layerId;
	private ActivationFunction function;
	private boolean isOutputLayer = false;
	private boolean nodesSelfRecurrent;
	
	public NEATNetLayerDescriptor(ActivationFunction function, int layerSize, int layerInputsSize, int id) {
		this.layerSize = layerSize;
		this.layerId = id;
		this.function = function;
		this.layerInputSize = layerInputsSize;
	}
	
	public NEATNetLayerDescriptor(ActivationFunction function, int layerSize, int layerInputsSize, int id, boolean opLayer, boolean selfRecurrent) {
		this(function, layerSize, layerInputsSize, id);
		this.isOutputLayer = opLayer;
		this.nodesSelfRecurrent = selfRecurrent;
	}

	public int layerSize() {
		return this.layerSize;
	}


	public int layerId() {
		return (this.layerId);
	}


	public ActivationFunction activationFunction() {
		return (this.function);
	}

	public int inputsIntoLayer() {
		return (this.layerInputSize);
	}


	public boolean isOutputLayer() {
		return (this.isOutputLayer);
	}

	/* (non-Javadoc)
	 *
	 */
	public boolean nodesSelfRecurrent() {
		return (this.nodesSelfRecurrent);
	}
}
