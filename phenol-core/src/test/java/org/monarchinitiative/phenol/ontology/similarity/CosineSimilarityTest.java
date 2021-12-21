package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

public class CosineSimilarityTest extends VegetableOntologyTestBase {

  private final CosineSimilarity similarity = new CosineSimilarity(ontology);

  @Test
  public void testQueries() {
    assertEquals("Cosine similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{oppositeAware: false}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(
        0.408,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.816,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.50,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }
}
