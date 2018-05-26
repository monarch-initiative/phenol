package org.monarchinitiative.phenol.ontology.similarity;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

public class SimpleFeatureVectorSimilarityTest {

  SimpleFeatureVectorSimilarity similarity;

  @Before
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
            Lists.newArrayList(
                TermId.constructWithPrefix("HP:0000008"),
                TermId.constructWithPrefix("HP:0000009")),
            Lists.newArrayList(TermId.constructWithPrefix("HP:0000008"))),
        0.01);
    assertEquals(
        1.0,
        similarity.computeScore(
            Lists.newArrayList(
                TermId.constructWithPrefix("HP:0000008"),
                TermId.constructWithPrefix("HP:0000009")),
            Lists.newArrayList(
                TermId.constructWithPrefix("HP:0000008"),
                TermId.constructWithPrefix("HP:0000010"))),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(
            Lists.newArrayList(TermId.constructWithPrefix("HP:0000009")),
            Lists.newArrayList(
                TermId.constructWithPrefix("HP:0000008"),
                TermId.constructWithPrefix("HP:0000010"))),
        0.01);
  }
}
