package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;

/**
 * Implementation of information content similarity.
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class InformationContentSimilarity<T extends Term, R extends TermRelation>
    implements
      ObjectSimilarity {

  /** The {@link Ontology} to compute the similarity for. */
  private final Ontology<T, R> ontology;

  /** Whether or not to use symmetric computation. */
  private final boolean symmetric;

  /**
   * Construct <code>CosineSimilarity</code> object for the given {@link Ontology}.
   *
   * <p>
   * By default, similarity is <b>not</b> symmetric.
   * </p>
   *
   * @param ontology {@link Ontology} to base the computation on.
   */
  public InformationContentSimilarity(Ontology<T, R> ontology) {
    this(ontology, false);
  }

  /**
   * Construct <code>JaccardSimilarity</code> object for the given {@link Ontology}.
   *
   * @param ontology {@link Ontology} to base the computation on.
   * @param symmetric Whether or not to compute in symmetric fashion.
   */
  public InformationContentSimilarity(Ontology<T, R> ontology, boolean symmetric) {
    this.ontology = ontology;
    this.symmetric = symmetric;
  }

  @Override
  public String getName() {
    return "Cosine similarity";
  }

  @Override
  public String getParameters() {
    return "{symmetric: " + symmetric + "}";
  }

  @Override
  public boolean isSymmetric() {
    return symmetric;
  }

  @Override
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    // TODO Auto-generated method stub
    return 0;
  }

}
