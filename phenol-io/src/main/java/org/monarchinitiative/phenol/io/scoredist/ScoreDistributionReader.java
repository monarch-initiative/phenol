package org.monarchinitiative.phenol.io.scoredist;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;

/**
 * Interface for score distribution reading.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface ScoreDistributionReader extends Closeable {

  /**
   * Read for the given {@code termCount} and {@code objectId}.
   *
   * @param termCount The number of terms to read for.
   * @param objectId The "world object" ID to read for.
   * @return {@link ObjectScoreDistribution} with the empirical distribution for the query.
   * @throws PhenolException In the case of problems when reading and parsing.
   */
  ObjectScoreDistribution readForTermCountAndObject(int termCount, int objectId)
      throws PhenolException;

  /**
   * Read and return entries for score distribution given a specific {@code termCount}.
   *
   * @param termCount The number of terms to return the score distribution for.
   * @return {@link ScoreDistribution} for the given {@code termCount}.
   * @throws PhenolException In the case of problems when reading or parsing.
   */
  ScoreDistribution readForTermCount(int termCount) throws PhenolException;

  /**
   * Read all entries and return mapping from term count to {@link ScoreDistribution} object.
   *
   * @return Resulting score distributions from the file.
   * @throws PhenolException In the case of problems when reading or parsing.
   */
  Map<Integer, ScoreDistribution> readAll() throws PhenolException;

  void close() throws IOException;
}
