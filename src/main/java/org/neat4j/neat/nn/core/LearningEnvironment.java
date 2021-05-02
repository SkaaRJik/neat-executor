/*
 * Created on Sep 30, 2004
 *
 */
package org.neat4j.neat.nn.core;

import org.neat4j.neat.data.core.NetworkDataSet;

import java.io.Serializable;

/**
 * @author MSimmerson
 *
 */
public class LearningEnvironment implements Serializable {

	private NetworkDataSet trainDataSet;
	private NetworkDataSet testDataSet;

	public void setTrainDataSet(NetworkDataSet dataSet) {
		trainDataSet = dataSet;
	}

	public void setTestDataSet(NetworkDataSet dataSet) {
		testDataSet = dataSet;
	}

	public NetworkDataSet getTrainDataSet() {
		return trainDataSet;
	}

	public NetworkDataSet getTestDataSet() {
		return testDataSet;
	}
}
