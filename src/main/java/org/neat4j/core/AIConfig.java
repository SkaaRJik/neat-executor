/*
 * Created on Sep 27, 2004
 *
 */
package org.neat4j.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author MSimmerson
 *
 */
public interface AIConfig {

	/**
	 * Allows access to given configuration getNetOutputs
	 * @param elementKey
	 * @return The configuration value for the given key
	 */
	public String configElement(String elementKey);
	/**
	 * Allows updating of configuration getNetOutputs
	 * @param elementKey
	 * @param elementValue
	 */
	public void updateConfig(String elementKey, String elementValue);

	boolean saveConfig(File file) throws IOException;

	HashMap getMap();

	/*long getGeneratorSeed();
	double getMutationProbability();
	double getCrossoverProbability();
	double getAddLinkProbability();
	double getAddNodeProbability();
	double getNewActivationFunctionProbability();
	double getBiasMutationProbability();
	double getToggleLinkProbability();
	double getReplaceWeightProbability();

	double getExcessCoefficient();
	double getDisjointCoefficient();
	double getWeightCoefficient();

	double getCompatabilityThreshold();
	double getCompatabilityChange();
	int getSpecieCount();
	double getSurvivalThreshold();
	double getSpecieAgeThreshold();
	double getSpecieYouthThreshold();
	double getSpecieOldPenalty();
	double getSpecieYouthBoost();
	double getSpecieFitnessMax();

	double getMaxPertrub();
	double getMaxBiasPertrub();
	boolean getFeatureSelection();
	boolean getRecurrencyAllowed();

	List<String> getInputActivationFunctions();
	List<String> getOutputActivationFunctions();
	List<String> getHiddenActivationFunctions();

	boolean getEleEvents();
	double getEleSurvivalCount();
	int getEleEventTime();

	boolean getKeepBestEver();
	int getExtraFeatureCount();
	int getPopulationSize();
	int getEpochsNumber();
	boolean getTerminationValueToggle();
	double getTerminationValue();


	String getXoverOperator();
	String getFunctionOperator();
	String getPselectorOperator();
	String getMutatorOperator();
	boolean getNaturalOrderStrategy();
	String getLearnable();
	String getAiType();

	int getOutputNodes();
	int getInputNodes();*/


}
