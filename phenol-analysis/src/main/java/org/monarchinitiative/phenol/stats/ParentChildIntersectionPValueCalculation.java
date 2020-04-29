package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.util.HashSet;
import java.util.Set;

public class ParentChildIntersectionPValueCalculation extends ParentChildPValuesCalculation {
	public ParentChildIntersectionPValueCalculation(Ontology graph,
                                                  AssociationContainer goAssociations,
                                                  StudySet populationSet,
                                                  StudySet studySet,
                                                  MultipleTestingCorrection mtc) {
		super(graph, goAssociations, populationSet, studySet, mtc);
	}

  @Override
  protected Counts getCounts(TermId goId, Set<TermId> parents) {
    Set<TermId> parentsIntersection = new HashSet<>();

    for (TermId par : parents) {
      Set<TermId>  annotedGeneIds = annotationMap.get(par).getTotalAnnotated();
      if (parentsIntersection.isEmpty()) {
        parentsIntersection.addAll(annotedGeneIds);
      } else {
        // the following is set intersection
        parentsIntersection.retainAll(annotedGeneIds);
      }
    }
    int m_pa_t = parentsIntersection.size();
    Set<TermId> studyAnnotated = studySetAnnotationMap.get(goId).getTotalAnnotated();
    studyAnnotated.retainAll(parentsIntersection);
    int n_pa_t = studyAnnotated.size();
    return new Counts(m_pa_t, n_pa_t);
  }


}
