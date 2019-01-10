package org.monarchinitiative.phenol.ontology.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.monarchinitiative.phenol.ontology.algo.InformationContentComputation.mostInformativeCommonAncestor;

import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

public class InformationContentComputationTest extends VegetableOntologyTestBase {

  private InformationContentComputation computation;

  @BeforeEach
  public void setUp() {
    super.setUp();
    computation = new InformationContentComputation(ontology);
  }

  @Test
  void test() {
    Map<TermId, Collection<TermId>> termLabels = TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);

    assertEquals(7, informationContent.size());

    assertEquals(0.0, informationContent.get(idVegetable), 0.001);
    assertEquals(0.0, informationContent.get(idRootVegetable), 0.001);
    assertEquals(0.405, informationContent.get(idLeafVegetable), 0.001);
    assertEquals(0.405, informationContent.get(idCarrot), 0.001);
    assertEquals(0.405, informationContent.get(idBeet), 0.001);
    assertEquals(0.405, informationContent.get(idPumpkin), 0.001);
    assertEquals(1.099, informationContent.get(idBlueCarrot), 0.01);
  }


  @Test
  void testMICA1() {
    Map<TermId, Collection<TermId>> termLabels = TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);
    TermId expectedMICA = idRootVegetable;
    TermId calculatedMICA = mostInformativeCommonAncestor(idCarrot,idBeet,ontology,informationContent);
    assertEquals(expectedMICA,calculatedMICA);
  }



}
