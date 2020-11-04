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
import org.neat4j.neat.core.fitness.InvalidFitnessFunction;
import org.neat4j.neat.data.core.DataKeeper;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.ga.core.FitnessFunction;
import org.neat4j.neat.ga.core.GADescriptor;
import org.neat4j.neat.ga.core.GeneticAlgorithm;
import org.neat4j.neat.nn.core.LearningEnvironment;
import org.neat4j.neat.nn.core.NeuralNet;
import org.neat4j.neat.utils.NumberUtils;
import org.neat4j.neat.utils.RandomUtils;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class NEATTrainingForService implements Runnable {
    private static final Logger logger = LogManager.getLogger(NEATTrainingForService.class);


    private int numberOfThreads = 2;

    private List<Chromosome> bestEverChromosomes;

    protected GeneticAlgorithm geneticAlgorithm;
    protected Random random;
    protected InnovationDatabase innovationDatabase;

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

            this.random = RandomUtils.setSeed((Long) aiConfig.getConfigElementByName("GENERATOR.SEED"));
            GADescriptor gaDescriptor = this.createDescriptor(aiConfig);
            this.geneticAlgorithm = this.createGeneticAlgorithm(gaDescriptor);
            this.innovationDatabase = new InnovationDatabase(this.random, this.geneticAlgorithm.pluginAllowedActivationFunctions(aiConfig));
            this.geneticAlgorithm.pluginFitnessFunction(this.createFunction());
/*        try {
            this.assignConfig(config);
            innovationDatabase = new InnovationDatabase(this.random, this.ga.pluginAllowedActivationFunctions(config));
            this.ga.pluginFitnessFunction(this.createFunction());
            this.ga.pluginCrossOver(new NEATCrossover());
            this.ga.pluginMutator(new NEATMutator(this.random, innovationDatabase));
            this.ga.pluginParentSelector(new TournamentSelector(this.random));
            this.ga.createPopulation(innovationDatabase);
        */
        } catch (InvalidFitnessFunction e) {

            throw new InitialisationFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InitialisationFailedException(e.getMessage());
        }
    }

    private GeneticAlgorithm createGeneticAlgorithm(GADescriptor gaDescriptor) {
        GeneticAlgorithm ga = new NEATGeneticAlgorithm((NEATGADescriptor) gaDescriptor, this.random, this.numberOfThreads);
        return ga;
    }

    public void initialise(AIConfig config, DataKeeper trainDataSet, String pathToSave) throws InitialisationFailedException, IOException {

        /*config.updateConfig("TRAINING.SET", TEMP_DIRECTORY_PATH.getAbsolutePath()+"\\"+ UUID.randomUUID()+".tmp");
        trainDataSet.saveSet(config.getConfigElementByName("TRAINING.SET"), trainDataSet.getTrainData());


        List<List<Double>> testData = trainDataSet.getTestData();
        if(testData!=null) {
            config.updateConfig("TEST.SET", TEMP_DIRECTORY_PATH.getAbsolutePath() + "\\" + UUID.randomUUID() + ".tmp");
            trainDataSet.saveSet(config.getConfigElementByName("TEST.SET"), trainDataSet.getTestData());
        }
        config.updateConfig("SAVE.LOCATION", pathToSave);




        logger.debug("trainModel() : tempDataset name " + config.getConfigElementByName("TRAINING.SET"));
        //this.dataKeeper.setValue(trainDataSet);
        this.initialise(config);*/

    }

    public void evolve() {

       /* int epochs = this.asInt(config.getConfigElementByName("NUMBER.EPOCHS"));
        double terminateVal = ((NEATGADescriptor)this.ga.getDescriptor()).getErrorTerminationValue();
        boolean terminateEnabled = ((NEATGADescriptor)this.ga.getDescriptor()).isToggleErrorTerminationValue();
        boolean nOrder = ((NEATGADescriptor)this.ga.getDescriptor()).isNaturalOrder();
        boolean terminate = false;


        pathToSave = config.getConfigElementByName("SAVE.LOCATION");
        int i = 0;
        Long startTime = System.currentTimeMillis();
        while (i < epochs) {
            if(Thread.interrupted()) {
                break;
            }
            logger.info("Running Epoch[" + i + "]\r");
            this.ga.runEpoch();
            this.saveBest();

            if ((this.ga.discoverdBestMember().fitness() >= terminateVal && !nOrder) || (this.ga.discoverdBestMember().fitness() <= terminateVal && nOrder)) {
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
        //this.status.setValue(1.0);
        //this.isEnded.setValue(true);
        logger.debug("Innovation Database Stats - Hits:" + innovationDatabase.totalHits + " - totalMisses:" + innovationDatabase.totalMisses);
*/
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
        /*Chromosome best = this.ga.discoverdBestMember();
        best.setInputs(this.asInt(config.getConfigElementByName("INPUT.NODES")));
        best.setOutputs(this.asInt(config.getConfigElementByName("OUTPUT.NODES")));

        //this.bestEverChromosomeProperty.setValue(best);
        if(pathToSave != null)
            this.save(pathToSave, best);
        bestEverChromosomes.add(best);*/
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

    public FitnessFunction createFunction() throws InvalidFitnessFunction {
        FitnessFunction function = null;
        AIConfig nnConfig;
        NEATNetManager netManager;
        NeuralNet net = null;
        NetworkDataSet dataSet = null;
        NetworkDataSet testSet = null;
        LearningEnvironment env;

		/*try {

            nnConfig = new NEATConfig();
            //nnConfig  = new NEATLoader().loadConfig(nnConfigFile);
            nnConfig.updateConfig("INPUT_SIZE", config.configElement("INPUT.NODES"));
            nnConfig.updateConfig("OUTPUT_SIZE", config.configElement("OUTPUT.NODES"));
            nnConfig.updateConfig("LEARNABLE", config.configElement("LEARNABLE"));
            if(!config.configElement("TRAINING.SET").matches("/"))
                config.updateConfig("TRAINING.SET", config.configElement("CONFIGURATION.FILEPATH")+"/"+config.configElement("TRAINING.SET"));
            else
                config.updateConfig("TRAINING.SET", config.configElement("TRAINING.SET"));

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
		}*/

        return (function);
    }


}