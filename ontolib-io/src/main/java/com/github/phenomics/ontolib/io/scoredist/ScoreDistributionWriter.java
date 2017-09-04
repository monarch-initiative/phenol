package com.github.phenomics.ontolib.io.scoredist;

import java.io.Closeable;

import com.github.phenomics.ontolib.base.OntoLibException;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;

/**
 * Interface for writing score distributions.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface ScoreDistributionWriter extends Closeable {

  /**
   * Write out score distribution with resolution of {@code 100}.
   * 
   * @throws OntoLibException In the case that there was a problem writing.
   * 
   * @see #write(int, ScoreDistribution, int)
   */
  default void write(int numTerms, ScoreDistribution scoreDistribution) throws OntoLibException {
    write(numTerms, scoreDistribution, 100);
  }

  /**
   * Write out score distribution.
   *
   * @param numTerms Number of terms that {@code scoreDistribution} was computed for.
   * @param scoreDistribution The actual score distribution.
   * @param resolution The number of points to write out from the resolution; {@code 0} for no
   *        resampling.
   * @throws OntoLibException In the case that there was a problem writing.
   */
  void write(int numTerms, ScoreDistribution scoreDistribution, int resolution)
      throws OntoLibException;

}