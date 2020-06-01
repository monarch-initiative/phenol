package org.monarchinitiative.phenol.analysis.mgsa;


import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * MGSA Result for one GO Term
 *
 * @author Sebastian Bauer
 * @author Peter Robinson
 */

public class MgsaGOTermResult {


  private final double marg;
  private final TermId termId;
  private final int annotatedStudyGenes;
  private final int annotatedPopulationGenes;

  public MgsaGOTermResult(TermId tid, int annotatedStudy, int annotatedPopulation, double marg) {
    this.termId = tid;
    this.annotatedStudyGenes = annotatedStudy;
    this.annotatedPopulationGenes = annotatedPopulation;
    this.marg = marg;

  }

  public double getMarg() {
    return marg;
  }

  public TermId getTermId() {
    return termId;
  }

  public int getAnnotatedStudyGenes() {
    return annotatedStudyGenes;
  }

  public int getAnnotatedPopulationGenes() {
    return annotatedPopulationGenes;
  }
}
