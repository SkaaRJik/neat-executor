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
	private ProjectConfig.NormalizedDataDto normalizedDataDto;

	public NEATNetManagerForService(AIConfig aiConfig, ProjectConfig.NormalizedDataDto normalizedDataDto) {
		this.config = aiConfig;
		this.normalizedDataDto = normalizedDataDto;




	}

	public boolean save(String fileName) {
		return (false);
	}

	public NeuralNetDescriptor createNetDescriptor() {
		NeuralNetDescriptor descriptor = null;
		int inputLayerSize = (int) config.getConfigElementByName("INPUT.NODES");

		Learnable learnable = this.createLearnable();

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
		NeuralNetDescriptor descriptor = this.createNetDescriptor();
		this.net = NeuralNetFactory.getFactory().createNN(descriptor);
	}

	public void initialise(Chromosome chromosome) throws InitialisationFailedException {
		NeuralNetDescriptor descriptor = this.createNetDescriptor(chromosome);
		this.net = NeuralNetFactory.getFactory().createNN(descriptor);
	}

	public List<NetworkDataSet>  dataSet() {
		int trainEndIndex = this.normalizedDataDto.getTrainEndIndex();
		int testEndIndex = this.normalizedDataDto.getTestEndIndex();

		List<NetworkDataSet> dSet = null;
		int inputs = (int) config.getConfigElementByName("INPUT.NODES");
		int outputs = (int) config.getConfigElementByName("OUTPUT.NODES");

		dSet = new JsonDataConverter(normalizedDataDto, inputs, outputs, trainEndIndex, testEndIndex).loadData();

		return (dSet);
	}

	public Learnable createLearnable() {



		// learning env
		LearningEnvironment le = new LearningEnvironment();
		// create learnable

		if (this.normalizedDataDto != null) {
			List<NetworkDataSet> networkDataSets = this.dataSet();

			le.setTrainDataSet(networkDataSets.get(0));
			le.setTestDataSet(networkDataSets.get(1));
		}


		Learnable learnable = new GALearnable(le);
		logger.debug("learnableClassName:" + learnable.getClass().getName());


		return (learnable);
	}

	public NeuralNet managedNet() {
		return (this.net);
	}
}