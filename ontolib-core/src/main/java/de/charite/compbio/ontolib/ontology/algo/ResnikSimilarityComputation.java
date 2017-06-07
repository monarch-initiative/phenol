package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;

/**
 * Implementation of Resnik similarity computation.
 *
 * @param <T>
 *          {@link Term} sub class to use in the {@link Ontology}
 * @param <R>
 *          {@link TermRelation} sub class to use in the {@link Ontology}
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ResnikSimilarityComputation<T extends Term, R extends TermRelation>
    extends SimilarityComputationBase {

  /**
   * Construct computation object for Resnik's similarity measure.
   *
   * @param ontology
   *          The {@link Ontology} to base the computation on
   * @param informationContent
   *          Label for each {@link Term} in <code>ontology</code> with the
   *          information content
   */
  public ResnikSimilarityComputation(final Ontology<T, R> ontology,
      final Map<TermID, Double> informationContent) {
    super(new ResnikOneToOneSimilarityComputation<T, R>(ontology,
        informationContent));
  }

}
