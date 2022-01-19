package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.utils.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * Implementation of simple feature vector similarity.
 *
 * <p>The simple feature vector similarity is defined as the number of shared terms in two sets of
 * terms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class SimpleFeatureVectorSimilarity implements Similarity {

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
  public double computeScore(Collection<TermId> query, Collection<TermId> target) {
    return Sets.intersection(Set.copyOf(query), Set.copyOf(target)).size();
  }
}
