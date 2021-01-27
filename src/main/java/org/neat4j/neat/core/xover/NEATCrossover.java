/*
 * Created on 20-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.neat4j.neat.core.xover;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neat4j.neat.core.NEATChromosome;
import org.neat4j.neat.core.NEATGene;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.ga.core.ChromosomeSet;
import org.neat4j.neat.ga.core.CrossOver;
import org.neat4j.neat.utils.RandomUtils;

import java.util.ArrayList;

/**
 * @author MSimmerson
 *
 * Performs GA crossover between 2 individuals based on the NEAT xover algortihm described
 * by Kenneth Stanley 
 */
public class NEATCrossover implements CrossOver {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(NEATCrossover.class);
	private double pXOver;
	
	public void setProbability(double prob) {
		this.pXOver = prob;
	}

	public ChromosomeSet crossOver(ChromosomeSet parents) {
		ChromosomeSet childSet = new ChromosomeSet(false);
		ArrayList childGenes = new ArrayList();
		Chromosome pOne = null;
		Chromosome pTwo = null;
		Chromosome best;
		Chromosome worst;
		int bestIdx = 0;
		int worstIdx = 0;
		boolean childBorn = false;
		NEATGene[] bestGenes;
		NEATGene[] worstGenes;

		pOne = parents.nextChromosome();
		pTwo = parents.nextChromosome();
		
		// find best parent
		if (pOne.fitness() == pTwo.fitness()) {
			if (pOne.genes().length == pTwo.genes().length) {
				best = pOne;
				worst = pTwo;
			} else {
				if (pOne.genes().length < pTwo.genes().length) {
					best = pOne;
					worst = pTwo;
				} else {
					best = pTwo;
					worst = pOne;
				}
			}
		} else {
			best = pOne.fitness() > pTwo.fitness() ? pOne : pTwo;
			worst = pOne.fitness() > pTwo.fitness() ? pTwo : pOne;
		}

		bestGenes = (NEATGene[])best.genes();
		worstGenes = (NEATGene[])worst.genes();
		
		while (!childBorn) {
			if (worstIdx >= worstGenes.length) {
				// copy rest of best
				while (bestIdx < bestGenes.length) {
					childGenes.add(bestGenes[bestIdx++]);
				}
				childBorn = true;
			} else if (bestIdx >= bestGenes.length) {
				childBorn = true;
			} else if (bestGenes[bestIdx].getInnovationNumber() == worstGenes[worstIdx].getInnovationNumber()) {
				// innovations are the same, pick one gene at RandomUtils.getRand()om
				childGenes.add(RandomUtils.getRand().nextBoolean() ? bestGenes[bestIdx] : worstGenes[worstIdx]);
				bestIdx++;
				worstIdx++;
			} else if (bestGenes[bestIdx].getInnovationNumber() > worstGenes[worstIdx].getInnovationNumber()){
				// skip disjoint/excess
				worstIdx++;
			} else if (bestGenes[bestIdx].getInnovationNumber() < worstGenes[worstIdx].getInnovationNumber()){
				// add best disjoint/excess
				childGenes.add(bestGenes[bestIdx]);
				bestIdx++;
			}
		}
		
		childSet.add(this.createChromosome(childGenes));
		
		return (childSet);
	}
	
	private NEATChromosome createChromosome(ArrayList genes) {
		NEATChromosome chromo = null;
		NEATGene[] geneSet = new NEATGene[genes.size()];
		int i;
		
		for (i = 0; i < geneSet.length; i++) {
			geneSet[i] = (NEATGene)genes.get(i);
		}
		chromo = new NEATChromosome(geneSet);
		
		return (chromo);
	}
}
