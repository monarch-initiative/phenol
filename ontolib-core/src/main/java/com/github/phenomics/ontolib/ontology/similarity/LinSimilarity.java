package com.github.phenomics.ontolib.ontology.similarity;

import java.util.Map;

import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.github.phenomics.ontolib.ontology.data.Term;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermRelation;

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
    super(new PairwiseResnikSimilarity<T, R>(ontology, termToIc), symmetric);
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
   * @param pairwiseSimilarity {@link PairwiseSimilarity} to use internally.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   */
  public LinSimilarity(PairwiseSimilarity pairwiseSimilarity, boolean symmetric) {
    super(pairwiseSimilarity, symmetric);
  }


  @Override
  public String getName() {
    return "Lin similarity";
  }

}
