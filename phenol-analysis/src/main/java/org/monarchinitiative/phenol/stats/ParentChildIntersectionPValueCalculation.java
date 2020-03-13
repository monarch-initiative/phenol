package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.PopulationSet;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.util.List;
import java.util.Set;

public class ParentChildIntersectionPValueCalculation extends ParentChildPValuesCalculation
{
	public ParentChildIntersectionPValueCalculation(Ontology graph,
                                                  AssociationContainer goAssociations, PopulationSet populationSet,
                                                  StudySet studySet, Hypergeometric hyperg, MultipleTestingCorrection mtc)
	{
		super(graph, goAssociations, populationSet, studySet, hyperg, mtc);
	}

  @Override
  protected Counts getCounts(TermId goId, Set<TermId> parents) {
    return null;
  }



  @Override
  public List<GoTerm2PValAndCounts> calculatePVals() {
    return null;
  }
}
