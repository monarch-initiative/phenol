package org.monarchinitiative.phenol.io.scoredist;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Read score distributions from H2 database.
 *
 * <h4>H2 Dependency Notes</h4>
 *
 * <p>The class itself only uses JDBC. Thus, the ontolib module does not depend on H2 via maven but
 * your calling code has to depend on H2.
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

  /** H2 query for selecting all term counts. */
  private static final String H2_SELECT_TERM_COUNTS = "SELECT DISTINCT (term_count) from %s";

  /** H2 query for selecting by term count. */
  private static final String H2_SELECT_BY_TERM_COUNT_STATEMENT =
      "SELECT (term_count, object_id, scores, p_values) FROM % WHERE (term_count = ?)";

  /** H2 query for selecting by term count and object ID. */
  private static final String H2_SELECT_BY_TERM_COUNT_AND_OBJECT_STATEMENT =
      "SELECT (term_count, object_id, scores, p_values) FROM % WHERE (term_count = ? AND object_id = ?)";

  /**
   * Create new reader object.
   *
   * @param pathDb Path to H2 database to read from.
   * @param tableName Name of table to use for scores.
   * @throws PhenolException If there was a problem opening the H2 database connection.
   */
  public H2ScoreDistributionReader(String pathDb, String tableName) throws PhenolException {
    super();
    this.pathDb = pathDb;
    this.tableName = tableName;
    this.conn = openConnection();
  }

  /**
   * Open connection and perform checks.
   *
   * @return New {@link Connection} to H2 database.
   * @throws PhenolException In the case of problem with connecting.
   */
  private Connection openConnection() throws PhenolException {
    // Open connection.
    final Connection result;
    try {
      Class.forName("org.h2.Driver");
      result = DriverManager.getConnection("jdbc:h2:" + pathDb, "", "");
    } catch (ClassNotFoundException e) {
      throw new PhenolException("H2 driver class could not be found", e);
    } catch (SQLException e) {
      throw new PhenolException("Could not open database at " + pathDb, e);
    }

    // Check whether the table already exists.
    final boolean tableExists;
    try (final ResultSet rs =
        result.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"})) {
      tableExists = rs.next();
      if (!tableExists) {
        throw new PhenolException("Table of name " + tableName + " does not exist in database!");
      }
    } catch (SQLException e) {
      throw new PhenolException("Checking for table of name " + tableName + " failed", e);
    }

    return result;
  }

  @Override
  public ObjectScoreDistribution readForTermCountAndObject(int termCount, int objectId)
      throws PhenolException {
    try (final PreparedStatement stmt =
        conn.prepareStatement(
            String.format(H2_SELECT_BY_TERM_COUNT_AND_OBJECT_STATEMENT, tableName))) {
      stmt.setInt(1, termCount);
      stmt.setInt(2, objectId);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          return objectScoreDistributionFromResultSet(rs);
        }
      }
    } catch (SQLException e) {
      throw new PhenolException(
          "Problem with getting object score distribution for termCount: "
              + termCount
              + ", objectId: "
              + objectId);
    }

    throw new PhenolException(
        "Found no object for termCount: " + termCount + ", objectId: " + objectId);
  }

  /**
   * Build {@link ObjectScoreDistribution} from {@link ResultSet}.
   *
   * @param rs {@link ResultSet} to get data from.
   * @return {@link ObjectScoreDistribution} constructed from {@code rs}.
   * @throws SQLException In the case of a problem with retrieving the data.
   */
  private ObjectScoreDistribution objectScoreDistributionFromResultSet(ResultSet rs)
      throws SQLException {
    final int termCount = rs.getInt(1);
    final int objectId = rs.getInt(2);
    final int sampleSize = rs.getInt(3);
    final double[] scores = (double[]) rs.getObject(4);
    final double[] pValues = (double[]) rs.getObject(5);
    final TreeMap<Double, Double> scoreDist = new TreeMap<>();
    for (int i = 0; i < scores.length; ++i) {
      scoreDist.put(scores[i], pValues[i]);
    }
    return new ObjectScoreDistribution(termCount, objectId, sampleSize, scoreDist);
  }

  @Override
  public ScoreDistribution readForTermCount(int termCount) throws PhenolException {
    final Map<Integer, ObjectScoreDistribution> dists = new HashMap<>();

    try (final PreparedStatement stmt =
        conn.prepareStatement(String.format(H2_SELECT_BY_TERM_COUNT_STATEMENT, tableName))) {
      stmt.setInt(1, termCount);
      try (final ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          final ObjectScoreDistribution objScoreDist = objectScoreDistributionFromResultSet(rs);
          dists.put(objScoreDist.getObjectId(), objScoreDist);
        }
      }
    } catch (SQLException e) {
      throw new PhenolException(
          "Problem with getting object score distributions for termCount: " + termCount);
    }

    if (dists.size() == 0) {
      throw new PhenolException("Found no score distributions for termCount: " + termCount);
    } else {
      return new ScoreDistribution(termCount, dists);
    }
  }

  @Override
  public Map<Integer, ScoreDistribution> readAll() throws PhenolException {
    // Get all term counts.
    final List<Integer> termCounts = new ArrayList<>();
    try (final PreparedStatement stmt =
            conn.prepareStatement(String.format(H2_SELECT_TERM_COUNTS, tableName));
        final ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        termCounts.add(rs.getInt(1));
      }
    } catch (SQLException e) {
      throw new PhenolException("Problem querying the database for term counts", e);
    }

    // Query for all term counts.
    final Map<Integer, ScoreDistribution> result = new HashMap<>();
    for (int termCount : termCounts) {
      result.put(termCount, readForTermCount(termCount));
    }
    return result;
  }

  @Override
  public void close() throws IOException {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Problem closing connection to database", e);
    }
  }
}
