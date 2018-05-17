package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.Assert.*;

import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

public class TermOverlapSimilarityTest extends VegetableOntologyTestBase {

  private TermOverlapSimilarity similarity;

  @Before
  public void setUp() {
    super.setUp();
    similarity = new TermOverlapSimilarity(ontology);
  }

  @Test
  public void testQueries() {
    assertEquals("TermI overlap similarity", similarity.getName());
    assertEquals(true, similarity.isSymmetric());
    assertEquals("{normalized: true}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
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
