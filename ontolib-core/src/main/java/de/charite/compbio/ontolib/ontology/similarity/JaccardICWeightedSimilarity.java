package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
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
public final class JaccardICWeightedSimilarity<T extends Term, R extends TermRelation>
    implements
      ObjectSimilarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology<T, R> ontology;

  /** {@link Map} from {@link TermID} to its information content. */
  private final Map<TermID, Double> termToIC;

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
   * @param termToIC {@link Map} from {@link TermID} to information content.
   */
  public JaccardICWeightedSimilarity(Ontology<T, R> ontology, Map<TermID, Double> termToIC) {
    this(ontology, termToIC, true);
  }

  /**
   * Construct <code>JaccardICWeightedSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param termToIC {@link Map} from {@link TermID} to information content.
   * @param normalized Whether or not to normalize by union.
   */
  public JaccardICWeightedSimilarity(Ontology<T, R> ontology, Map<TermID, Double> termToIC,
      boolean normalized) {
    this.ontology = ontology;
    this.termToIC = termToIC;
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
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    final Set<TermID> querySet = Sets.newHashSet(ontology.getAllAncestorTermIDs(query, false));
    final Set<TermID> targetSet = Sets.newHashSet(ontology.getAllAncestorTermIDs(target, false));

    final double sum =
        Sets.intersection(querySet, targetSet).stream().mapToDouble(t -> termToIC.get(t)).sum();
    if (!normalized) {
      return sum;
    } else {
      return sum / Sets.union(querySet, targetSet).stream().mapToDouble(t -> termToIC.get(t)).sum();
    }
  }

}
