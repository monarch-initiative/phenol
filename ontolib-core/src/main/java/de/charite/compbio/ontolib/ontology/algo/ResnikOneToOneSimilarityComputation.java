package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;

/**
 * Implementation of Resnik's similarity measure between two terms.
 *
 * @param <T> {@link Term} sub class to use in {@link Ontology}
 * @param <R> {@link TermRelation} sub class to use in {@link Ontology}
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class ResnikOneToOneSimilarityComputation<T extends Term, R extends TermRelation>
    implements
      OneToOneSimilarityComputation {

  /** The ontology to base the similarity computation on. */
  private final Ontology<T, R> ontology;

  /**
   * Information content for each term in the {@link #ontology} as required by Resnik's similarity
   * measure.
   */
  private final Map<TermID, Double> informationContent;

  /**
   * Construct computation object for Resnik's similarity measure.
   *
   * @param pOntology The {@link Ontology} to base the computation on
   * @param pInformationContent Label for each {@link Term} in <code>ontology</code> with the
   *        information content
   */
  ResnikOneToOneSimilarityComputation(final Ontology<T, R> pOntology,
      final Map<TermID, Double> pInformationContent) {
    this.ontology = pOntology;
    this.informationContent = pInformationContent;
  }

  @Override
  public double computeScore(final TermID t1, final TermID t2) {
    throw new RuntimeException("Implement me!");
  }

  /**
   * @return Ownning {@link Ontology.
   */
  protected Ontology<T, R> getOntology() {
    return ontology;
  }

  /**
   * @return {@link Map} from {@link TermID} to information content.
   */
  protected Map<TermID, Double> getInformationContent() {
    return informationContent;
  }

}
