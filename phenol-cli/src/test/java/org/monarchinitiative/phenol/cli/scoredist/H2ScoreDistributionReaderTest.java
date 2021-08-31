package org.monarchinitiative.phenol.cli.scoredist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;



//@ExtendWith(TempDirectory.class)
// TODO - make these tests work or get rid of them
class H2ScoreDistributionReaderTest {

//  private ScoreDistributionReader objDbReader;
//
//  @BeforeEach
//  void setUp(@TempDirectory.TempDir Path tempDir) throws Exception {
//
//    Path dbpath = tempDir.resolve("db.h2");
//    System.out.println(dbpath.toFile().getAbsolutePath());
//    String dataTableName = "TempTable";
//    ScoreDistributionWriter dbWriter = new H2ScoreDistributionWriter(dbpath.toFile()
//      .getAbsolutePath(), dataTableName, true);
//    objDbReader = new H2ScoreDistributionReader(dbpath.toFile().getAbsolutePath(), dataTableName);
//
//    TermId termId = TermId.of("MONDO", "001");
//    ScoreDistribution objScoreDistribution = new ScoreDistribution(2, ImmutableMap.of(termId,
//      new ObjectScoreDistribution(termId, 2, 3,
//        ImmutableSortedMap.of(0.0, 0.2, 0.5, 0.6, 1.0, 1.0))));
//
//    dbWriter.write(3, objScoreDistribution);
//    dbWriter.close();
//  }
//
//  @AfterEach
//  void tearDown() throws IOException {
//    objDbReader.close();
//  }
//
//  @Test
//  void readForTermCountAndObject() throws Exception {
//    TermId termId = TermId.of("MONDO", "001");
//    ObjectScoreDistribution objScoreDist = objDbReader.readForTermCountAndObject(3, termId);
//    assertEquals(objScoreDist.getObjectId(), termId);
//  }
//
//  @Test
//  void readForTermCount() throws Exception {
//    ScoreDistribution objScoreDist = objDbReader.readForTermCount(3);
//    assertEquals(objScoreDist.getObjectIds().size(), 1);
//  }
//
//  @Test
//  void readAll() throws Exception{
//    Map<Integer, ScoreDistribution> results = objDbReader.readAll();
//    assertEquals(results.size(), 1);
//  }
//
//  @Test
//  void close() throws Exception{
//    objDbReader.close();
//  }

}
