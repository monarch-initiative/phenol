package de.charite.compbio.ontolib.ontology.similarity;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.charite.compbio.ontolib.ontology.testdata.vegetables.VegetableOntologyTestBase;
import de.charite.compbio.ontolib.ontology.testdata.vegetables.VegetableTerm;
import de.charite.compbio.ontolib.ontology.testdata.vegetables.VegetableTermRelation;
import org.junit.Before;
import org.junit.Test;

public class CosineSimilarityTest extends VegetableOntologyTestBase {

  CosineSimilarity<VegetableTerm, VegetableTermRelation> similarity;

  @Before
  public void setUp() {
    super.setUp();
    similarity = new CosineSimilarity<>(ontology);
  }

  @Test
  public void testQueries() {
    assertEquals("Cosine similarity", similarity.getName());
    assertEquals(true, similarity.isSymmetric());
    assertEquals("{oppositeAware: false}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.408,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)), 0.01);
    assertEquals(0.816,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(0.50,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)), 0.01);
    assertEquals(0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }

}
