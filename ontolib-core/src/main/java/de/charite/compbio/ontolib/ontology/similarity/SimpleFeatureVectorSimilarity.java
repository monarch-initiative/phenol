package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;

/**
 * Implementation of simple feature vector similarity.
 *
 * <p>
 * The simple feature vector similarity is defined as the number of shared terms in two sets of
 * terms.
 * </p>
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class SimpleFeatureVectorSimilarity<T extends Term, R extends TermRelation>
    implements
      Similarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology<T, R> ontology;

  /**
   * Construct <code>SimpleFeatureVectorSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public SimpleFeatureVectorSimilarity(Ontology<T, R> ontology) {
    this.ontology = ontology;
  }

  @Override
  public String getName() {
    return "Simple feature vector similarity";
  }

  @Override
  public String getParameters() {
    return "{}";
  }

  @Override
  public boolean isSymmetric() {
    return true;
  }

  @Override
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    return Sets.intersection(Sets.newHashSet(query), Sets.newHashSet(target)).size();
  }

}
