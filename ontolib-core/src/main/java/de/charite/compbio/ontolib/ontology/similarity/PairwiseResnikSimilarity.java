package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;

/**
 * Implementation of pairwise Resnik similarity without precomputation.
 *
 * <p>
 * This lies at the core of most of of the more computationally expensive pairwise similarities'
 * computations. If you want to precompute the pairwise similarity scores (computationally very
 * expensive), consider using {@link PrecomputingPairwiseResnikSimilarity}.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class PairwiseResnikSimilarity<T extends Term, R extends TermRelation>
    extends AbstractPairwiseResnikSimilarity<T, R> {

  /**
   * Construct new {@link PairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc) {
    super(ontology, termToIc);
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    return computeScoreImpl(query, target);
  }

}
