package de.charite.compbio.ontolib.ontology.similarity;

import static org.junit.Assert.assertEquals;

import de.charite.compbio.ontolib.ontology.algo.InformationContentComputation;
import de.charite.compbio.ontolib.ontology.data.TermAnnotations;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.testdata.vegetables.VegetableOntologyTestBase;
import de.charite.compbio.ontolib.ontology.testdata.vegetables.VegetableTerm;
import de.charite.compbio.ontolib.ontology.testdata.vegetables.VegetableTermRelation;
import java.util.Collection;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class PrecomputingPairwiseResnikSimilarityTest extends VegetableOntologyTestBase {

  PrecomputingPairwiseResnikSimilarity<VegetableTerm, VegetableTermRelation> similarity;

  @Before
  public void setUp() {
    super.setUp();

    InformationContentComputation<VegetableTerm, VegetableTermRelation> computation =
        new InformationContentComputation<>(ontology);
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);

    similarity = new PrecomputingPairwiseResnikSimilarity<>(ontology, informationContent);
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.0, similarity.computeScore(idBeet, idCarrot), 0.01);
    assertEquals(0.405, similarity.computeScore(idBlueCarrot, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idLeafVegetable, idCarrot), 0.01);
  }

}
