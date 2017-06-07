package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;

/**
 * Implementation of Jaccard similarity computation.
 *
 * @param <T>
 * @link Term} sub class to use in the contained classes
 * @param <R>
 *          {@link TermRelation} sub class to use in the contained classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JaccardSimilarityComputation<T extends Term,
    R extends TermRelation> implements SimilarityComputation {

  @Override
  public final double computeScore(final TermID t1, final TermID t2) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public final double computeScore(final Collection<TermID> query,
      final Collection<TermID> target) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public final double computeSymmetricScore(final Collection<TermID> query,
      final Collection<TermID> target) {
    // TODO Auto-generated method stub
    return 0;
  }

}
