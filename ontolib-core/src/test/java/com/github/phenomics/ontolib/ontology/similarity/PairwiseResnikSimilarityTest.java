package com.github.phenomics.ontolib.ontology.similarity;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import com.github.phenomics.ontolib.ontology.algo.InformationContentComputation;
import com.github.phenomics.ontolib.ontology.data.TermAnnotations;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.similarity.PairwiseResnikSimilarity;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableOntologyTestBase;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableTerm;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableTermRelation;

public class PairwiseResnikSimilarityTest extends VegetableOntologyTestBase {

  PairwiseResnikSimilarity<VegetableTerm, VegetableTermRelation> similarity;

  @Before
  public void setUp() {
    super.setUp();

    InformationContentComputation<VegetableTerm, VegetableTermRelation> computation =
        new InformationContentComputation<>(ontology);
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);

    similarity = new PairwiseResnikSimilarity<>(ontology, informationContent);
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.0, similarity.computeScore(idBeet, idCarrot), 0.01);
    assertEquals(0.405, similarity.computeScore(idBlueCarrot, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idPumpkin, idCarrot), 0.01);
    assertEquals(0.0, similarity.computeScore(idLeafVegetable, idCarrot), 0.01);
  }

}
