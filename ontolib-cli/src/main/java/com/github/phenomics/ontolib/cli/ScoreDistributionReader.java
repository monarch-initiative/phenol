package com.github.phenomics.ontolib.cli;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import com.github.phenomics.ontolib.base.OntoLibException;
import com.github.phenomics.ontolib.ontology.scoredist.ObjectScoreDistribution;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;

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
   * @throws IOException In the case of problems when reading and parsing.
   */
  ObjectScoreDistribution readForTermCountAndObject(int termCount, int objectId)
      throws OntoLibException;

  /**
   * Read and return entries for score distribution given a specific {@code termCount}.
   *
   * @param termCount The number of terms to return the score distribution for.
   * @return {@link ScoreDistribution} for the given {@code termCount}.
   * @throws IOException In the case of problems when reading or parsing.
   */
  ScoreDistribution readForTermCount(int termCount) throws OntoLibException;

  /**
   * Read all entries and return mapping from term count to {@link ScoreDistribution} object.
   *
   * @return Resulting score distributions from the file.
   * @throws IOException In the case of problems when reading or parsing.
   */
  Map<Integer, ScoreDistribution> readAll() throws OntoLibException;

  void close() throws IOException;

}