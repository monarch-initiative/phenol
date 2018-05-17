package org.monarchinitiative.phenol.ontology.similarity;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;


public class PairwiseResnikSimilarityTest extends VegetableOntologyTestBase {

  private PairwiseResnikSimilarity similarity;

  @Before
  public void setUp() {
    super.setUp();

    InformationContentComputation computation =
        new InformationContentComputation(ontology);
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);

    similarity = new PairwiseResnikSimilarity(ontology, informationContent);
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.0, similarity.computeScore(idBeet, idCarrot), 0.01);
    assertEquals(0.405, similarity.computeScore(idBlueCarrot, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idLeafVegetable, idCarrot), 0.01);
  }
}
