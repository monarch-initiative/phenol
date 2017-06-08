package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.Map;

// TODO(holtgrewe): we will require caching to make this fast enough

/**
 * Abstract base class for similarity measures computed based on information content of common
 * ancestors, such as Resnik, Lin, and Jiang similarities.
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
abstract class AbstractCommonAncestorSimilarity<T extends Term, R extends TermRelation>
    implements
      Similarity {

  /** {@link Ontology} underlying the computation. */
  private final Ontology<T, R> ontology;

  /** {@link Map} from {@link TermID} to its information content. */
  private final Map<TermID, Double> termToIC;

  /** Whether or not to use symmetric score flavor (arithmetic mean of both directions). */
  private final boolean symmetric;

  /** Pairwise term similarity computation. */
  private final PairwiseSimilarity pairwiseSimilarity;

  /**
   * Constructor.
   * 
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIC {@link Map} from {@link TermID} to information content.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   */
  public AbstractCommonAncestorSimilarity(Ontology<T, R> ontology, Map<TermID, Double> termToIC,
      boolean symmetric) {
    this.ontology = ontology;
    this.termToIC = termToIC;
    this.symmetric = symmetric;
    this.pairwiseSimilarity = buildPairwiseSimilarity(this.ontology, this.termToIC);
  }

  @Override
  public final double computeScore(Collection<TermID> query, Collection<TermID> target) {
    if (symmetric) {
      return 0.5 * (computeScoreImpl(query, target) + computeScoreImpl(target, query));
    } else {
      return computeScoreImpl(query, target);
    }
  }

  /**
   * Compute directed score between a query and a target set of {@link TermID}s.
   *
   * @param query Query set of {@link TermID}s.
   * @param target Target set of {@link TermID}s
   * @return Symmetric similarity score between <code>query</code> and <code>target</code>.
   */
  protected final double computeScoreImpl(Collection<TermID> query, Collection<TermID> target) {
    double sum = 0;

    for (TermID q : query) {
      double maxValue = 0.0;
      for (TermID t : target) {
        maxValue = Math.max(maxValue, pairwiseSimilarity.computeScore(q, t));
      }
      sum += maxValue;
    }

    return sum / query.size();
  }

  /**
   * @return Whether score computation is to be symmetric.
   */
  public final boolean isSymmetric() {
    return symmetric;
  }

  /**
   * Override in sub classes to get pairwise similarity computation.
   *
   * @param ontology {@link Ontology} to use for pairwise computation.
   * @param termToIC {@link Map} from {@link TermID} to information content to use.
   * @return Pairwise term similarity, to use in subclasses.
   */
  protected abstract PairwiseSimilarity buildPairwiseSimilarity(Ontology<T, R> ontology,
      Map<TermID, Double> termToIC);

  public String getParameters() {
    return "{symmetric: " + this.isSymmetric() + "}";
  }

}
