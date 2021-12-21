package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.*;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResnikSimilarityTest extends VegetableOntologyTestBase {

  private ResnikSimilarity similarity;

  @BeforeEach
  public void setUp() {
    InformationContentComputation computation = new InformationContentComputation(ontology);
    Map<TermId, Collection<TermId>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);
    PairwiseResnikSimilarity pairwise = new PairwiseResnikSimilarity(ontology, informationContent);

    similarity = new ResnikSimilarity(pairwise, true);
  }

  @Test
  public void testQueries() {
    assertEquals("Resnik similarity", similarity.getName());
    assertTrue(similarity.isSymmetric());
    assertEquals("{symmetric: true}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(
        0.0,
        similarity.computeScore(List.of(idBeet), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.405,
        similarity.computeScore(List.of(idBlueCarrot), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.00,
        similarity.computeScore(List.of(idPumpkin), List.of(idCarrot)),
        0.01);
    assertEquals(
        0.0,
        similarity.computeScore(List.of(idLeafVegetable), List.of(idCarrot)),
        0.01);
  }
}
