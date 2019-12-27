package org.monarchinitiative.phenol.io.scoredist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TempDirectory.class)
class H2ScoreDistributionReaderTest {
  private ScoreDistribution<Integer> integerScoreDistribution;
  private ScoreDistribution<TermId> objScoreDistribution;
  private static ScoreDistributionWriter dbWriter;
  private static ScoreDistributionReader dbReader;

  @BeforeEach
  void setUp(@TempDirectory.TempDir Path tempDir) throws Exception {

    Path dbpath = tempDir.resolve("db.h2");
    System.out.println(dbpath.toFile().getAbsolutePath());
    String dataTableName = "TempTable";
    dbWriter = new H2ScoreDistributionWriter(dbpath.toFile().getAbsolutePath(), dataTableName, true);
    dbReader = new H2ScoreDistributionReader(dbpath.toFile().getAbsolutePath(), dataTableName);

    integerScoreDistribution = new ScoreDistribution<>(2, ImmutableMap.of(1, new ObjectScoreDistribution<>((Integer) 1, 2, 3, ImmutableSortedMap.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0))));

    dbWriter.write(2, integerScoreDistribution);

//    TermId termId = TermId.of("MONDO", "001");
//    objScoreDistribution = new ScoreDistribution<>(2, ImmutableMap.of(termId, new ObjectScoreDistribution<>(termId, 2, 3, ImmutableSortedMap.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0))));
//
//    dbWriter.write(2, objScoreDistribution);

    dbWriter.close();
  }

  @Test
  void readForTermCountAndObject() throws Exception {
    ObjectScoreDistribution<Integer> intScoreDist = (ObjectScoreDistribution<Integer>) dbReader.readForTermCountAndObject(2, 1);
  }

  @Test
  void readForTermCount() throws Exception {
    ScoreDistribution intScoreDist = dbReader.readForTermCount(2);
  }

  @Test
  void readAll() throws Exception{
    Map<Integer, ScoreDistribution<Integer>> results = dbReader.readAll();
  }

  @Test
  void close() {
  }

}
