/*
 * Created on Oct 6, 2004
 *
 */
package org.neat4j.neat.data.set;

import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkInputSet;

import java.util.List;

/**
 * @author MSimmerson
 *
 */
public class InputSetImpl implements NetworkInputSet {


	private List<String> headers;
	private List<NetworkInput> inputs;
	private int idx;
	
	public InputSetImpl(List<String> headers , List<NetworkInput> inputs) {
		this.headers = headers;
		this.inputs = inputs;
		this.idx = 0;
	}

	public int size() {
		return (this.inputs.size());
	}


	public NetworkInput nextInput() {
		this.idx = this.idx % this.size();
		return ((NetworkInput)this.inputs.get(idx++));		
	}
	/* (non-Javadoc)
	 *
	 */
	public NetworkInput inputAt(int idx) {
		return ((NetworkInput)this.inputs.get(idx));		
	}
	/* (non-Javadoc)
	 *
	 */
	public void removeInputAt(int idx) {
		if (idx < this.size()) {
			this.inputs.remove(idx);
		}
	}

	@Override
	public List<String> getHeaders() {
		return this.headers;
	}

	@Override
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
}
