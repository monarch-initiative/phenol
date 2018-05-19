package org.monarchinitiative.phenol.ontology.algo;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;

public class InformationContentComputationTest extends VegetableOntologyTestBase {

  private InformationContentComputation computation;

  @Before
  public void setUp() {
    super.setUp();
    computation = new InformationContentComputation(ontology);
  }

  @Test
  public void test() {
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
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
}
