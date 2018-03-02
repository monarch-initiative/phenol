package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermRelation;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * Implementation of Jaccard similarity computation.
 *
 * <p>
 * For computing Jaccard similarity of two sets of terms, the sets are first extended by all
 * ancestors except for the root term. Then, the size of the intersection is divided by the size of
 * the union. Optionally, normalization by size of the union can be deactivated.
 * </p>
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class JaccardSimilarity<T extends Term, R extends TermRelation>
    implements
      Similarity {

  /**
   * The {@link Ontology} to compute the similarity for.
   */
  private final Ontology<T, R> ontology;

  /** Whether or not to normalize score by union. */
  private final boolean normalized;

  /**
   * Construct <code>JaccardSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>
   * By default, score will be normalized by union.
   * </p>
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public JaccardSimilarity(Ontology<T, R> ontology) {
    this(ontology, true);
  }

  /**
   * Construct <code>JaccardSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param normalized Whether or not to normalize by union.
   */
  public JaccardSimilarity(Ontology<T, R> ontology, boolean normalized) {
    this.ontology = ontology;
    this.normalized = normalized;
  }

  @Override
  public String getName() {
    return "Jaccard similarity";
  }

  @Override
  public String getParameters() {
    return "{normalized: " + normalized + "}";
  }

  @Override
  public boolean isSymmetric() {
    return true;
  }

  @Override
  public double computeScore(Collection<TermId> query, Collection<TermId> target) {
    final Set<TermId> termIdsQuery = ontology.getAllAncestorTermIds(query, false);
    final Set<TermId> termIdsTarget = ontology.getAllAncestorTermIds(target, false);

    double intersectionSize = Sets.intersection(termIdsQuery, termIdsTarget).size();
    if (normalized) {
      return intersectionSize / Sets.union(termIdsQuery, termIdsTarget).size();
    } else {
      return intersectionSize;
    }
  }


}
