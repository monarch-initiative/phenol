package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

import org.junit.jupiter.api.Test;

import java.util.List;

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
        similarity.computeScore(List.of(idBeet), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.816,
        similarity.computeScore(List.of(idBlueCarrot), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.50,
        similarity.computeScore(List.of(idPumpkin), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(List.of(idLeafVegetable), List.of(idCarrot)),
        0.01);
  }
}
