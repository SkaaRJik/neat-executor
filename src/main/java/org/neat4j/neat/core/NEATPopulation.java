/*
 * Created on 20-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.neat4j.neat.core;

import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.ga.core.Gene;
import org.neat4j.neat.ga.core.Population;
import org.neat4j.neat.nn.core.ActivationFunction;
import org.neat4j.neat.nn.core.functions.ActivationFunctionContainer;
import org.neat4j.neat.utils.RandomUtils;

/**
 * @author MSimmerson
 *
 * Contains a population of NEAT chromosomes that are used to describe a NEAT neural network.
 */
public class NEATPopulation implements Population {
	private Chromosome[] chromosomes;
	private int popSize;
	private int initialChromoSize;
	private int inputs;
	private int outputs;
	private boolean featureSelection;
	private int extraFeatureCount = 0;


	public NEATPopulation(int popSize, int initialChromoSize, int inputs, int outputs, boolean featureSelection, int extraFeaturecount) {
		this.popSize = popSize;
		this.initialChromoSize = initialChromoSize;
		this.inputs = inputs;
		this.outputs = outputs;
		this.featureSelection = featureSelection;
		this.extraFeatureCount = extraFeaturecount;

	}

	public NEATPopulation(int popSize, int initialChromoSize, int inputs, int outputs, boolean featureSelection) {
		this(popSize, initialChromoSize, inputs, outputs, featureSelection, 0);
	}
	
	public Chromosome[] genoTypes() {
		return (this.chromosomes);
	}

	/**
	 * Creates an intial population
	 */
	public void createPopulation(InnovationDatabase innovationDatabase) {
		this.chromosomes = new Chromosome[this.popSize];
		int i;
		// use the innovation database to create the initial population
		Chromosome[] templates = innovationDatabase.initialiseInnovations(this.popSize, this.inputs, this.outputs, this.featureSelection, this.extraFeatureCount);
		
		for (i = 0; i < this.popSize; i++) {
			this.chromosomes[i] = this.individualFromTemplate(templates[i], innovationDatabase.getActivationFunctionContainer());
		}
	}
	
	private Chromosome individualFromTemplate(Chromosome template, ActivationFunctionContainer activationFunctionContainer) {
		int i;
		Gene[] templateGenes = template.genes();
		Gene[] individualGenes = new Gene[templateGenes.length]; 
		NEATNodeGene nodeGene;
		NEATLinkGene linkGene;
		NEATFeatureGene featureGene;
		String label = "";
		for (i = 0; i < templateGenes.length; i++) {
			if (templateGenes[i] instanceof NEATNodeGene) {
				nodeGene = (NEATNodeGene)templateGenes[i];

				ActivationFunction activationFunction = null;
				if(nodeGene.getType() == NEATNodeGene.TYPE.INPUT) activationFunction = activationFunctionContainer.getRandomInputActivationFunction();
				else if(nodeGene.getType() == NEATNodeGene.TYPE.HIDDEN) activationFunction = activationFunctionContainer.getRandomHiddenActivationFunction();
				else if(nodeGene.getType() == NEATNodeGene.TYPE.OUTPUT) activationFunction = activationFunctionContainer.getRandomOutputActivationFunction();



				individualGenes[i] = new NEATNodeGene(nodeGene.getInnovationNumber(), nodeGene.id(), RandomUtils.nextPlusMinusOne(), nodeGene.getType(), nodeGene.getLabel(), RandomUtils.nextDouble(), activationFunction);
			} else if (templateGenes[i] instanceof NEATLinkGene) {
				linkGene = (NEATLinkGene)templateGenes[i];
				individualGenes[i] = new NEATLinkGene(linkGene.getInnovationNumber(), true, linkGene.getFromId(), linkGene.getToId(), RandomUtils.nextPlusMinusOne());
			} else if (templateGenes[i] instanceof NEATFeatureGene) {
				featureGene = (NEATFeatureGene)templateGenes[i];
				individualGenes[i] = new NEATFeatureGene(featureGene.getInnovationNumber(), RandomUtils.nextDouble());
			}
		}

		return (new NEATChromosome(individualGenes));
	}

	public void updatePopulation(Chromosome[] newGenoTypes) {
		if (newGenoTypes.length == this.popSize) {
			System.arraycopy(newGenoTypes, 0, this.chromosomes, 0, this.popSize);
		} else {
			System.out.println(this.getClass().getName() + ".updatePopulation() incompatable newGenoTypes length");
		}
	}

}
