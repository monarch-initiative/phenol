package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.util.HashSet;
import java.util.Set;

public class ParentChildUnonPValueCalculation extends ParentChildPValuesCalculation {
  public ParentChildUnonPValueCalculation(Ontology graph,
                                                  AssociationContainer goAssociations,
                                                  StudySet populationSet,
                                                  StudySet studySet,
                                                  Hypergeometric hyperg,
                                                  MultipleTestingCorrection mtc) {
    super(graph, goAssociations, populationSet, studySet, hyperg, mtc);
  }
  @Override
  protected Counts getCounts(TermId goId, Set<TermId> parents) {
    Set<TermId> parentsUnion = new HashSet<>();

    for (TermId par : parents) {
      Set<TermId>  annotedGeneIds = annotationMap.get(par).getTotalAnnotated();
      parentsUnion.addAll(annotedGeneIds);
    }
    int m_pa_t = parentsUnion.size();
    Set<TermId> studyAnnotated = studySetAnnotationMap.get(goId).getTotalAnnotated();
    //studyAnnotated.retainAll(parentsIntersection);
    int n_pa_t = studyAnnotated.size();
    return new Counts(m_pa_t, n_pa_t);
  }
}
