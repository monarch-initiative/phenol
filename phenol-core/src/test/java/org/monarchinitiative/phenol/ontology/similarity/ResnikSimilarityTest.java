package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.*;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResnikSimilarityTest extends VegetableOntologyTestBase {

  private ResnikSimilarity similarity;

  @BeforeEach
  void setUp() {
    InformationContentComputation computation = new InformationContentComputation(ontology);
    Map<TermId, Collection<TermId>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);
    PairwiseResnikSimilarity pairwise = new PairwiseResnikSimilarity(ontology, informationContent);

    similarity = new ResnikSimilarity(pairwise, true);
  }

  @Test
  void testQueries() {
    assertEquals("Resnik similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{symmetric: true}", similarity.getParameters());
  }

  @Test
  void testComputeSimilarities() {
    assertEquals(
        0.0,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.405,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.00,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }
}
