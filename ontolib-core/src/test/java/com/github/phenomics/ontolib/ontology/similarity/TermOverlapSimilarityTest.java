package com.github.phenomics.ontolib.ontology.similarity;

import static org.junit.Assert.*;

import com.github.phenomics.ontolib.ontology.similarity.TermOverlapSimilarity;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableOntologyTestBase;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableTerm;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableTermRelation;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

public class TermOverlapSimilarityTest extends VegetableOntologyTestBase {

  TermOverlapSimilarity<VegetableTerm, VegetableTermRelation> similarity;

  @Before
  public void setUp() {
    super.setUp();
    similarity = new TermOverlapSimilarity<>(ontology);
  }

  @Test
  public void testQueries() {
    assertEquals("Term overlap similarity", similarity.getName());
    assertEquals(true, similarity.isSymmetric());
    assertEquals("{normalized: true}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.5,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)), 0.01);
    assertEquals(1.0,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(0.5,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)), 0.01);
    assertEquals(0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }

}
