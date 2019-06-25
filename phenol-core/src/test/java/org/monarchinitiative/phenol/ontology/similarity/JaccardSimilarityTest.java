package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.*;

import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

class JaccardSimilarityTest extends VegetableOntologyTestBase {

  private final JaccardSimilarity similarity = new JaccardSimilarity(ontology);

  @Test
  void testQueries() {
    assertEquals("Jaccard similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{normalized: true}", similarity.getParameters());
  }

  @Test
  void testComputeSimilarities() {
    assertEquals(
        0.25,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.66,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.33,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }
}
