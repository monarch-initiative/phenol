package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;

/**
 * Implementation of Lin similarity.
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class LinSimilarity<T extends Term, R extends TermRelation>
    extends AbstractCommonAncestorSimilarity<T, R> {

  /**
   * Constructor.
   *
   * <p>
   * The internally used {@link PairwiseSimilarity} is constructed from the given information
   * content mapping. This precomputation is done at construction of the
   * <code>ResnikSimilarity</code> object.
   * </p>
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from {@link TermId} to information content.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   */
  public LinSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc, boolean symmetric) {
    super(ontology, symmetric, new PairwiseResnikSimilarity<T, R>(ontology, termToIc));
  }


  /**
   * Constructor.
   *
   * <p>
   * By passing in the {@link PairwiseSimilarity} explicitely here, an implementation with
   * precomputation can be passed in explicitely, performing the precomputation explicitely earlier
   * instead of implicitely on object construction.
   * </p>
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   * @param pairwiseSimilarity {@link PairwiseSimilarity} to use internally.
   */
  public LinSimilarity(Ontology<T, R> ontology, boolean symmetric,
      PairwiseSimilarity pairwiseSimilarity) {
    super(ontology, symmetric, pairwiseSimilarity);
  }


  @Override
  public String getName() {
    return "Lin similarity";
  }

}
