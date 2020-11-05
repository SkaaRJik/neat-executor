/*
 * Created on 22-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.neat4j.neat.core.control;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.AIController;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.NEATNetDescriptor;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.data.csv.CSVDataLoader;
import org.neat4j.neat.data.csv.JsonDataConverter;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.nn.core.*;
import org.neat4j.neat.nn.core.learning.GALearnable;
import ru.filippov.neatexecutor.entity.ProjectConfig;

import java.util.List;

/**
 * @author MSimmerson
 *
 * Mamages the initialisation and creation of a NEAT network
 */
public class NEATNetManagerForService implements AIController {
	private static final Logger logger = LogManager.getLogger(NEATNetManagerForService.class);
	private NeuralNet net;
	private AIConfig config;


	public NEATNetManagerForService(AIConfig aiConfig, ProjectConfig.NormalizedDataDto normalizedDataDto) {
		this.config = aiConfig;





	}

	public boolean save(String fileName) {
		return (false);
	}

	public NeuralNetDescriptor createNetDescriptor(boolean loadData) {
		return (this.createNetDescriptor(this.config, loadData));
	}

	public NeuralNetDescriptor createNetDescriptor(AIConfig config, boolean init) {
		NeuralNetDescriptor descriptor = null;
		int inputLayerSize = Integer.parseInt((String) config.getConfigElementByName("INPUT.NODES"));
		int outputLayerSize = Integer.parseInt((String) config.getConfigElementByName("OUTPUT.NODES"));
		// create learnable

		Learnable learnable = this.createLearnable(config, outputLayerSize, init);

		descriptor = new NEATNetDescriptor(inputLayerSize, learnable);

		return (descriptor);
	}

	public NeuralNetDescriptor createNetDescriptor(Chromosome chromo) {
		NeuralNetDescriptor descriptor = null;
		int inputLayerSize = chromo.getInputs();

		descriptor = new NEATNetDescriptor(inputLayerSize, null);

		return (descriptor);
	}

	public void initialise(AIConfig config, boolean loadData) throws InitialisationFailedException {
		this.config = config;

	}

	public void initialise(Chromosome chromosome) throws InitialisationFailedException {
		NeuralNetDescriptor descriptor = this.createNetDescriptor(chromosome);
		this.net = NeuralNetFactory.getFactory().createNN(descriptor);
	}

	public List<NetworkDataSet>  dataSet(ProjectConfig.NormalizedDataDto normalizedDataDto, int inputs, int outputs, int trainEndIndex, int testEndIndex) {
		List<NetworkDataSet> dSet = null;


		dSet = new JsonDataConverter(normalizedDataDto, inputs, outputs, trainEndIndex, testEndIndex).loadData();


		return (dSet);
	}

	public Learnable createLearnable(ProjectConfig.NormalizedDataDto normalizedDataDto, ) {



		// learning env
		LearningEnvironment le = new LearningEnvironment();
		this.dataSet(normalizedDataDto, )
			le.addEnvironmentParameter("TRAINING.SET", dSet);
		if(init) {
			dSet = this.dataSet("TEST.SET", config, numOutputs);
		}
		le.addEnvironmentParameter("TEST.SET", dSet);

		Learnable learnable = new GALearnable(le);
		logger.debug("learnableClassName:" + learnable.getClass().getName());


		return (learnable);
	}

	public NeuralNet managedNet() {
		return (this.net);
	}
}