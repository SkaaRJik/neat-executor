package org.neat4j.neat.applications.core;

import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.nn.core.NeuralNet;

/**
 * Provides bare bones NEAT application
 * @author MSimmerson
 *
 */
public abstract class NEATApplicationEngine implements ApplicationEngine {
	protected static final String GA = "GA";
	protected static final String NN = "NN";
	protected NeuralNet net;
	protected NetworkDataSet netData;

	/**
	 * @see org.neat4j.ailibrary.core.AIController#initialise(org.neat4j.ailibrary.core.AIConfig)
	 */
	public abstract void initialise(AIConfig config, boolean loadData) throws InitialisationFailedException;
	public abstract NeuralNet createNet(AIConfig config) throws InitialisationFailedException;




	public NeuralNet net() {
		return (this.net);
	}

	public NetworkDataSet netData() {
		return (this.netData);
	}

	public void setNetData(NetworkDataSet netData) {
		this.netData = netData;
	}

	public void setNet(NeuralNet net) {
		this.net = net;
	}
}
