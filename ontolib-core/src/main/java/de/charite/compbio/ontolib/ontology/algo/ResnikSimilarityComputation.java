package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import de.charite.compbio.ontolib.ontology.similarity.ObjectSimilarity;
import java.util.Collection;
import java.util.Map;

/**
 * Implementation of Resnik similarity computation.
 *
 * @param <T> {@link Term} sub class to use in the {@link Ontology}
 * @param <R> {@link TermRelation} sub class to use in the {@link Ontology}
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ResnikSimilarityComputation<T extends Term, R extends TermRelation>
    implements
      ObjectSimilarity {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSymmetric() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public double computeScore(Collection<TermID> query, Collection<TermID> target) {
    // TODO Auto-generated method stub
    return 0;
  }

}
