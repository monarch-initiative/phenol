package org.monarchinitiative.phenol.io.scoredist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;

/**
 * Write score distribution to a table in an H2 database.
 *
 * <p>The database will be automatically created. If it exists, the table has to be re-created or
 * object initialization will fail.
 *
 * <h4>H2 Dependency Notes</h4>
 *
 * <p>The class itself only uses JDBC. Thus, the ontolib module does not depend on H2 via maven but
 * your calling code has to depend on H2.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class H2ScoreDistributionWriter implements ScoreDistributionWriter {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(H2ScoreDistributionWriter.class);

  /** Path to database. */
  private final String pathDb;

  /** Name of the table to use. */
  private final String tableName;

  /** Connection of the database to use. */
  private final Connection conn;

  /** H2 statement for dropping table. */
  private static final String H2_DROP_TABLE_STATEMENT = "DROP TABLE %s";

  /** H2 statement for creating table. */
  private static final String[] H2_CREATE_TABLE_STATEMENTS =
      new String[] {
        "CREATE TABLE %s (num_terms INT, entrez_id INT, sample_size INT, scores OTHER, p_values OTHER)",
        "CREATE INDEX ON %s (num_terms)",
        "CREATE UNIQUE INDEX ON %s (num_terms, entrez_id)"
      };

  /** H2 statement for insterting into table. */
  private static final String H2_INSERT_STATEMENT =
      "INSERT INTO %s (num_terms, entrez_id, sample_size, scores, p_values) VALUES (?, ?, ?, ?, ?)";

  /**
   * Object constructor.
   *
   * @param pathDb Path to the database to use.
   * @param dataTableName Name of the table to use.
   * @param resetTableIfExists Whether or not to reset the table if it already exists. Otherwise,
   *     {@link PhenolException} will be thrown.
   */
  public H2ScoreDistributionWriter(String pathDb, String dataTableName, boolean resetTableIfExists)
      throws PhenolException {
    this.pathDb = pathDb;
    this.tableName = dataTableName;
    this.conn = openConnection(resetTableIfExists);
  }

  /**
   * Open H2 database connection and perform initialization.
   *
   * @return Initialized {@link Connection} to H2 database.
   * @throws PhenolException if there was a problem with initialization (e.g., table existed but not
   *     {@code resetTableIfExists}.
   */
  private Connection openConnection(boolean resetTableIfExists) throws PhenolException {
    LOGGER.info("Opening connection to H2 database file at {} and configuring...", pathDb);
    // Open connection.
    final Connection resultConn;
    try {
      Class.forName("org.h2.Driver");
      resultConn = DriverManager.getConnection("jdbc:h2:" + pathDb, "", "");
    } catch (ClassNotFoundException e) {
      throw new PhenolException("H2 driver class could not be found", e);
    } catch (SQLException e) {
      throw new PhenolException("Could not open database at " + pathDb, e);
    }

    // Check whether the table already exists.
    final boolean tableExists;
    try (final ResultSet rs =
        resultConn
            .getMetaData()
            .getTables(null, null, tableName.toUpperCase(), new String[] {"TABLE"})) {
      tableExists = rs.next();
    } catch (SQLException e) {
      throw new PhenolException("Checking for table of name " + tableName + " failed", e);
    }

    if (tableExists) {
      if (!resetTableIfExists) {
        throw new PhenolException("Table exists but not allowed to reset!");
      }

      // DROP table
      LOGGER.info("Table {} exists, dropping.", tableName);
      final String sqlStmt = String.format(H2_DROP_TABLE_STATEMENT, tableName);
      LOGGER.info("Executing SQL statement: {}", sqlStmt);
      try (final PreparedStatement stmt = resultConn.prepareStatement(sqlStmt)) {
        stmt.executeUpdate();
      } catch (SQLException e) {
        throw new PhenolException("Could not drop table with statement " + sqlStmt, e);
      }
    }

    // CREATE table, add indices
    LOGGER.info("Creating table {} and indices...", tableName);
    for (String sql : H2_CREATE_TABLE_STATEMENTS) {
      final String sqlStmt = String.format(sql, tableName);
      LOGGER.info("Executing SQL statement: {}", sqlStmt);
      try (final PreparedStatement stmt = resultConn.prepareStatement(sqlStmt)) {
        stmt.executeUpdate();
      } catch (SQLException e) {
        throw new PhenolException("Could execute table creation/index statement: " + sqlStmt, e);
      }
    }

    LOGGER.info("Successfully connected and configured H2 database file.");

    return resultConn;
  }

  @Override
  public void close() throws IOException {
    try {
      conn.close();
    } catch (SQLException e) {
      throw new IOException("Problem closing connection to database", e);
    }
  }

  @Override
  public void write(int numTerms, ScoreDistribution scoreDistribution, int resolution)
      throws PhenolException {
    for (int objectId : scoreDistribution.getObjectIds()) {
      final ObjectScoreDistribution dist = scoreDistribution.getObjectScoreDistribution(objectId);
      writeObjectScoreDistribution(numTerms, dist, resolution);
    }
  }

  /**
   * Write score distribution for one number of terms and one object.
   *
   * @param numTerms Number of terms to write for.
   * @param dist {@link ObjectScoreDistribution} to write out.
   * @param resolution The resolution, {@code 0} for no change.
   * @throws PhenolException In case of problems when writing to database.
   */
  private void writeObjectScoreDistribution(
      int numTerms, ObjectScoreDistribution dist, int resolution) throws PhenolException {
    final double scores[];
    final double pValues[];

    final List<Double> observedScores = dist.observedScores();
    if (resolution != 0) {
      scores = new double[resolution + 1];
      pValues = new double[resolution + 1];

      for (int i = 0; i <= resolution; ++i) {
        final double pos = (((double) observedScores.size() - 1) / resolution) * i;
        final int left = Math.max(0, (int) Math.floor(pos));
        final int right = Math.min(observedScores.size() - 1, (int) Math.ceil(pos));
        final double dx = right - pos;
        final double score =
            observedScores.get(left)
                + (1 - dx) * (observedScores.get(right) - observedScores.get(left));
        scores[i] = score;
        pValues[i] = dist.estimatePValue(score);
      }
    } else {
      scores = new double[dist.getCumulativeFrequencies().size()];
      pValues = new double[dist.getCumulativeFrequencies().size()];

      int i = 0;
      for (Entry<Double, Double> e : dist.getCumulativeFrequencies().entrySet()) {
        scores[i] = e.getKey();
        pValues[i] = e.getValue();
      }
    }

    final String sqlStmt = String.format(H2_INSERT_STATEMENT, tableName);
    try (final PreparedStatement stmt = conn.prepareStatement(sqlStmt)) {
      stmt.setInt(1, numTerms);
      stmt.setInt(2, dist.getObjectId());
      stmt.setInt(3, dist.getSampleSize());
      stmt.setObject(4, scores);
      stmt.setObject(5, pValues);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new PhenolException("Problem with inserting into score distribution table", e);
    }
  }
}
