/*
 * Created on 20-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.neat4j.neat.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.neat.core.mutators.NEATMutator;
import org.neat4j.neat.ga.core.*;
import org.neat4j.neat.nn.core.functions.ActivationFunctionContainer;
import org.neat4j.neat.nn.core.functions.ActivationFunctionImpl;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 
 * @author MSimmerson
 *
 */
public class NEATGeneticAlgorithm implements GeneticAlgorithm {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(NEATGeneticAlgorithm.class);
	private NEATGADescriptor descriptor;
	private NEATMutator mut;
	private FitnessFunction func;
	private ParentSelector selector;
	private CrossOver xOver;
	private Population pop;
	private Chromosome discoveredBest;
	private Chromosome genBest;
	private Species specieList;
	private int specieIdIdx;
	private int eleCount = 0;
	private final int numberOfThreads;
	private int offsetOfIndex;
	private final ExecutorService executorService;

	/**
	 * Creates a NEAT GA with behaviour defined by the getDescriptor
	 * @param descriptor
	 */
	public NEATGeneticAlgorithm(NEATGADescriptor descriptor, int numberOfThreads, ExecutorService executorService) {
		this.descriptor = descriptor;
		this.specieList = new Species();
		this.numberOfThreads = numberOfThreads;
		this.executorService = executorService;
		this.offsetOfIndex = descriptor.gaPopulationSize() / numberOfThreads;
		specieIdIdx = 1;
	}

	/**
	 * Runs an evaluation and evolution cycle
	 */
	public void runEpoch() throws ExecutionException, InterruptedException {
		Chromosome[] currentGen = this.pop.genoTypes();
		List<Future<?>> futures = new ArrayList<>(this.numberOfThreads);
		this.setChromosomeNO(currentGen);
		logger.debug("Evaluating pop");
		for (int i = 0; i < this.numberOfThreads; i++) {
			int finalI = i;

			Future<?> submit = this.executorService.submit(() ->
					calculateFitness(currentGen, (finalI) * offsetOfIndex, (finalI) * offsetOfIndex + offsetOfIndex));
			futures.add(submit);
		}

		for (Future<?> future : futures){
			future.get();
		}


		this.runEvolutionCycle(currentGen);

	}

	private Chromosome[] getPartChromosomes(Chromosome[] currentGen, int i) {
		final Chromosome[] part = new Chromosome[offsetOfIndex];
		int k = 0;
		i++;
		for (int j = (i)*offsetOfIndex; j < (i)*offsetOfIndex+offsetOfIndex; j++) {
			part[k++] = currentGen[j];
		}
		return part;
	}


	public FitnessFunction getGaEvaluator() {
		return (this.func);
	}
	
	public GADescriptor getDescriptor() {
		return (this.descriptor);
	}

	/**
	 * Creates the initial population
	 */
	public void createPopulation(InnovationDatabase innovationDatabase) {
		int popSize = this.descriptor.gaPopulationSize();
		int initialChromoSize = this.func.requiredChromosomeSize() + this.descriptor.getExtraFeatureCount();
		this.pop = new NEATPopulation(popSize, initialChromoSize, this.descriptor.getInputNodes(), this.descriptor.getOutputNodes(), this.descriptor.featureSelectionEnabled(), this.descriptor.getExtraFeatureCount());
		this.pop.createPopulation(innovationDatabase);
	}

	/**
	 * Finds the best performing chromosome from a give evaluation cycle
	 * @param genoTypes - population
	 * @return
	 */
	private Chromosome findBestChromosome(Chromosome[] genoTypes) {
		Chromosome best = genoTypes[0];
		int i;
		
		for (i = 1; i < genoTypes.length; i++) {
			if (this.descriptor.isNaturalOrder()) {
				if (genoTypes[i].fitness() < best.fitness()) {
					best = genoTypes[i];
				}
			} else {
				if (genoTypes[i].fitness() > best.fitness()) {
					best = genoTypes[i];
				}
			}
		}
		
		return (best);
	}

	private void calculateFitness(Chromosome[] genoTypes, int start, int end) {
		double eval;
		
		for (int i = start; i < end; i++) {
			eval = this.func.evaluate(genoTypes[i]);
			genoTypes[i].updateFitness(eval);			
		}
	}
	
	private Chromosome cloneBest(Chromosome best) {
		Chromosome cloneBest = new NEATChromosome(best.genes());
		cloneBest.updateFitness(best.fitness());
		cloneBest.setOutputValues(best.getOutputValues());
		cloneBest.setTrainError(best.getTrainError());
		cloneBest.setValidationError(best.getValidationError());
		((NEATChromosome)cloneBest).setSpecieId(((NEATChromosome)best).getSpecieId());
		
		return (cloneBest);
	}
	
	private void speciatePopulation(Chromosome[] currentGen) {
		int i;
		int j;
		boolean memberAssigned = false;
		ArrayList currentSpecieList;
		Specie specie;
		
		this.specieList.resetSpecies(this.descriptor.getThreshold());
		
		logger.debug("Compat threshold:" + this.descriptor.getThreshold());
		for (i = 0; i < currentGen.length; i++) {
			if (!memberAssigned) {
				currentSpecieList = this.specieList.specieList();
				j = 0;
				while (!memberAssigned && j < currentSpecieList.size()) {
					specie = (Specie)currentSpecieList.get(j);
					if (specie.addSpecieMember(currentGen[i])) {
						memberAssigned = true;
						//((NEATChromosome)currentGen[i]).setSpecieId(specie.id());
						//logger.info("Member assigned to specie " + specie.id());
					} else {
						j++;
					}
				}	
				
				if (!memberAssigned) {
					specie = this.createNewSpecie(currentGen[i]);
					this.specieList.addSpecie(specie);
					//((NEATChromosome)currentGen[i]).setSpecieId(specie.id());
					logger.debug("Created new specie, member assigned to specie " + specie.id());
				}
			}
			memberAssigned = false;
		}

		if (logger.isDebugEnabled()) {
			for (i = 0; i < this.specieList.specieList().size(); i++) {
				specie = (Specie)this.specieList.specieList().get(i);
				if (!specie.isExtinct() && specie.specieMembers().size() > 0) {
					logger.debug("Specie:" + specie.id() + ":size:" + specie.specieMembers().size() + ":age:" + ((NEATSpecie)specie).specieAge() + ":fAge:" + + specie.getCurrentFitnessAge() + ":best:" + specie.findBestMember().fitness());
				}
			}
		}
	}


	
	/**
	 * Runs an evolution cycle on the given population
	 * @param currentGen
	 */
	public void runEvolutionCycle(Chromosome[] currentGen) {
		NEATChromosome champ;
		ArrayList validSpecieList;

		// ELE?
		this.runEle(currentGen);
		// speciate the remaining population
		this.speciatePopulation(currentGen);
		this.genBest = this.findBestChromosome(currentGen);		
		if ((this.discoveredBest == null) || (this.genBest.fitness() >= this.discoveredBest.fitness() && !this.descriptor.isNaturalOrder()) || (this.genBest.fitness() <= this.discoveredBest.fitness() && this.descriptor.isNaturalOrder())) {
			// copy best
			this.discoveredBest = this.cloneBest(this.genBest);
		}
		logger.debug("Best Ever Raw:" + (this.discoveredBest.fitness()) + ":from specie:" + ((NEATChromosome)this.discoveredBest).getSpecieId());
		logger.debug("Best of Generation is:" + (this.genBest.fitness()) + " specie " + ((NEATChromosome)this.genBest).getSpecieId());
		// kill any extinct species
		if (this.descriptor.keepBestEver()) {
			champ = (NEATChromosome)this.discoveredBest;
		} else {
			champ = (NEATChromosome)this.genBest;
		}
		this.specieList.removeExtinctSpecies(champ);
		logger.debug("Creating New Gen");
		// spawn new pop	
		this.pop.updatePopulation(this.spawn());
		// Display specie stats
		validSpecieList = this.specieList.validSpecieList(champ.getSpecieId());
		logger.debug("Num species:" + validSpecieList.size());
		if (this.descriptor.getCompatabilityChange() > 0) {
			if (validSpecieList.size() > this.descriptor.getSpecieCount()) {
				this.descriptor.setThreshold(this.descriptor.getThreshold() + this.descriptor.getCompatabilityChange());
			} else if (validSpecieList.size() < this.descriptor.getSpecieCount() && (this.descriptor.getThreshold() > this.descriptor.getCompatabilityChange())) {
				this.descriptor.setThreshold(this.descriptor.getThreshold() - this.descriptor.getCompatabilityChange());
			}
		}
		this.eleCount++;
	}
	
	private void runEle(Chromosome[] currentGen) {
		if (this.descriptor.isEleEvents()) {
			if ((this.eleCount % this.descriptor.getEleEventTime()) == 0 && this.eleCount != 0) {
				logger.debug("Runnig ELE");
				this.descriptor.setThreshold(this.descriptor.getThreshold() * 5);
			} else if ((this.eleCount % this.descriptor.getEleEventTime()) == 1 && this.eleCount != 1) {
				this.descriptor.setThreshold(this.descriptor.getThreshold() / 5.0);
			}
		}
	}
	
	private void setChromosomeNO(Chromosome[] gen) {
		int i;
		
		for (i = 0; i < gen.length; i++) {
			((NEATChromosome)gen[i]).setNaturalOrder(this.descriptor.isNaturalOrder());
		}
	}
	
	private Chromosome[] spawn() {
		Chromosome[] currentGen = this.pop.genoTypes();
		Chromosome[] newGen = new Chromosome[currentGen.length];
		Specie specie = null;
		ChromosomeSet offspring = null;
		int offSpringCount;
		int newGenIdx = 0;
		int i;
		int j = 0;
		double totalAvFitness;
		//int offSp = 0;
		
		// update species by sharing fitness.
		this.specieList.shareFitness();
		totalAvFitness = this.specieList.totalAvSpeciesFitness();
		// mate within valid species to produce new population
		ArrayList species = this.specieList.validSpecieList(((NEATChromosome)this.discoveredBest).getSpecieId());
		for (i = 0; i < species.size(); i++) {
			specie = (Specie)species.get(i);
			if (specie.specieMembers().size() == 0) {
				logger.error("spawn produced error:");
			}
			// ensure we have enough for next gen
			if (i == (species.size() - 1)) {
				offSpringCount = newGen.length - newGenIdx;
				//offSp = this.specieList.calcSpecieOffspringCount(specie, this.getDescriptor.gaPopulationSize(), totalAvFitness);
			} else {
				offSpringCount = this.specieList.calcSpecieOffspringCount(specie, this.descriptor.gaPopulationSize(), totalAvFitness);
			}
			
			//logger.debug("Sp[" + specie.id() + "] Age:" + ((NEATSpecie)specie).specieAge() + ":Offspring Sz:" + offSpringCount + ":AvF:" + specie.getAverageFitness() + ":FAge:" + specie.getCurrentFitnessAge() + ":BestF:" + specie.findBestMember().fitness());
			if (offSpringCount > 0) {
				offspring = specie.specieOffspring(offSpringCount, this.mut, this.selector, this.xOver);
				for (j = 0; j < offspring.size(); j++) {
					if (newGenIdx < newGen.length) {
						// if population not full
						newGen[newGenIdx++] = offspring.nextChromosome();
					}
				}
			} else {
				logger.debug("Specie " + specie.id() + ":size:" + specie.specieMembers().size() + " produced no offspring.  Average fitness was " + specie.averageFitness() + " out of a total fitness of " + this.specieList.totalAvSpeciesFitness());
				specie.setExtinct();
			}
		}
				
		return (newGen);
	}
	
	private Specie createNewSpecie(Chromosome member) {
		double excessCoeff = this.descriptor.getExcessCoeff();
		double disjointCoeff = this.descriptor.getDisjointCoeff();
		double weightCoeff = this.descriptor.getWeightCoeff();
		double threshold = this.descriptor.getThreshold();
		
		NEATSpecie specie = new NEATSpecie(threshold, excessCoeff, disjointCoeff, weightCoeff, specieIdIdx++);
		specie.setMaxFitnessAge(this.descriptor.getMaxSpecieAge());
		specie.setAgePenalty(this.descriptor.getAgePenalty());
		specie.setAgeThreshold(this.descriptor.getSpecieAgeThreshold());
		specie.setYouthBoost(this.descriptor.getYouthBoost());
		specie.setYouthThreshold(this.descriptor.getSpecieYouthThreshold());
		specie.setSurvivalThreshold(this.descriptor.getSurvivalThreshold());
		specie.addSpecieMember(member);
		
		return (specie);
	}

	public Chromosome discoverdBestMember() {
		return (this.discoveredBest);
	}


	public void pluginMutator(Mutator mut) {
		this.mut = (NEATMutator)mut;
		this.mut.setPAddLink(this.descriptor.getPAddLink());
		this.mut.setPAddNode(this.descriptor.getPAddNode());
		this.mut.setPPerturb(this.descriptor.getPMutation());
		this.mut.setPToggle(this.descriptor.getPToggleLink());
		this.mut.setPWeightReplaced(this.descriptor.getPWeightReplaced());
		this.mut.setFeatureSelection(this.descriptor.featureSelectionEnabled());
		this.mut.setRecurrencyAllowed(this.descriptor.isRecurrencyAllowed());
		this.mut.setPMutateBias(this.descriptor.getPMutateBias());
		this.mut.setBiasPerturb(this.descriptor.getMaxBiasPerturb());
		this.mut.setPerturb(this.descriptor.getMaxPerturb());
		this.mut.setpNewActivationFunction(this.descriptor.pNewActivationFunction);
	}


	public void pluginFitnessFunction(FitnessFunction func) {
		this.func = func;
	}


	public void pluginParentSelector(ParentSelector selector) {
		this.selector = selector;
		this.selector.setOrderStrategy(this.descriptor.isNaturalOrder());
	}


	public void pluginCrossOver(CrossOver xOver) {
		this.xOver = xOver;
		this.xOver.setProbability(this.descriptor.getPXover());
	}

	/**
	 * Saves the entire population.  Especially useful for long running evolution processes
	 */
	public void savePopulationState(String fileName) {
		FileOutputStream out = null;
		ObjectOutputStream s = null;
		try {
			if (fileName != null) {
				logger.debug("Saving Population " + fileName);
				out = new FileOutputStream(fileName);
				s = new ObjectOutputStream(out);
				s.writeObject(this.pop);
				s.flush();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				s.close();
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		logger.debug("Saving Population...Done");
	}

	public Population population() {
		return (this.pop);
	}

	@Override
	public ActivationFunctionContainer pluginAllowedActivationFunctions(AIConfig config) {
		ActivationFunctionContainer activationFunctionContainer = new ActivationFunctionContainer();
		List<String> inputFunctions = (List<String>) config.getConfigElementByName("INPUT.ACTIVATIONFUNCTIONS");
		List<String> hiddenFunctions =(List<String>) config.getConfigElementByName("HIDDEN.ACTIVATIONFUNCTIONS");
		List<String> outputFunctions =(List<String>) config.getConfigElementByName("OUTPUT.ACTIVATIONFUNCTIONS");


		try {


			activationFunctionContainer.setInputActivationFunctions(new ArrayList<>(inputFunctions.size()));
			for(String function : inputFunctions){
				if(!StringUtils.isEmpty(function)){
					activationFunctionContainer.getInputActivationFunctions().add((ActivationFunctionImpl)(Class.forName(function).newInstance()));
				}

			}

			activationFunctionContainer.setHiddenActivationFunctions(new ArrayList<>(hiddenFunctions.size()));
			for(String function : hiddenFunctions){
				if(!StringUtils.isEmpty(function)) {
					activationFunctionContainer.getHiddenActivationFunctions().add((ActivationFunctionImpl) (Class.forName(function).newInstance()));
				}
			}
			activationFunctionContainer.setOutputActivationFunctions(new ArrayList<>(outputFunctions.size()));
			for(String function : outputFunctions){
				if(!StringUtils.isEmpty(function)) {
					activationFunctionContainer.getOutputActivationFunctions().add((ActivationFunctionImpl) (Class.forName(function).newInstance()));
				}
			}
		} catch (InstantiationException e) {
			logFunctionError(inputFunctions, hiddenFunctions, outputFunctions, e);
		} catch (IllegalAccessException e) {
			logFunctionError(inputFunctions, hiddenFunctions, outputFunctions, e);
		} catch (ClassNotFoundException e) {
			logFunctionError(inputFunctions, hiddenFunctions, outputFunctions, e);
		}
		return activationFunctionContainer;
	}

	private void logFunctionError(List<String> inputFunctions, List<String> hiddenFunctions, List<String> outputFunctions, Exception e) {
		logger.error(
				String.format("[NEATGeneticAlgorithm].pluginAllowedActivationFunctions \ninputs: [%s] \nhidden: [%s] \noutputs: [%s]",
						String.join(",", inputFunctions),
						String.join(",", hiddenFunctions),
						String.join(",", outputFunctions)),
				e
		);
	}


	public Chromosome generationBest() {
		return (this.genBest);
	}
}
