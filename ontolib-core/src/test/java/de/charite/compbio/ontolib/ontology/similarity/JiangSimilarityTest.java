package de.charite.compbio.ontolib.ontology.similarity;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
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

public class JiangSimilarityTest extends VegetableOntologyTestBase {

  JiangSimilarity<VegetableTerm, VegetableTermRelation> similarity;

  @Before
  public void setUp() {
    super.setUp();

    InformationContentComputation<VegetableTerm, VegetableTermRelation> computation =
        new InformationContentComputation<>(ontology);
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);
    PairwiseResnikSimilarity<VegetableTerm, VegetableTermRelation> pairwise =
        new PairwiseResnikSimilarity<>(ontology, informationContent);

    similarity = new JiangSimilarity<>(pairwise, true);
  }

  @Test
  public void testQueries() {
    assertEquals("Jiang similarity", similarity.getName());
    assertEquals(true, similarity.isSymmetric());
    assertEquals("{symmetric: true}", similarity.getParameters());
  }

  @Test
  public void testComputeSimilarities() {
    assertEquals(0.0,
        similarity.computeScore(Lists.newArrayList(idBeet), Lists.newArrayList(idCarrot)), 0.01);
    assertEquals(0.405,
        similarity.computeScore(Lists.newArrayList(idBlueCarrot), Lists.newArrayList(idCarrot)),
        0.01);
    assertEquals(0.00,
        similarity.computeScore(Lists.newArrayList(idPumpkin), Lists.newArrayList(idCarrot)), 0.01);
    assertEquals(0.0,
        similarity.computeScore(Lists.newArrayList(idLeafVegetable), Lists.newArrayList(idCarrot)),
        0.01);
  }

}
