package org.monarchinitiative.phenol.io.scoredist;

import com.google.common.collect.ImmutableSortedMap;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;


import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.*;

/*
@ExtendWith(TempDirectory.class)
class H2ScoreDistributionWriterTest {

  private ScoreDistribution<Integer> integerScoreDistribution;
  private ScoreDistribution<TermId> objScoreDistribution;
  private static ScoreDistributionWriter dbWriter;

  @BeforeEach
  void setUp(@TempDir Path tempDir) throws Exception {

    Path dbpath = tempDir.resolve("db.h2");
    System.out.println(dbpath.toFile().getAbsolutePath());
    String dataTableName = "tempTable";
    dbWriter = new H2ScoreDistributionWriter(dbpath.toFile().getAbsolutePath(), dataTableName, true);

  }

  @Test
  void testNotNull(){
    assertNotNull(dbWriter);
  }

  @Test
  void close() throws Exception {
    assertDoesNotThrow(() -> dbWriter.close());
  }

  @Test
  void write_int_key_scoredistribution() throws Exception{

    Map<Integer, ObjectScoreDistribution<Integer>> scoreDistributionMap = new HashMap<>();

    SortedMap<Double, Double> sortedMap = ImmutableSortedMap.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0);
    scoreDistributionMap.put(1, new ObjectScoreDistribution<>((Integer) 1, 2, 3, sortedMap));

    integerScoreDistribution = new ScoreDistribution<>(2, scoreDistributionMap);

    Assertions.assertDoesNotThrow(() -> dbWriter.write(2, integerScoreDistribution));

  }

  @Test
  void write_obj_key_scoredistribution() throws Exception{

    TermId termId = TermId.of("MONDO", "001");
    Map<TermId, ObjectScoreDistribution<TermId>> scoreDistributionMap = new HashMap<>();

    SortedMap<Double, Double> sortedMap = ImmutableSortedMap.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0);
    scoreDistributionMap.put(termId, new ObjectScoreDistribution<>(termId, 2, 3, sortedMap));

    objScoreDistribution = new ScoreDistribution<>(2, scoreDistributionMap);

    Assertions.assertDoesNotThrow(() -> dbWriter.write(2, objScoreDistribution));
  }

}
*/
