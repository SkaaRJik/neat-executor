/*
 * Created on Oct 6, 2004
 *
 */
package org.neat4j.neat.data.set;

import org.neat4j.neat.data.core.ExpectedOutputSet;
import org.neat4j.neat.data.core.NetworkOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MSimmerson
 *
 */
public class ExpectedOutputSetImpl implements ExpectedOutputSet {

	List<String> headers;

	private List<NetworkOutput> ops;
	private int idx;
	
	public ExpectedOutputSetImpl(List<String> headers, List<NetworkOutput> eOps) {
		this.idx = 0;
		this.ops = eOps;
		this.headers = headers;
	}

	public int size() {
		return (this.ops.size());
	}

	/**
	 * Wraps round to beginning
	 *
	 */
	public NetworkOutput nextOutput() {
		this.idx = this.idx % this.size();
		return ((NetworkOutput)this.ops.get(this.idx++));		
	}

	@Override
	public void refresh() {
		this.idx = 0;
	}


	public void addNetworkOutput(NetworkOutput op) {
		this.ops.add(op);
	}
	/* (non-Javadoc)
	 *
	 */
	public NetworkOutput outputAt(int idx) {
		return ((NetworkOutput)this.ops.get(idx));		
	}

	@Override
	public List<String> getHeaders() {
		return this.headers;
	}

	@Override
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	/* (non-Javadoc)
	 *
	 */
	public void removeNetworkOutput(int idx) {
		if (idx < this.size()) {
			this.ops.remove(idx);
		}
	}

	@Override
	public boolean hasNext() {
		return this.idx < size();
	}


}
