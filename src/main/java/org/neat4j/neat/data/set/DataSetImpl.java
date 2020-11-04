/*
 * Created on Oct 12, 2004
 *
 */
package org.neat4j.neat.data.set;

import org.neat4j.neat.data.core.ExpectedOutputSet;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.data.core.NetworkInputSet;

/**
 * @author MSimmerson
 *
 */
public class DataSetImpl implements NetworkDataSet {

	private NetworkInputSet inputSet;
	private ExpectedOutputSet expectedOutputSet;
	
	public DataSetImpl() {
		// deliberate empty constructor
	}
	
	public DataSetImpl(NetworkInputSet inputSet, ExpectedOutputSet expectedOutputSet) {
		this.inputSet = inputSet;
		this.expectedOutputSet = expectedOutputSet;
	}

	public NetworkInputSet inputSet() {
		return (this.inputSet);
	}


	public ExpectedOutputSet expectedOutputSet() {
		return (this.expectedOutputSet);
	}
}
