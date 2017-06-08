package de.charite.compbio.ontolib.ontology.algo;

import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of Jaccard similarity computation.
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JaccardSimilarity<T extends Term, R extends TermRelation> implements ObjectSimilarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology<T, R> ontology;

  /** Whether or not to normalize score by union. */
  private final boolean normalizeByUnion;

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
   * @param normalizeByUnion Whether or not to normalize by union.
   */
  public JaccardSimilarity(Ontology<T, R> ontology, boolean normalizeByUnion) {
    this.ontology = ontology;
    this.normalizeByUnion = normalizeByUnion;
  }

  @Override
  public String getName() {
    return "Jaccard similarity";
  }

  @Override
  public String getParameters() {
    return "{normalizeByUnion: " + normalizeByUnion + "}";
  }

  @Override
  public boolean isSymmetric() {
    return true;
  }

  @Override
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    final Set<TermID> termIDsQuery = getAllTermIDs(query);
    final Set<TermID> termIDsTarget = getAllTermIDs(target);

    double intersectionSize = Sets.intersection(termIDsQuery, termIDsTarget).size();
    if (normalizeByUnion) {
      return intersectionSize / Sets.union(termIDsQuery, termIDsTarget).size();
    } else {
      return intersectionSize;
    }
  }

  /**
   * Return all the terms including all ancestors terms.
   *
   * <p>
   * Note that the root is not included!
   * </p>
   *
   * @param termIDs {@link Collection} of {@link TermID}s to gather all parents except for the root.
   * @return {@link Set} of {@link Term}s including all {@link Term}s from <code>terms</code>,
   *         including all ancestors, except for the root.
   */
  private Set<TermID> getAllTermIDs(Collection<TermID> termIDs) {
    final Set<TermID> resutl = new HashSet<>();
    for (TermID termID : termIDs) {
      resutl.add(termID);
      for (TermID ancestorID : ontology.getAncestors(termID)) {
        if (!ontology.isRootTerm(ancestorID)) {
          resutl.add(ancestorID);
        }
      }
    }
    return resutl;
  }

}
