package org.monarchinitiative.phenol.ontology.scoredist;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.similarity.PairwiseResnikSimilarity;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableRecipeAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SimilarityScoreSamplingTest extends VegetableOntologyTestBase {

  private SimilarityScoreSampling scoreSampling;

  @BeforeEach
  public void setUp() {
    InformationContentComputation computation = new InformationContentComputation(ontology);
    Map<TermId, Collection<TermId>> termLabels = TermAnnotations.constructTermAnnotationToLabelsMap(ontology, recipeAnnotations);
    Map<TermId, Double> informationContent = computation.computeInformationContent(termLabels);
    PairwiseResnikSimilarity pairwise = new PairwiseResnikSimilarity(ontology, informationContent);
    ResnikSimilarity resnikSimilarity = new ResnikSimilarity(pairwise, true);

    ScoreSamplingOptions options = new ScoreSamplingOptions(1, null, null, 2, 2, 10_000, 42);
    scoreSampling = new SimilarityScoreSampling(ontology, resnikSimilarity, options,termLabels);
  }

 @Test
  public void test() {
    // TODO: this logic should be moved into the library
    Map<TermId, Integer> recipeToId = new HashMap<>();
    Map<TermId, Set<TermId>> labels = new HashMap<>();
    int nextId = 1;
    for (VegetableRecipeAnnotation a : recipeAnnotations) {
      if (!recipeToId.containsKey(a.getItemId())) {
        recipeToId.put(a.getItemId(), nextId++);
      }
      TermId recipeId = a.getTermId();
      if (!labels.containsKey(recipeId)) {
        labels.put(recipeId, new HashSet<>());
      }
      final Set<TermId> termIds = labels.get(recipeId);
      termIds.add(a.getTermId());
    }

    Map<Integer, ScoreDistribution> samplingResult = scoreSampling.performSampling();
    assertEquals(1, samplingResult.size());
    // todo add more testing of distribution
  }

}
