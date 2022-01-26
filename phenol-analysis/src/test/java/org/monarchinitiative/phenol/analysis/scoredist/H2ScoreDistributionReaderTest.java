package org.monarchinitiative.phenol.analysis.scoredist;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.monarchinitiative.phenol.analysis.scoredist.H2ScoreDistributionReader;
import org.monarchinitiative.phenol.analysis.scoredist.H2ScoreDistributionWriter;
import org.monarchinitiative.phenol.analysis.scoredist.ScoreDistributionReader;
import org.monarchinitiative.phenol.analysis.scoredist.ScoreDistributionWriter;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;


public class H2ScoreDistributionReaderTest {

  @TempDir
  public Path tempDir;

  private ScoreDistributionReader objDbReader;

  @BeforeEach
  public void setUp() throws Exception {
    Path dbpath = tempDir.resolve("db.h2");
    String dataTableName = "TempTable";
    ScoreDistributionWriter dbWriter = new H2ScoreDistributionWriter(dbpath.toFile().getAbsolutePath(), dataTableName, true);
    objDbReader = new H2ScoreDistributionReader(dbpath.toFile().getAbsolutePath(), dataTableName);

    TermId termId = TermId.of("MONDO", "001");
    SortedMap<Double, Double> cumulativeFrequencies = new TreeMap<>(Map.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0));
    ObjectScoreDistribution scoreDistribution = new ObjectScoreDistribution(termId, 2, 3, cumulativeFrequencies);
    Map<TermId, ObjectScoreDistribution> scoreDistributionMap = Map.of(termId, scoreDistribution);
    ScoreDistribution objScoreDistribution = new ScoreDistribution(2, scoreDistributionMap);

    dbWriter.write(3, objScoreDistribution);
    dbWriter.close();
  }

  @AfterEach
  public void tearDown() throws IOException {
    objDbReader.close();
  }

  @Test
  public void readForTermCountAndObject() throws Exception {
    TermId termId = TermId.of("MONDO", "001");
    ObjectScoreDistribution objScoreDist = objDbReader.readForTermCountAndObject(3, termId);
    assertEquals(objScoreDist.getObjectId(), termId);
  }

  @Test
  public void readForTermCount() throws Exception {
    ScoreDistribution objScoreDist = objDbReader.readForTermCount(3);
    assertEquals(objScoreDist.getObjectIds().size(), 1);
  }

  @Test
  public void readAll() throws Exception {
    Map<Integer, ScoreDistribution> results = objDbReader.readAll();
    assertEquals(results.size(), 1);
  }

  @Test
  public void close() {
    assertDoesNotThrow(() -> objDbReader.close());
  }

}
