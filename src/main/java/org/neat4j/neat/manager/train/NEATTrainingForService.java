package org.neat4j.neat.manager.train;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.InnovationDatabase;
import org.neat4j.neat.core.NEATConfig;
import org.neat4j.neat.core.NEATGADescriptor;
import org.neat4j.neat.core.NEATGeneticAlgorithm;
import org.neat4j.neat.core.control.NEATNetManager;
import org.neat4j.neat.core.control.NEATNetManagerForService;
import org.neat4j.neat.core.fitness.InvalidFitnessFunction;
import org.neat4j.neat.core.fitness.MSENEATFitnessFunction;
import org.neat4j.neat.core.mutators.NEATMutator;
import org.neat4j.neat.core.pselectors.TournamentSelector;
import org.neat4j.neat.core.xover.NEATCrossover;
import org.neat4j.neat.data.core.DataKeeper;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.ga.core.*;
import org.neat4j.neat.nn.core.LearningEnvironment;
import org.neat4j.neat.nn.core.NeuralNet;
import org.neat4j.neat.utils.NumberUtils;
import org.neat4j.neat.utils.RandomUtils;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;
import ru.filippov.neatexecutor.entity.ProjectConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class NEATTrainingForService implements Runnable {
    private static final Logger logger = LogManager.getLogger(NEATTrainingForService.class);


    private int numberOfThreads = 2;
    private AIConfig config;
    private List<Chromosome> bestEverChromosomes;

    protected GeneticAlgorithm geneticAlgorithm;
    protected Random random;
    protected InnovationDatabase innovationDatabase;
    private double timeSpend;

    public NEATTrainingForService(NeatConfigEntity neatConfigEntity) throws InitialisationFailedException, IOException {

        Properties prop = new Properties();

        //load a properties file from class path, inside static method
        prop.load(NEATTrainingForService.class.getClassLoader().getResourceAsStream("application.properties"));

        String maxThreadsProperty = prop.getProperty("max-threads");
        if (maxThreadsProperty != null) {
            numberOfThreads = Integer.parseInt(maxThreadsProperty);
        }


        this.initialise(neatConfigEntity);
    }


    @Override
    public void run() {
        this.evolve();
    }

    private AIConfig parseNeatSetting(List<NeatConfigEntity.NeatSetting> neatSettings) {

        AIConfig aiConfig = new NEATConfig();
        neatSettings.stream()
                .forEach(
                        neatSetting -> neatSetting
                                .getParams()
                                .stream()
                                .forEach(neatSettingValue -> aiConfig.updateConfig(neatSettingValue.getName(), neatSettingValue.getValue()))
                );
        return aiConfig;
    }


    public void initialise(NeatConfigEntity neatConfigEntity) throws InitialisationFailedException {
        try {
            AIConfig aiConfig = this.parseNeatSetting(neatConfigEntity.getNeatSettings());

            this.config = aiConfig;
            this.random = RandomUtils.setSeed((Long) aiConfig.getConfigElementByName("GENERATOR.SEED"));
            GADescriptor gaDescriptor = this.createDescriptor(aiConfig);
            this.geneticAlgorithm = this.createGeneticAlgorithm(gaDescriptor);
            this.innovationDatabase = new InnovationDatabase(this.random, this.geneticAlgorithm.pluginAllowedActivationFunctions(aiConfig));
            this.geneticAlgorithm.pluginFitnessFunction(this.createFunction(aiConfig, neatConfigEntity.getNormalizedData()));
            this.geneticAlgorithm.pluginCrossOver(new NEATCrossover());
            this.geneticAlgorithm.pluginMutator(new NEATMutator(this.random, innovationDatabase));
            this.geneticAlgorithm.pluginParentSelector(new TournamentSelector(this.random));
            this.geneticAlgorithm.createPopulation(innovationDatabase);
        } catch (InvalidFitnessFunction e) {
            logger.error("[NEATTrainingForService].initialise", e);
            throw new InitialisationFailedException(e.getMessage());
        } catch (Exception e) {
            logger.error("[NEATTrainingForService].initialise", e);
            throw new InitialisationFailedException(e.getMessage());
        }
    }

    private GeneticAlgorithm createGeneticAlgorithm(GADescriptor gaDescriptor) {
        GeneticAlgorithm ga = new NEATGeneticAlgorithm((NEATGADescriptor) gaDescriptor, this.random, this.numberOfThreads);
        return ga;
    }

    public void evolve() {

        int epochs = NumberUtils.asInt(config.getConfigElementByName("NUMBER.EPOCHS"));
        this.bestEverChromosomes = new ArrayList<>(epochs);
        double terminateVal = ((NEATGADescriptor)this.geneticAlgorithm.getDescriptor()).getErrorTerminationValue();
        boolean terminateEnabled = ((NEATGADescriptor)this.geneticAlgorithm.getDescriptor()).isToggleErrorTerminationValue();
        boolean nOrder = this.geneticAlgorithm.getDescriptor().isNaturalOrder();
        boolean terminate = false;


        int i = 0;
        Long startTime = System.currentTimeMillis();
        while (i < epochs) {
            if(Thread.interrupted()) {
                break;
            }
            logger.info("Running Epoch[" + i + "]\r");
            this.geneticAlgorithm.runEpoch();
            this.saveBest();

            if ((this.geneticAlgorithm.discoverdBestMember().fitness() >= terminateVal && !nOrder)
                    || (this.geneticAlgorithm.discoverdBestMember().fitness() <= terminateVal && nOrder)) {
                terminate = true;
            }

            i++;
            //status.setValue(((double)i)/epochs);
            this.saveDataForGUI(i);
            if(terminate && terminateEnabled) {
                //status.setValue(1);
                break;
            }

        }
        this.timeSpend = (double)(System.currentTimeMillis() - startTime) / 1000;

        logger.debug("Innovation Database Stats - Hits:" + innovationDatabase.totalHits + " - totalMisses:" + innovationDatabase.totalMisses);
    }

    private void saveDataForGUI(int i) {
        /*Chromosome best = this.ga.discoverdBestMember();
        logger.info("Save GUI[" + i + "]\r");

        this.lastTrainError = best.getTrainError();
        this.lastValidationError = best.getValidationError();
        currentEpoch = i+1;*/


        /*this.errorData.add();
        if(best.getValidationError()!=null){
            this.validationErrorData.add(this.createXYChart(i, best.getValidationError()));
        }*/


    }

    public void saveBest() {
        Chromosome best = this.geneticAlgorithm.discoverdBestMember();
        best.setInputs(NumberUtils.asInt(config.getConfigElementByName("INPUT.NODES")));
        best.setOutputs(NumberUtils.asInt(config.getConfigElementByName("OUTPUT.NODES")));

        bestEverChromosomes.add(best);
    }

    public GADescriptor createDescriptor(AIConfig config) {

        NEATGADescriptor descriptor = new NEATGADescriptor();
        descriptor.setPAddLink(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.ADDLINK")));
        descriptor.setPAddNode(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.ADDNODE")));
        descriptor.setPToggleLink(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.TOGGLELINK")));
        descriptor.setPMutateBias(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.MUTATEBIAS")));
        descriptor.setPNewActivationFunction(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.NEWACTIVATIONFUNCTION")));
        //descriptor.setPXover(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.CROSSOVER"));
        descriptor.setPMutation(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.MUTATION")));
        descriptor.setInputNodes(NumberUtils.asInt(config.getConfigElementByName("INPUT.NODES")));
        descriptor.setOutputNodes(NumberUtils.asInt(config.getConfigElementByName("OUTPUT.NODES")));
        descriptor.setNaturalOrder((Boolean) config.getConfigElementByName("NATURAL.ORDER.STRATEGY"));
        descriptor.setPopulationSize(NumberUtils.asInt(config.getConfigElementByName("POP.SIZE")));
        descriptor.setDisjointCoeff(NumberUtils.asDouble(config.getConfigElementByName("DISJOINT.COEFFICIENT")));
        descriptor.setExcessCoeff(NumberUtils.asDouble(config.getConfigElementByName("EXCESS.COEFFICIENT")));
        descriptor.setWeightCoeff(NumberUtils.asDouble(config.getConfigElementByName("WEIGHT.COEFFICIENT")));
        descriptor.setThreshold(NumberUtils.asDouble(config.getConfigElementByName("COMPATABILITY.THRESHOLD")));
        descriptor.setCompatabilityChange(NumberUtils.asDouble(config.getConfigElementByName("COMPATABILITY.CHANGE")));
        descriptor.setMaxSpecieAge(NumberUtils.asInt(config.getConfigElementByName("SPECIE.FITNESS.MAX")));
        descriptor.setSpecieAgeThreshold(NumberUtils.asInt(config.getConfigElementByName("SPECIE.AGE.THRESHOLD")));
        descriptor.setSpecieYouthThreshold(NumberUtils.asInt(config.getConfigElementByName("SPECIE.YOUTH.THRESHOLD")));
        descriptor.setAgePenalty(NumberUtils.asDouble(config.getConfigElementByName("SPECIE.OLD.PENALTY")));
        descriptor.setYouthBoost(NumberUtils.asDouble(config.getConfigElementByName("SPECIE.YOUTH.BOOST")));
        descriptor.setSpecieCount(NumberUtils.asInt(config.getConfigElementByName("SPECIE.COUNT")));
        descriptor.setPWeightReplaced(NumberUtils.asDouble(config.getConfigElementByName("PROBABILITY.WEIGHT.REPLACED")));
        descriptor.setSurvivalThreshold(NumberUtils.asDouble(config.getConfigElementByName("SURVIVAL.THRESHOLD")));
        descriptor.setFeatureSelection((Boolean) config.getConfigElementByName("FEATURE.SELECTION"));
        descriptor.setExtraFeatureCount(NumberUtils.asInt(config.getConfigElementByName("EXTRA.FEATURE.COUNT")));
        descriptor.setEleEvents((Boolean) config.getConfigElementByName("ELE.EVENTS"));
        descriptor.setEleSurvivalCount(NumberUtils.asDouble(config.getConfigElementByName("ELE.SURVIVAL.COUNT")));
        descriptor.setEleEventTime(NumberUtils.asInt(config.getConfigElementByName("ELE.EVENT.TIME")));
        descriptor.setRecurrencyAllowed((Boolean) config.getConfigElementByName("RECURRENCY.ALLOWED"));
        descriptor.setKeepBestEver((Boolean) config.getConfigElementByName("KEEP.BEST.EVER"));
        descriptor.setErrorTerminationValue(NumberUtils.asDouble(config.getConfigElementByName("TERMINATION.VALUE")));
        descriptor.setMaxPerturb(NumberUtils.asDouble(config.getConfigElementByName("MAX.PERTURB")));
        descriptor.setMaxBiasPerturb(NumberUtils.asDouble(config.getConfigElementByName("MAX.BIAS.PERTURB")));
        descriptor.setToggleErrorTerminationValue((Boolean) config.getConfigElementByName("TERMINATION.VALUE.TOGGLE"));
        return (descriptor);
    }

    public FitnessFunction createFunction(AIConfig aiConfig, ProjectConfig.NormalizedDataDto normalizedDataDto) throws InvalidFitnessFunction {
        FitnessFunction function = null;
        NEATNetManagerForService netManager;
        NeuralNet net = null;
        NetworkDataSet dataSet = null;
        NetworkDataSet testSet = null;
        LearningEnvironment env;

		try {
            netManager = new NEATNetManagerForService(aiConfig, normalizedDataDto);
            netManager.initialise(aiConfig, true);
            net = netManager.managedNet();
            env = net.netDescriptor().learnable().learningEnvironment();
            dataSet = env.getTrainDataSet();
            testSet =  env.getTestDataSet();
            function = new MSENEATFitnessFunction(net, dataSet, testSet);
		}  catch (IllegalArgumentException e) {
			throw new InvalidFitnessFunction("Invalid function class, " + function.getClass() + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
		}  catch (InitialisationFailedException e) {
			throw new InvalidFitnessFunction("Could not create Firness function, configuration was invalid:" + e.getMessage());
		}

        return (function);
    }


}