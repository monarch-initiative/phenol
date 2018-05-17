package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

// TODO: opposite aware computation is not implemented (yet)

/**
 * Implementation of cosine similarity.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class CosineSimilarity implements Similarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology ontology;

  /** Whether or not to compute in an opposite-aware fashion. */
  private final boolean oppositeAware;

  /**
   * Construct <code>CosineSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>By default, similarity is <b>not</b> opposite aware.
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public CosineSimilarity(Ontology ontology) {
    this(ontology, false);
  }

  /**
   * Construct <code>CosineSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param oppositeAware Whether or not to be opposite aware.
   */
  public CosineSimilarity(Ontology ontology, boolean oppositeAware) {
    this.ontology = ontology;
    this.oppositeAware = oppositeAware;
  }

  @Override
  public String getName() {
    return "Cosine similarity";
  }

  @Override
  public String getParameters() {
    return "{oppositeAware: " + oppositeAware + "}";
  }

  @Override
  public boolean isSymmetric() {
    return true;
  }

  @Override
  public double computeScore(Collection<TermId> query, Collection<TermId> target) {
    final Set<TermId> termIdsQuery = ontology.getAllAncestorTermIds(query, false);
    final Set<TermId> termIdsTarget = ontology.getAllAncestorTermIds(target, false);

    return Sets.intersection(termIdsQuery, termIdsTarget).size()
        / (Math.sqrt(termIdsQuery.size()) * Math.sqrt(termIdsTarget.size()));
  }
}
