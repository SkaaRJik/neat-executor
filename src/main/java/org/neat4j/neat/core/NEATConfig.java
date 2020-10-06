/*
 * Created on Sep 29, 2004
 *
 */
package org.neat4j.neat.core;

import lombok.extern.log4j.Log4j2;
import org.neat4j.core.AIConfig;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Holds an object representation of the NEAT config
 * @author MSimmerson
 *
 */
@Log4j2
public class NEATConfig implements AIConfig {


	private HashMap<String, Object> config;



	public NEATConfig (NeatConfigEntity neatConfigEntity){
		this.config = new HashMap(50);
		neatConfigEntity
				.getNeatSettings()
				.stream()
				.map(neatSetting -> neatSetting.getParams())
				.forEach(neatSettingValues -> neatSettingValues
						.forEach(neatSettingValue -> this.config.put(neatSettingValue.getName(), neatSettingValue.getValue())));
	}
	
	
	
	

	//Make copy of config
	public NEATConfig (NEATConfig originalConfig){
		this.config = new HashMap(originalConfig.config);
	}

	public NEATConfig(HashMap config) {
		this.config = new HashMap(config);
	}

	// default
	public NEATConfig() {
		this.config = new HashMap();
	}

	public NEATConfig(String filePath) {
		this.config = new HashMap();
	}


	public Object configElement(String elementKey) {
		return this.config.get(elementKey);
	}

	@Override
	public void updateConfig(String elementKey, Object elementValue) {
		this.config.put(elementKey, elementValue);
	}

	@Override
	public boolean saveConfig(File dest) throws IOException {
		/*if(dest != null) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
			writer.write("");
			this.config.entrySet().stream().forEach(o -> {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) o;
				try {
					writer.append(entry.getKey() +"="+entry.getValue()+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
			writer.flush();
			writer.close();
			return true;
		} else {
			return false;
		}*/
		return false;
	}

	@Override
	public HashMap<String, Object> getMap() {
		return this.config;
	}

	public List<String> getActivationFunctionsByElementKey(String elementKey){
		return (List<String>) this.configElement(elementKey);
	}






	/*private long generatorSeed;
	private double mutationProbability;
	private double crossoverProbability;
	private double addLinkProbability;
	private double addNodeProbability;
	private double newActivationFunctionProbability;
	private double biasMutationProbability;
	private double toggleLinkProbability;
	private double replaceWeightProbability;

	private double excessCoefficient;
	private double disjointCoefficient;
	private double weightCoefficient;

	private double compatabilityThreshold;
	private double compatabilityChange;
	private int specieCount;
	private double survivalThreshold;
	private double specieAgeThreshold;
	private double specieYouthThreshold;
	private double specieOldPenalty;
	private double specieYouthBoost;
	private double specieFitnessMax;

	private double maxPertrub;
	private double maxBiasPertrub;
	private boolean featureSelection;
	private boolean recurrencyAllowed;

	private List<String> inputActivationFunctions;
	private List<String> outputActivationFunctions;
	private List<String> hiddenActivationFunctions;

	private boolean eleEvents;
	private double eleSurvivalCount;
	private int eleEventTime;

	private boolean keepBestEver;
	private int extraFeatureCount;
	private int populationSize;
	private int epochsNumber;
	private boolean terminationValueToggle;
	private double terminationValue;


	private String xoverOperator = "org.neat4j.neat.core.xover.NEATCrossover";
	private String functionOperator = "org.neat4j.neat.core.fitness.MSENEATFitnessFunction";
	private String pselectorOperator = "org.neat4j.neat.core.pselectors.TournamentSelector";
	private String mutatorOperator = "org.neat4j.neat.core.mutators.NEATMutator";
	private boolean naturalOrderStrategy = true;
	private String learnable = "org.neat4j.neat.nn.core.learning.GALearnable";
	private String aiType = "GA";

	private int outputNodes;
	private int inputNodes;*/


}
