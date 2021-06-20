package org.neat4j.neat.manager.train;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.InnovationDatabase;
import org.neat4j.neat.core.NEATConfig;
import org.neat4j.neat.core.NEATGADescriptor;
import org.neat4j.neat.core.NEATGeneticAlgorithm;
import org.neat4j.neat.core.control.NEATNetManagerForService;
import org.neat4j.neat.core.fitness.InvalidFitnessFunction;
import org.neat4j.neat.core.fitness.MSENEATFitnessFunction;
import org.neat4j.neat.core.mutators.NEATMutator;
import org.neat4j.neat.core.pselectors.TournamentSelector;
import org.neat4j.neat.core.xover.NEATCrossover;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.ga.core.*;
import org.neat4j.neat.nn.core.LearningEnvironment;
import org.neat4j.neat.nn.core.NeuralNet;
import org.neat4j.neat.utils.NumberUtils;
import org.neat4j.neat.utils.RandomUtils;
import ru.filippov.neatexecutor.entity.ExperimentConfigEntity;
import ru.filippov.neatexecutor.entity.ProjectConfig;
import ru.filippov.neatexecutor.rabbitmq.RabbitMQWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NEATTrainingForService implements Runnable {
    private static final Logger logger = LogManager.getLogger(NEATTrainingForService.class);

    private RabbitMQWriter rabbitMQWriter;
    private int numberOfThreads = 2;
    private AIConfig config;
    private List<Chromosome> bestEverChromosomes;

    protected GeneticAlgorithm geneticAlgorithm;
    protected InnovationDatabase innovationDatabase;
    protected ExecutorService executorService;

    protected Long configId;

    private long timeSpend;


    public NEATTrainingForService(AIConfig aiConfig,  ProjectConfig.NormalizedDataDto normalizedDataDto) throws InitialisationFailedException, IOException {

        Properties prop = new Properties();

        //load a properties file from class path, inside static method
        prop.load(NEATTrainingForService.class.getClassLoader().getResourceAsStream("application.properties"));

        String maxThreadsProperty = prop.getProperty("max-threads");
        if (maxThreadsProperty != null) {
            this.numberOfThreads = Integer.parseInt(maxThreadsProperty);
        }

        this.initialise(aiConfig, normalizedDataDto);
    }

    public NEATTrainingForService(AIConfig aiConfig,  ProjectConfig.NormalizedDataDto normalizedDataDto, int numberOfThreads) throws InitialisationFailedException, IOException {
        if(numberOfThreads <= 0) {
            throw new InitialisationFailedException("Number of threads must be more or equal then 1");
        }

        this.numberOfThreads = numberOfThreads;
        this.initialise(aiConfig, normalizedDataDto);
    }

    @SneakyThrows
    @Override
    public void run() {
        this.evolve();
    }

    public static AIConfig parseNeatSetting(List<ExperimentConfigEntity.NeatSetting> neatSettings) {

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


    public void initialise( AIConfig config,  ProjectConfig.NormalizedDataDto normalizedDataDto) throws InitialisationFailedException {
        try {
            this.config = config;
            RandomUtils.setSeed((Long) config.getConfigElementByName("GENERATOR.SEED"));
            GADescriptor gaDescriptor = this.createDescriptor(config);
            this.geneticAlgorithm = this.createGeneticAlgorithm(gaDescriptor);
            this.innovationDatabase = new InnovationDatabase(this.geneticAlgorithm.pluginAllowedActivationFunctions(config));
            this.geneticAlgorithm.pluginFitnessFunction(this.createFunction(config, normalizedDataDto));
            this.geneticAlgorithm.pluginCrossOver(new NEATCrossover());
            this.geneticAlgorithm.pluginMutator(new NEATMutator(innovationDatabase));
            this.geneticAlgorithm.pluginParentSelector(new TournamentSelector());
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
        this.executorService = Executors.newCachedThreadPool();
        GeneticAlgorithm ga = new NEATGeneticAlgorithm((NEATGADescriptor) gaDescriptor, this.numberOfThreads, executorService);
        return ga;
    }

    public void evolve() throws ExecutionException, InterruptedException {

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
            logger.debug("Running Epoch[" + i + "]\r");
            try{
                this.geneticAlgorithm.runEpoch();
            } catch (ExecutionException e) {
                logger.error(String.format("[epoch] = %d", i));
                throw e;
            }

            this.saveBest();

            if ((this.geneticAlgorithm.discoverdBestMember().fitness() >= terminateVal && !nOrder)
                    || (this.geneticAlgorithm.discoverdBestMember().fitness() <= terminateVal && nOrder)) {
                terminate = true;
            }

            i++;
            //status.setValue(((double)i)/epochs);

            if(terminate && terminateEnabled) {
                break;
            }

        }
        this.timeSpend = System.currentTimeMillis() - startTime;
        Chromosome chromosome = bestEverChromosomes.get(bestEverChromosomes.size() - 1);
        this.executorService.shutdown();

        logger.info(String.format("trainError: [ %f ], testError: [ %f ], timeSpend: [ %d ]", chromosome.getTrainError(), chromosome.getValidationError(), timeSpend));
        logger.debug("Innovation Database Stats - Hits:" + innovationDatabase.totalHits + " - totalMisses:" + innovationDatabase.totalMisses);
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

    public Chromosome getBestChromosome(){
        //return this.bestEverChromosomes.get(this.bestEverChromosomes.size()-1);

        return this.bestEverChromosomes.stream().min((o1, o2) -> Double.compare(o1.getValidationError(), o1.getValidationError())).orElse(this.bestEverChromosomes.get(this.bestEverChromosomes.size()-1));
    }

    public long getTimeSpend() {
        return timeSpend;
    }

    public List<Double> getTrainErrors() {
        return this.bestEverChromosomes.stream().map(Chromosome::getTrainError).collect(Collectors.toList());
    }

    public List<Double> getTestErrors() {
        return this.bestEverChromosomes.stream().map(Chromosome::getValidationError).collect(Collectors.toList());
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }
}