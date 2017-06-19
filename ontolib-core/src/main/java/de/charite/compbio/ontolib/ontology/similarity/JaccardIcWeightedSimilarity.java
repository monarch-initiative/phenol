package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of IC-weighted Jaccard similarity computation.
 *
 * <p>
 * This is compute similarly to Jaccard similarity. However, each term is weighted by its
 * information content and contributes the information content to the score sum.
 * </p>
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class JaccardIcWeightedSimilarity<T extends Term, R extends TermRelation>
    implements
      Similarity {

  /**
   * The {@link Ontology} to compute the similarity for.
   */
  private final Ontology<T, R> ontology;

  /**
   * {@link Map} from {@link TermId} to its information content.
   */
  private final Map<TermId, Double> termToIc;

  /** Whether or not to normalize score by union. */
  private final boolean normalized;

  /**
   * Construct <code>JaccardICWeightedSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>
   * By default, score will be normalized by union.
   * </p>
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param termToIc {@link Map} from {@link TermId} to information content.
   */
  public JaccardIcWeightedSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc) {
    this(ontology, termToIc, true);
  }

  /**
   * Construct <code>JaccardICWeightedSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param termToIc {@link Map} from {@link TermId} to information content.
   * @param normalized Whether or not to normalize by union.
   */
  public JaccardIcWeightedSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc,
      boolean normalized) {
    this.ontology = ontology;
    this.termToIc = termToIc;
    this.normalized = normalized;
  }

  @Override
  public String getName() {
    return "Jaccard IC-weighted similarity";
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
    final Set<TermId> querySet = Sets.newHashSet(ontology.getAllAncestorTermIds(query, false));
    final Set<TermId> targetSet = Sets.newHashSet(ontology.getAllAncestorTermIds(target, false));

    final double sum =
        Sets.intersection(querySet, targetSet).stream().mapToDouble(t -> termToIc.get(t)).sum();
    if (!normalized) {
      return sum;
    } else {
      return sum / Sets.union(querySet, targetSet).stream().mapToDouble(t -> termToIc.get(t)).sum();
    }
  }

}
