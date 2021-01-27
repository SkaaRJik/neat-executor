package org.neat4j.neat.nn.core.functions;

import org.neat4j.neat.nn.core.ActivationFunction;
import org.neat4j.neat.utils.RandomUtils;

import java.util.List;

public class ActivationFunctionContainer {
   private  List<ActivationFunctionImpl> inputActivationFunctions;
   private  List<ActivationFunctionImpl> hiddenActivationFunctions;
   private  List<ActivationFunctionImpl> outputActivationFunctions;

    public void setInputActivationFunctions(List<ActivationFunctionImpl> inputActivationFunctions) {
        this.inputActivationFunctions = inputActivationFunctions;
    }

    public void setHiddenActivationFunctions(List<ActivationFunctionImpl> hiddenActivationFunctions) {
        this.hiddenActivationFunctions = hiddenActivationFunctions;
    }

    public void setOutputActivationFunctions(List<ActivationFunctionImpl> outputActivationFunctions) {
        this.outputActivationFunctions = outputActivationFunctions;
    }

    public List<ActivationFunctionImpl> getInputActivationFunctions() {
        return inputActivationFunctions;
    }

    public List<ActivationFunctionImpl> getHiddenActivationFunctions() {
        return hiddenActivationFunctions;
    }

    public List<ActivationFunctionImpl> getOutputActivationFunctions() {
        return outputActivationFunctions;
    }

    public ActivationFunction getRandomInputActivationFunction(){
        return inputActivationFunctions.get(RandomUtils.getRand().nextInt(inputActivationFunctions.size())).newInstance();
    }

    public ActivationFunction getRandomOutputActivationFunction(){
        return outputActivationFunctions.get(RandomUtils.getRand().nextInt(outputActivationFunctions.size())).newInstance();
    }

    public ActivationFunction getRandomHiddenActivationFunction(){
        return hiddenActivationFunctions.get(RandomUtils.getRand().nextInt(hiddenActivationFunctions.size())).newInstance();
    }
}
