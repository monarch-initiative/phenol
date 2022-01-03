package org.monarchinitiative.phenol.analysis.scoredist;

import java.io.Closeable;
import java.io.Serializable;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;

/**
 * Interface for writing score distributions.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface ScoreDistributionWriter extends Closeable {

  /**
   * Write out score distribution with resolution of {@code 100}.
   *
   * @throws PhenolException In the case that there was a problem writing.
   * @see #write(int, ScoreDistribution, int)
   */
  default <T extends Serializable> void write(int numTerms, ScoreDistribution scoreDistribution) throws PhenolException {
    write(numTerms, scoreDistribution, 100);
  }

  /**
   * Write out score distribution.
   *
   * @param numTerms Number of terms that {@code scoreDistribution} was computed for.
   * @param scoreDistribution The actual score distribution.
   * @param resolution The number of points to write out from the resolution; {@code 0} for no
   *     resampling.
   * @throws PhenolException In the case that there was a problem writing.
   */
  <T extends Serializable> void write(int numTerms, ScoreDistribution scoreDistribution, int resolution)
      throws PhenolException;
}
