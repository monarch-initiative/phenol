package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This class is used to store the numbers we need to calculate a P Value for individual GO Terms
 * using the Term for Term approach and also to store the numbers of study and population genes
 * that were annotated to the term
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */

public class GoTerm2PValAndCounts extends Item2PValue<TermId>
{

  private final int annotatedStudyGenes;
  private final int annotatedPopulationGenes;

  public GoTerm2PValAndCounts(TermId goId, double raw_pval, int annotatedStudyGenes, int annotatedPopulationGenes){
    super(goId,raw_pval);
    this.annotatedPopulationGenes=annotatedPopulationGenes;
    this.annotatedStudyGenes=annotatedStudyGenes;
  }

  public int getAnnotatedStudyGenes() {
    return annotatedStudyGenes;
  }

  public int getAnnotatedPopulationGenes() {
    return annotatedPopulationGenes;
  }

}
