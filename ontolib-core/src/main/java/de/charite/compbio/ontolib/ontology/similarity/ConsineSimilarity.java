package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.Set;

// TODO: opposite aware computation is not implemented (yet)

/**
 * Implementation of cosine similarity.
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ConsineSimilarity<T extends Term, R extends TermRelation>
    implements
      ObjectSimilarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology<T, R> ontology;

  /** Whether or not to compute in an opposite-aware fashion. */
  private final boolean oppositeAware;

  /**
   * Construct <code>CosineSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>
   * By default, similarity is <b>not</b> opposite aware.
   * </p>
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public ConsineSimilarity(Ontology<T, R> ontology) {
    this(ontology, false);
  }

  /**
   * Construct <code>CosineSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param oppositeAware Whether or not to be opposite aware.
   */
  public ConsineSimilarity(Ontology<T, R> ontology, boolean oppositeAware) {
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
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    final Set<TermID> termIDsQuery = ontology.getAllAncestorTermIDs(query, false);
    final Set<TermID> termIDsTarget = ontology.getAllAncestorTermIDs(target, false);

    return Sets.intersection(termIDsQuery, termIDsTarget).size()
        / (Math.sqrt(termIDsQuery.size()) * Math.sqrt(termIDsTarget.size()));
  }

}
