/*
 * Created on Oct 6, 2004
 *
 */
package org.neat4j.neat.core;

import org.neat4j.neat.data.core.NetworkOutput;
import org.neat4j.neat.data.core.NetworkOutputSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MSimmerson
 *
 * Stores a output set from an NEAT neural network evaluation run.
 */
public class NEATNetOutputSet implements NetworkOutputSet {
	private static final long serialVersionUID = 1L;
	private List<NetworkOutput> outputSet;
	private int idx;
	
	public NEATNetOutputSet() {
		this.outputSet = new ArrayList<>();
		this.idx = 0;
	}

	public int size() {
		return (this.outputSet.size());
	}


	public NetworkOutput nextOutput() {
		this.idx = this.idx % this.size();
		return ((NetworkOutput)this.outputSet.get(this.idx++));		
	}

	@Override
	public boolean hasNext() {
		return this.idx < size();
	}

	@Override
	public void refresh() {
		this.idx = 0;
	}


	public void addNetworkOutput(NetworkOutput op) {
		this.outputSet.add(op);
	}

	public void removeNetworkOutput(int idx) {
		if (idx < this.size()) {
			this.outputSet.remove(idx);
		}
	}


}
