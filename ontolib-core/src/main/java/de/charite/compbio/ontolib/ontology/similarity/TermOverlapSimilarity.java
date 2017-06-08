package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.Set;

/**
 * Implementation of term overlap similarity.
 *
 * <p>
 * The term overlap similarity is computed from two sets of terms by first adding all of their
 * ancestors except the root and counting the intersection size. When normalizing, this count is
 * divided by the size of the smaller set, otherwise the score is the number of terms in the
 * intersection.
 * </p>
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class TermOverlapSimilarity<T extends Term, R extends TermRelation>
    implements
      ObjectSimilarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology<T, R> ontology;

  /** Whether or not to compute in a normalized fashion. */
  private final boolean normalized;

  /**
   * Construct <code>TermOverlapSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>
   * By default, similarity is normalized by smaller set size.
   * </p>
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public TermOverlapSimilarity(Ontology<T, R> ontology) {
    this(ontology, true);
  }

  /**
   * Construct <code>TermOverlapSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param normalized Whether or not to normalize by smaller set size.
   */
  public TermOverlapSimilarity(Ontology<T, R> ontology, boolean normalized) {
    this.ontology = ontology;
    this.normalized = normalized;
  }

  @Override
  public String getName() {
    return "Term overlap similarity";
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
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    final Set<TermID> termIDsQuery = ontology.getAllAncestorTermIDs(query, false);
    final Set<TermID> termIDsTarget = ontology.getAllAncestorTermIDs(target, false);

    double overlap = Sets.intersection(termIDsQuery, termIDsTarget).size();
    if (!normalized) {
      return overlap;
    } else {
      return overlap / Math.min(termIDsQuery.size(), termIDsTarget.size());
    }
  }

}
