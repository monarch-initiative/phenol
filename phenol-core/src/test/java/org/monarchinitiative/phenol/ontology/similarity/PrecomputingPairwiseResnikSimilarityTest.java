package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;


public class PrecomputingPairwiseResnikSimilarityTest extends VegetableOntologyTestBase {

  private PrecomputingPairwiseResnikSimilarity similarity;

  @BeforeEach
  public void setUp() {
    InformationContentComputation computation = new InformationContentComputation(ontology);
    Map<TermId, Collection<TermId>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);

    similarity = new PrecomputingPairwiseResnikSimilarity(ontology, informationContent);
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.405, similarity.computeScore(idBeet, idBeet), 0.01);
    assertEquals(0.0, similarity.computeScore(idBeet, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idBeet, idBlueCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idBeet, idPumpkin), 0.01);
    assertEquals(0.405, similarity.computeScore(idBeet, idLeafVegetable), 0.01);

    assertEquals(0.0, similarity.computeScore(idCarrot, idBeet), 0.01);
    assertEquals(0.405, similarity.computeScore(idCarrot, idCarrot), 0.01);
    assertEquals(0.405, similarity.computeScore(idCarrot, idBlueCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idCarrot, idPumpkin), 0.01);
    assertEquals(0.0, similarity.computeScore(idCarrot, idLeafVegetable), 0.01);

    assertEquals(1.098, similarity.computeScore(idBlueCarrot, idBlueCarrot), 0.01);
    assertEquals(0.405, similarity.computeScore(idBlueCarrot, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idBlueCarrot, idBeet), 0.01);
    assertEquals(0.0, similarity.computeScore(idBlueCarrot, idPumpkin), 0.01);
    assertEquals(0.0, similarity.computeScore(idBlueCarrot, idLeafVegetable), 0.01);

    assertEquals(0.405, similarity.computeScore(idPumpkin, idPumpkin), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idBlueCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idBeet), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idLeafVegetable), 0.01);

    assertEquals(0.405, similarity.computeScore(idLeafVegetable, idLeafVegetable), 0.01);
    assertEquals(0.0, similarity.computeScore(idLeafVegetable, idPumpkin), 0.01);
    assertEquals(0.0, similarity.computeScore(idLeafVegetable, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idLeafVegetable, idBlueCarrot), 0.01);
    assertEquals(0.405, similarity.computeScore(idLeafVegetable, idBeet), 0.01);
  }
}
