package org.monarchinitiative.phenol.analysis.mgsa;


import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * GO Properties for term for term approach.
 *
 * @author Sebastian Bauer
 */

public class MgsaGOTermProperties {


  private final double marg;
  private final TermId termId;
  private final int annotatedStudyGenes;
  private final int annotatedPopulationGenes;

  public MgsaGOTermProperties(TermId tid, int annotatedStudy, int annotatedPopulation, double marg) {
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
