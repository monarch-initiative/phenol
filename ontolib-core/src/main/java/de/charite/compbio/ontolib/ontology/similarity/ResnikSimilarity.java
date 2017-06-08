package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;

/**
 * Implementation of Resnik similarity.
 *
 * @param <T> {@link Term} sub class to use in the contained classes
 * @param <R> {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ResnikSimilarity<T extends Term, R extends TermRelation>
    extends AbstractCommonAncestorSimilarity<T, R> {

  /**
   * Constructor.
   * 
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIC {@link Map} from {@link TermID} to information content.
   * @param symmetric Whether or not to compute score in symmetric fashion.
   */
  public ResnikSimilarity(Ontology<T, R> ontology, Map<TermID, Double> termToIC,
      boolean symmetric) {
    super(ontology, termToIC, symmetric);
  }

  @Override
  public String getName() {
    return "Resnik similarity";
  }

  @Override
  protected PairwiseSimilarity buildPairwiseSimilarity(Ontology<T, R> ontology,
      Map<TermID, Double> termToIC) {
    return new PairwiseResnikSimilarity<T, R>(ontology, termToIC);
  }

}
