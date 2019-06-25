package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.*;

import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

class TermOverlapSimilarityTest extends VegetableOntologyTestBase {

  private final TermOverlapSimilarity similarity = new TermOverlapSimilarity(ontology);

  @Test
  void testQueries() {
    assertEquals("TermI overlap similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{normalized: true}", similarity.getParameters());
  }

  @Test
  void testComputeSimilarities() {
    assertEquals(
        0.5,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        1.0,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.5,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }
}
