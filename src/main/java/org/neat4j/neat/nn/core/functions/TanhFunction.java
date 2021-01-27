/*
 * Created on Sep 30, 2004
 *
 */
package org.neat4j.neat.nn.core.functions;

import org.neat4j.neat.nn.core.ActivationFunction;

/**
 * @author MSimmerson
 * @fixed Filippov
 */
public class TanhFunction extends ActivationFunctionImpl {

	private static String functionName = "tanh(x)";

	public TanhFunction(double factor) {
		this.factor = factor;
	}

	public TanhFunction() {
		this.factor = 1;
	}


	public double activate(double neuronIp) {
		double op;
		op = th(neuronIp/this.factor);
		return (op);
	}

	public double th(double neuronIp){
		return (Math.exp(neuronIp)-Math.exp(-neuronIp))/(Math.exp(neuronIp)+Math.exp(-neuronIp));
	}

	public double derivative(double neuronIp) {
		double deriv = 0;
		deriv = 1 - Math.pow(th(neuronIp/this.factor), 2);
		return (deriv);
	}

	@Override
	public ActivationFunction newInstance() {
		return new TanhFunction(this.factor);
	}

	public static String getStaticFunctionName(){
		return functionName;
	}

	public String getFunctionName(){
		return functionName;
	}
}
