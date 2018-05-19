package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * Implementation of Jaccard similarity computation.
 *
 * <p>For computing Jaccard similarity of two sets of terms, the sets are first extended by all
 * ancestors except for the root term. Then, the size of the intersection is divided by the size of
 * the union. Optionally, normalization by size of the union can be deactivated.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class JaccardSimilarity implements Similarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology ontology;

  /** Whether or not to normalize score by union. */
  private final boolean normalized;

  /**
   * Construct <code>JaccardSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>By default, score will be normalized by union.
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public JaccardSimilarity(Ontology ontology) {
    this(ontology, true);
  }

  /**
   * Construct <code>JaccardSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param normalized Whether or not to normalize by union.
   */
  public JaccardSimilarity(Ontology ontology, boolean normalized) {
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
