package org.neat4j.neat.applications.train;

import org.apache.log4j.Category;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.InnovationDatabase;
import org.neat4j.neat.core.NEATGADescriptor;
import org.neat4j.neat.core.NEATGeneticAlgorithm;
import org.neat4j.neat.core.control.NEATNetManager;
import org.neat4j.neat.core.fitness.InvalidFitnessFunction;
import org.neat4j.neat.core.fitness.MSENEATFitnessFunction;
import org.neat4j.neat.core.mutators.NEATMutator;
import org.neat4j.neat.core.pselectors.InvalidParentSelectorFunction;
import org.neat4j.neat.core.pselectors.TournamentSelector;
import org.neat4j.neat.core.xover.NEATCrossover;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.ga.core.*;
import org.neat4j.neat.nn.core.LearningEnvironment;
import org.neat4j.neat.nn.core.NeuralNet;
import org.neat4j.neat.utils.MathUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * Training control for a NEAT network based on given configuration.
 * @author MSimmerson
 *
 */
public class NEATGATrainingManager {
	private static final Category cat = Category.getInstance(NEATGATrainingManager.class);
	protected GeneticAlgorithm ga;
	protected AIConfig config;
	protected Random random;
	protected InnovationDatabase innovationDatabase;


	public GeneticAlgorithm getGeneticAlgorithm() {
		return (this.ga);
	}
	/**
	 *
	 */
	public void initialise(AIConfig config) throws InitialisationFailedException {
		MathUtils.initRandom((long) config.configElement("GENERATOR.SEED"));
		this.random = MathUtils.getRand();
		GADescriptor gaDescriptor = this.createDescriptor(config);
		this.assigGA(this.createGeneticAlgorithm(gaDescriptor));
		try {
			this.assignConfig(config);
			innovationDatabase = new InnovationDatabase(this.random, this.ga.pluginAllowedActivationFunctions(config));
			this.ga.pluginFitnessFunction(this.createFunction());
			this.ga.pluginCrossOver(new NEATCrossover());
			this.ga.pluginMutator(new NEATMutator(this.random, innovationDatabase));
			this.ga.pluginParentSelector(new TournamentSelector(this.random));
			this.ga.createPopulation(innovationDatabase);
		} catch (InvalidFitnessFunction e) {
			throw new InitialisationFailedException(e.getMessage());
		}  catch (Exception e) {
			e.printStackTrace();
			throw new InitialisationFailedException(e.getMessage());
		}
	}

	public void assigGA(GeneticAlgorithm ga) {
		this.ga = ga;
	}

	public void assignConfig(AIConfig config) {
		this.config = config;
	}

	/**
	 * Initiates an evaluation and evolution cycle.
	 *
	 */
	public void evolve() {
		int epochs = (int)  config.configElement("NUMBER.EPOCHS");
		double errorValueToTerminate = ((NEATGADescriptor)this.ga.getDescriptor()).getErrorTerminationValue();
		boolean nOrder = this.ga.getDescriptor().isNaturalOrder();
		boolean terminate = false;
		int i = 0;

		while (i < epochs /*&& !terminate*/) {
			cat.info("Running Epoch[" + i + "]\r");
			this.ga.runEpoch();
			this.saveBest();
			if ((this.ga.discoverdBestMember().fitness() >= errorValueToTerminate && !nOrder) || (this.ga.discoverdBestMember().fitness() <= errorValueToTerminate && nOrder)) {
				terminate = true;
			}
			i++;
		}
		cat.debug("Innovation Database Stats - Hits:" + innovationDatabase.getTotalHits() + " - totalMisses:" + innovationDatabase.getTotalMisses());
	}

	/**
	 * Saves the best candidate of the generation
	 *
	 */
	//TODO Make saving
	public void saveBest() {
		/*String pathToSave = config.configElement("SAVE.LOCATION");
		Chromosome chromosome = this.ga.discoverdBestMember();
		chromosome.setInputs((int)  config.configElement("INPUT.NODES"));
		chromosome.setOutputs((int)  config.configElement("OUTPUT.NODES"));
		this.save(pathToSave, chromosome);*/
	}

	/**
	 *
	 */
	public GADescriptor createDescriptor(AIConfig config) {

		NEATGADescriptor descriptor = new NEATGADescriptor();
		descriptor.setPAddLink((double)  config.configElement("PROBABILITY.ADDLINK"));
		descriptor.setPAddNode((double)  config.configElement("PROBABILITY.ADDNODE"));
		descriptor.setPToggleLink((double)  config.configElement("PROBABILITY.TOGGLELINK"));
		descriptor.setPMutateBias((double)  config.configElement("PROBABILITY.MUTATEBIAS"));
		descriptor.setPNewActivationFunction((double)  config.configElement("PROBABILITY.NEWACTIVATIONFUNCTION"));
		//descriptor.setPXover((double)  config.configElement("PROBABILITY.CROSSOVER"));
		descriptor.setPMutation((double)  config.configElement("PROBABILITY.MUTATION"));
		descriptor.setInputNodes((int)  config.configElement("INPUT.NODES"));
		descriptor.setOutputNodes((int)  config.configElement("OUTPUT.NODES"));
		descriptor.setNaturalOrder((boolean)  config.configElement("NATURAL.ORDER.STRATEGY"));
		descriptor.setPopulationSize((int)  config.configElement("POP.SIZE"));
		descriptor.setDisjointCoeff((double)  config.configElement("DISJOINT.COEFFICIENT"));
		descriptor.setExcessCoeff((double)  config.configElement("EXCESS.COEFFICIENT"));
		descriptor.setWeightCoeff((double)  config.configElement("WEIGHT.COEFFICIENT"));
		descriptor.setThreshold((double)  config.configElement("COMPATABILITY.THRESHOLD"));
		descriptor.setCompatabilityChange((double)  config.configElement("COMPATABILITY.CHANGE"));
		descriptor.setMaxSpecieAge((int)  config.configElement("SPECIE.FITNESS.MAX"));
		descriptor.setSpecieAgeThreshold((int)  config.configElement("SPECIE.AGE.THRESHOLD"));
		descriptor.setSpecieYouthThreshold((int)  config.configElement("SPECIE.YOUTH.THRESHOLD"));
		descriptor.setAgePenalty((double)  config.configElement("SPECIE.OLD.PENALTY"));
		descriptor.setYouthBoost((double)  config.configElement("SPECIE.YOUTH.BOOST"));
		descriptor.setSpecieCount((int)  config.configElement("SPECIE.COUNT"));
		descriptor.setPWeightReplaced((double)  config.configElement("PROBABILITY.WEIGHT.REPLACED"));
		descriptor.setSurvivalThreshold((double)  config.configElement("SURVIVAL.THRESHOLD"));
		descriptor.setFeatureSelection((boolean)  config.configElement("FEATURE.SELECTION"));
		descriptor.setExtraFeatureCount((int)  config.configElement("EXTRA.FEATURE.COUNT"));
		descriptor.setEleEvents((boolean)  config.configElement("ELE.EVENTS"));
		descriptor.setEleSurvivalCount((double)  config.configElement("ELE.SURVIVAL.COUNT"));
		descriptor.setEleEventTime((int)  config.configElement("ELE.EVENT.TIME"));
		descriptor.setRecurrencyAllowed((boolean)  config.configElement("RECURRENCY.ALLOWED"));
		descriptor.setKeepBestEver((boolean)  config.configElement("KEEP.BEST.EVER"));
		descriptor.setErrorTerminationValue((double)  config.configElement("TERMINATION.VALUE"));
		descriptor.setMaxPerturb((double)  config.configElement("MAX.PERTURB"));
		descriptor.setMaxBiasPerturb((double)  config.configElement("MAX.BIAS.PERTURB"));
		descriptor.setToggleErrorTerminationValue((boolean) config.configElement("TERMINATION.VALUE.TOGGLE"));

		return (descriptor);
	}


	/**
	 * Creates a GA for NEAT evolution based on the getDescriptor
	 * @param gaDescriptor
	 * @return created GA
	 */
	public GeneticAlgorithm createGeneticAlgorithm(GADescriptor gaDescriptor) {
		GeneticAlgorithm ga = new NEATGeneticAlgorithm((NEATGADescriptor)gaDescriptor, this.random, 5);
		return (ga);
	}

	/**
	 *
	 */
	public FitnessFunction createFunction() throws InvalidFitnessFunction {
		FitnessFunction function = null;
		AIConfig nnConfig;
		NEATNetManager netManager;
		NeuralNet net = null;
		NetworkDataSet dataSet = null;
		NetworkDataSet testSet = null;
		LearningEnvironment env;

		try {

				/*nnConfig = new NEATConfig();
				//nnConfig  = new NEATLoader().loadConfig(nnConfigFile);
				nnConfig.updateConfig("INPUT_SIZE", config.configElement("INPUT.NODES"));
				nnConfig.updateConfig("OUTPUT_SIZE", config.configElement("OUTPUT.NODES"));
				nnConfig.updateConfig("LEARNABLE", config.configElement("LEARNABLE"));*/
				/*if(!config.configElement("TRAINING.SET").matches("/"))
					config.updateConfig("TRAINING.SET", config.configElement("CONFIGURATION.FILEPATH")+"/"+config.configElement("TRAINING.SET"));
				else
					config.updateConfig("TRAINING.SET", config.configElement("TRAINING.SET"));
				*/
				netManager = new NEATNetManager();
				netManager.initialise(config, true);
				net = netManager.managedNet();
				env = net.netDescriptor().learnable().learningEnvironment();
				dataSet = (NetworkDataSet)env.learningParameter("TRAINING.SET");
				testSet = (NetworkDataSet)env.learningParameter("TEST.SET");
				function = new MSENEATFitnessFunction(net, dataSet, testSet);

		}  catch (IllegalArgumentException e) {
			throw new InvalidFitnessFunction("Invalid function class, " + function.getClass() + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
		}  catch (InitialisationFailedException e) {
			e.printStackTrace();
			throw new InvalidFitnessFunction("Could not create Firness function, configuration was invalid:" + e.getMessage());
		}

		return (function);
	}


	public ParentSelector createParentSelector(AIConfig config) throws InvalidParentSelectorFunction {
		String pSelectorClass = (String) config.configElement("OPERATOR.PSELECTOR");
		ParentSelector pSelector;

		if (pSelectorClass != null) {
			try {
				pSelector = (ParentSelector) Class.forName(pSelectorClass).newInstance();
			} catch (InstantiationException e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			} catch (Exception e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			}
		} else {
			throw new InvalidParentSelectorFunction("Parent Selector class was null");
		}

		return (pSelector);
	}








	public boolean save(String fileName, Chromosome genoType) {
		boolean saveOk = false;
		ObjectOutputStream s = null;
		FileOutputStream out = null;
		try {
			if (fileName != null)
			//System.out.println("Saving Best Chromosome to " + fileName);
			out = new FileOutputStream(fileName);
			s = new ObjectOutputStream(out);
			s.writeObject(genoType);
			s.flush();
			saveOk = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null) {
					s.close();
				}

				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		//System.out.println("Saving Best Chromosome...Done");
		return (saveOk);
	}


}
