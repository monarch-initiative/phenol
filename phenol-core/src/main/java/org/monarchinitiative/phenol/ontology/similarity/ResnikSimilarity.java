package org.monarchinitiative.phenol.ontology.similarity;

import java.util.Map;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.RelationshipI;
import org.monarchinitiative.phenol.ontology.data.TermI;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Implementation of Resnik similarity.
 *
 * @param <T> {@link TermI} sub class to use in the contained classes
 * @param <R> {@link RelationshipI} sub class to use in the contained classes
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ResnikSimilarity<T extends TermI, R extends RelationshipI>
    extends AbstractCommonAncestorSimilarity<T, R> {

  /**
   * Constructor.
   *
   * <p>The internally used {@link PrecomputingPairwiseResnikSimilarity} is constructed from the
   * given information content mapping using {@link PairwiseResnikSimilarity}. In case that you want
   * to use perform this precomputation explicitely, use ResnikSimilarity(Ontology,
   * PairwiseSimilarity, boolean) with {@link PrecomputingPairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from {@link TermId} to information content.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   */
  public ResnikSimilarity(
      Ontology<T, R> ontology, Map<TermId, Double> termToIc, boolean symmetric) {
    super(new PrecomputingPairwiseResnikSimilarity<T, R>(ontology, termToIc), symmetric);
  }

  /**
   * Constructor.
   *
   * <p>By passing in the {@link PairwiseSimilarity} explicitely here, an implementation with
   * precomputation can be passed in explicitely, performing the precomputation explicitely earlier
   * instead of implicitely on object construction.
   *
   * @param pairwiseSimilarity {@link PairwiseSimilarity} to use internally.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   */
  public ResnikSimilarity(PairwiseSimilarity pairwiseSimilarity, boolean symmetric) {
    super(pairwiseSimilarity, symmetric);
  }

  @Override
  public String getName() {
    return "Resnik similarity";
  }
}
