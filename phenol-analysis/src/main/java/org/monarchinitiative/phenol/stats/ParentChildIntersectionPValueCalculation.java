package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.PopulationSet;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.util.List;

public class ParentChildIntersectionPValueCalculation extends ParentChildPValuesCalculation
{
	public ParentChildIntersectionPValueCalculation(Ontology graph,
                                                  AssociationContainer goAssociations, PopulationSet populationSet,
                                                  StudySet studySet, Hypergeometric hyperg, MultipleTestingCorrection mtc)
	{
		super(graph, goAssociations, populationSet, studySet, hyperg, mtc);
	}

	@Override
	protected Counts getCounts(int[] studyIds, TermId term)
	{
		//int slimIndex = slimGraph.getVertexIndex(term);
	//	int [] parents = slimGraph.vertexParents[slimIndex];
	//	int [][] parentItems = new int[parents.length][];

		int i = 0;
	//	for (int parent : parents)
	//	{
			//parentItems[i++] = term2Items[getIndex(slimGraph.getVertex(parent).getID())];
	//	}

	//	int [][] allItems = new int[parents.length + 1][];
	//	for (i=0;i<parents.length;i++)
		//{
	//		allItems[i] =  parentItems[i];
	//	}
	//	allItems[i] = studyIds;

		/* number of genes annotated to family (term and parents) */
		//Counts counts = new Counts(parents.length, commonInts(allItems), commonInts(parentItems));
		return null; // counts;
	}

  @Override
  public List<GoTerm2PValAndCounts> calculatePVals() {
    return null;
  }
}
