package com.github.phenomics.ontolib.cli;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.github.phenomics.ontolib.base.OntoLibException;
import com.github.phenomics.ontolib.ontology.scoredist.ObjectScoreDistribution;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;

/**
 * Read score distributions from H2 database.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class H2ScoreDistributionReader implements ScoreDistributionReader {

  /** Path to database. */
  private final String pathDb;

  /** Name of the table to use. */
  private final String tableName;

  /** Connection of the database to use. */
  private final Connection conn;

  /** H2 statement for dropping table. */
  private final static String H2_SELECT_TABLE_STATEMENT =
      "SELECT (term_count, object_id, scores, p_values) FROM % WHERE (term_count = ? AND object_id = ?)";

  /**
   * Create new reader object.
   *
   * @param pathDb Path to H2 database to read from.
   * @param tableName Name of table to use for scores.
   * @throws OntoLibException If there was a problem opening the H2 database connection.
   */
  public H2ScoreDistributionReader(String pathDb, String tableName) throws OntoLibException {
    super();
    this.pathDb = pathDb;
    this.tableName = tableName;
    this.conn = openConnection();
  }

  /**
   * Open connection and perform checks.
   *
   * @return New {@link Connection} to H2 database.
   * @throws OntoLibException In the case of problem with connecting.
   */
  private Connection openConnection() throws OntoLibException {
    // Open connection.
    final Connection result;
    try {
      Class.forName("org.h2.Driver");
      result = DriverManager.getConnection("jdbc:h2:" + pathDb, "", "");
    } catch (ClassNotFoundException e) {
      throw new OntoLibException("H2 driver class could not be found", e);
    } catch (SQLException e) {
      throw new OntoLibException("Could not open database at " + pathDb, e);
    }

    // Check whether the table already exists.
    final boolean tableExists;
    try (final ResultSet rs =
        result.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"})) {
      tableExists = rs.next();
      if (!tableExists) {
        throw new OntoLibException("Table of name " + tableName + " does not exist in database!");
      }
    } catch (SQLException e) {
      throw new OntoLibException("Checking for table of name " + tableName + " failed", e);
    }

    return result;
  }

  @Override
  public ObjectScoreDistribution readForTermCountAndObject(int termCount, int objectId)
      throws OntoLibException {
    try (final PreparedStatement stmt = conn.prepareStatement(H2_SELECT_TABLE_STATEMENT)) {
      stmt.setInt(1, termCount);
      stmt.setInt(2, objectId);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final int sampleSize = rs.getInt(3);
          final double[] scores = (double[]) rs.getObject(4);
          final double[] pValues = (double[]) rs.getObject(5);
          final TreeMap<Double, Double> scoreDist = new TreeMap<Double, Double>();
          for (int i = 0; i < scores.length; ++i) {
            scoreDist.put(scores[i], pValues[i]);
          }
          return new ObjectScoreDistribution(termCount, objectId, sampleSize, scoreDist);
        }
      }
    } catch (SQLException e) {
      throw new OntoLibException("Problem with getting object score distribution for termCount: "
          + termCount + ", objectId: " + objectId);
    }

    throw new OntoLibException(
        "Found no object for termCount: " + termCount + ", objectId: " + objectId);
  }

  @Override
  public ScoreDistribution readForTermCount(int termCount) throws OntoLibException {
    final Map<Integer, ObjectScoreDistribution> dists = new HashMap<>();

    try (final PreparedStatement stmt = conn.prepareStatement(H2_SELECT_TABLE_STATEMENT)) {
      stmt.setInt(1, termCount);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final int objectId = rs.getInt(2);
          final int sampleSize = rs.getInt(3);
          final double[] scores = (double[]) rs.getObject(4);
          final double[] pValues = (double[]) rs.getObject(5);
          final TreeMap<Double, Double> scoreDist = new TreeMap<Double, Double>();
          for (int i = 0; i < scores.length; ++i) {
            scoreDist.put(scores[i], pValues[i]);
          }
          dists.put(objectId,
              new ObjectScoreDistribution(termCount, objectId, sampleSize, scoreDist));
        }
      }
    } catch (SQLException e) {
      throw new OntoLibException(
          "Problem with getting object score distributions for termCount: " + termCount);
    }

    if (dists.size() == 0) {
      throw new OntoLibException("Found no score distributions for termCount: " + termCount);
    } else {
      return new ScoreDistribution(termCount, dists);
    }
  }

  @Override
  public Map<Integer, ScoreDistribution> readAll() throws OntoLibException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

}
