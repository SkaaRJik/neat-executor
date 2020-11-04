package org.neat4j.neat.manager.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.core.control.NEATNetManager;
import org.neat4j.neat.nn.core.NeuralNet;

import java.util.List;

public class MSENEATPredictionEngine {
	private static final Logger cat = LogManager.getLogger(MSENEATPredictionEngine.class);
	protected List<List<Double>> outs;


	public void initialise(AIConfig config, boolean initData) throws InitialisationFailedException, IllegalArgumentException {
		/*try {
			Chromosome chromo = (Chromosome) NEATChromosome.readObject(config.configElement("AI.SOURCE"));
			// need to create a nn based on this chromo.
			this.net = this.createNet(config);
			((NEATNetDescriptor)(this.net().netDescriptor())).updateStructure(chromo);
			((NEATNeuralNet)this.net()).updateNetStructure();
			//this.showNet();
			String testInputs = config.configElement("TEST.INPUTS");
			String testOutputs = config.configElement("TEST.OUTPUTS");

			List inputs = this.net.inputLayer();
			List outputs = this.net.outputLayer();

			if(inputs.size() != Integer.parseInt(testInputs) || outputs.size() !=Integer.parseInt(testOutputs) ){
				throw new IllegalArgumentException("Trained model: Inputs = " +inputs.size() + " Outputs = " + outputs.size() + "\n" +
						"Test data: Inputs = " +testInputs + " Outputs = " + testOutputs);
			}


			// now setup the input data
			String dataFile = config.configElement("TEST.DATA");
			if (dataFile != null) {
				this.netData = new CSVDataLoader(dataFile, outputs.size()).loadData();
			}

			*//*this.net.outputLayer().layerNeurons().length;
			this.net.*//*

		} catch (IOException e) {
			throw new InitialisationFailedException("Problem loading " + config.configElement("AI.SOURCE") + ":" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new InitialisationFailedException("Cannot find class for " + config.configElement("AI.SOURCE") + ":" + e.getMessage());
		}*/
	}



	public NeuralNet createNet(AIConfig config) throws InitialisationFailedException {

		NEATNetManager netManager;
		netManager = new NEATNetManager();
		netManager.initialise(config, true);

		return netManager.managedNet();
	}

	public void startTesting() {
		/*NetworkDataSet dataSet = this.netData();
		NetworkInputSet ipSet = dataSet.inputSet();
		NetworkInput ip;
		NetworkOutputSet opSet = null;
		NetworkOutput outSet;
		int i;
		this.outs = new ArrayList<>(ipSet.size());

		for (i = 0; i < ipSet.size(); i++) {
			ip = ipSet.inputAt(i);
			opSet = this.net.execute(ip);
			this.outs.add(opSet.nextOutput().getNetOutputs());
			//logger.info("Output for " + ip.toString() + " is "+ opSet.nextOutput().getNetOutputs());
		}*/
		/*try(FileWriter writer = new FileWriter("outs.txt", false))
		{
			for (i = 0; i < this.outs.length; i++) {
				writer.append(String.valueOf(this.outs[i]).replace(".", ",")+"\n");
			}
			writer.flush();
		}*/
		/*catch(IOException ex){
			System.out.println(ex.getMessage());
		}*/
	}

}