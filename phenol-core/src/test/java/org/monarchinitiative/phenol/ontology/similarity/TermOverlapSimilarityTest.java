package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.*;

import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

import org.junit.jupiter.api.Test;

import java.util.List;

public class TermOverlapSimilarityTest extends VegetableOntologyTestBase {

  private final TermOverlapSimilarity similarity = new TermOverlapSimilarity(ontology);

  @Test
  public void testQueries() {
    assertEquals("TermI overlap similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{normalized: true}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(
        0.5,
        similarity.computeScore(List.of(idBeet), List.of(idCarrot)),
        0.01);
    assertEquals(
        1.0,
        similarity.computeScore(List.of(idBlueCarrot), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.5,
        similarity.computeScore(List.of(idPumpkin), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(List.of(idLeafVegetable), List.of(idCarrot)),
        0.01);
  }
}
