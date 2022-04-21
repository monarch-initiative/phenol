package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.monarchinitiative.phenol.ontology.data.TermId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SimpleFeatureVectorSimilarityTest {

  SimpleFeatureVectorSimilarity similarity;

  @BeforeEach
  public void setUp() {
    similarity = new SimpleFeatureVectorSimilarity();
  }

  @Test
  public void testQueries() {
    assertEquals("Simple feature vector similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(
        1.0,
        similarity.computeScore(
          List.of(TermId.of("HP:0000008"), TermId.of("HP:0000009")),
          List.of(TermId.of("HP:0000008"))),
        0.01);
    assertEquals(
        1.0,
        similarity.computeScore(
            List.of(TermId.of("HP:0000008"), TermId.of("HP:0000009")),
            List.of(TermId.of("HP:0000008"), TermId.of("HP:0000010"))),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(
            List.of(TermId.of("HP:0000009")),
            List.of(TermId.of("HP:0000008"), TermId.of("HP:0000010"))),
        0.01);
  }
}
