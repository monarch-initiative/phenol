package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.similarity.PairwiseResnikSimilarity;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableRecipeAnnotation;


public class SimilarityScoreSamplingTest extends VegetableOntologyTestBase {

  private SimilarityScoreSampling scoreSampling;
  private ResnikSimilarity resnikSimilarity;

  @Before
  public void setUp() {
    super.setUp();

    InformationContentComputation computation =
        new InformationContentComputation(ontology);
    Map<TermId, Collection<String>> termLabels =
        TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);
    PairwiseResnikSimilarity pairwise =
        new PairwiseResnikSimilarity(ontology, informationContent);
    resnikSimilarity = new ResnikSimilarity(pairwise, true);

    ScoreSamplingOptions options = new ScoreSamplingOptions(1, null, null, 2, 2, 10_000, 42);
    scoreSampling =
        new SimilarityScoreSampling(
            ontology, resnikSimilarity, options);
  }

  @Test
  public void test() {
    // TODO: this logic should be moved into the library
    Map<String, Integer> recipeToId = new HashMap<>();
    Map<Integer, Set<TermId>> labels = new HashMap<>();
    int nextId = 1;
    for (VegetableRecipeAnnotation a : recipeAnnotations) {
      if (!recipeToId.containsKey(a.getLabel())) {
        recipeToId.put(a.getLabel(), nextId++);
      }
      final int recipeId = recipeToId.get(a.getLabel());
      if (!labels.containsKey(recipeId)) {
        labels.put(recipeId, new HashSet<>());
      }
      final Set<TermId> termIds = labels.get(recipeId);
      termIds.add(a.getTermId());
    }

    Map<Integer, ScoreDistribution> samplingResult = scoreSampling.performSampling(labels);
    assertEquals(1, samplingResult.size());
    ScoreDistribution dist = samplingResult.get(2);
    assertEquals(0.50, dist.getObjectScoreDistribution(1).estimatePValue(0.2), 0.3);
    assertEquals(0.0, dist.getObjectScoreDistribution(1).estimatePValue(0.4), 0.01);
    assertEquals(0.0, dist.getObjectScoreDistribution(1).estimatePValue(0.6), 0.01);
    assertEquals(0.0, dist.getObjectScoreDistribution(1).estimatePValue(0.8), 0.01);
  }
}
