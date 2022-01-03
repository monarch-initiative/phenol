package org.monarchinitiative.phenol.scoredist;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;


import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;


public class H2ScoreDistributionWriterTest {

  @TempDir
  public Path tempDir;

  private ScoreDistributionWriter dbWriter;

  @BeforeEach
  public void setUp() throws Exception {
    Path dbpath = tempDir.resolve("db.h2");
    String dataTableName = "tempTable";
    dbWriter = new H2ScoreDistributionWriter(dbpath.toFile().getAbsolutePath(), dataTableName, true);
  }

  @AfterEach
  public void tearDown() throws IOException {
    dbWriter.close();
  }

  @Test
  public void close() {
    assertDoesNotThrow(() -> dbWriter.close());
  }

  @Test
  public void write_int_key_scoredistribution() throws Exception{

    Map<TermId, ObjectScoreDistribution> scoreDistributionMap = new HashMap<>();
    TermId tid1 = TermId.of("HP:1");

    SortedMap<Double, Double> sortedMap = new TreeMap<>(Map.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0));
    scoreDistributionMap.put(tid1, new ObjectScoreDistribution(tid1, 2, 3, sortedMap));

    ScoreDistribution integerScoreDistribution = new ScoreDistribution(2, scoreDistributionMap);

    assertDoesNotThrow(() -> dbWriter.write(2, integerScoreDistribution));

  }

  @Test
  public void write_obj_key_scoredistribution() throws Exception{

    TermId termId = TermId.of("MONDO", "001");
    Map<TermId, ObjectScoreDistribution> scoreDistributionMap = new HashMap<>();

    SortedMap<Double, Double> sortedMap = new TreeMap<>(Map.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0));
    scoreDistributionMap.put(termId, new ObjectScoreDistribution(termId, 2, 3, sortedMap));

    ScoreDistribution objScoreDistribution = new ScoreDistribution(2, scoreDistributionMap);

    assertDoesNotThrow(() -> dbWriter.write(2, objScoreDistribution));
  }

}
