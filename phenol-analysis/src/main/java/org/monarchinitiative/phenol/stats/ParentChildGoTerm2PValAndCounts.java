package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

public class ParentChildGoTerm2PValAndCounts extends GoTerm2PValAndCounts {

  public ParentChildGoTerm2PValAndCounts(TermId goId,
                                         double raw_pval,
                                         int annotatedStudyGenes,

                                         int annotatedPopulationGenes) {
    super(goId, raw_pval, annotatedStudyGenes, annotatedPopulationGenes);
  }
}
