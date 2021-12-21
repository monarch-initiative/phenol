package org.monarchinitiative.phenol.ontology.similarity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.testdata.hpo.HpoOntologyTestBase;
import org.monarchinitiative.phenol.ontology.testdata.hpo.ToyHpoAnnotation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HpoResnikSimilarityTest extends HpoOntologyTestBase {

  private static HpoResnikSimilarity similarity;

  private static final double EPSILON = 0.0000000001;


  @BeforeAll
  public static void  init() {
    final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();
    for (ToyHpoAnnotation annot : hpoAnnotations) {

      Set<TermId> termIds = new HashSet<>();
      termIds.add(annot.getTermId());
      Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, termIds, true);
      for (TermId tid : inclAncestorTermIds) {
        termIdToDiseaseIds.putIfAbsent(tid, new HashSet<>());
        termIdToDiseaseIds.get(tid).add(annot.getLabel());
      }
    }
    final Map<TermId, Double> icMap = new InformationContentComputation(ontology).computeInformationContent(termIdToDiseaseIds);
    similarity = new HpoResnikSimilarity(ontology, icMap);
  }

  @Test
  public void testMicaAtRoot() {
    // These two terms have their MICA at the root, the similarity is zero
    double sim = similarity.getResnikTermSimilarity(GALLOP_RHYTHM, HYPERTELORISM);
    assertEquals(0.0, sim, EPSILON);
  }

  @Test
  public void testABN_GLOBE_LOCATION() {
    // HYPERTELORISM - disease1
    //PROPTOSIS - disease1, disease7
    // MICA -- ABN_GLOBE_LOCATION -- disease1, disease7, frequency 2/8
    double sim = similarity.getResnikTermSimilarity(HYPERTELORISM, PROPTOSIS);
    double expected = -1 * Math.log(0.25);
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testHYPERTELORISM() {
    // HYPERTELORISM - disease1
    double sim = similarity.getResnikTermSimilarity(HYPERTELORISM, HYPERTELORISM);
    double expected = -1 * Math.log(0.125);  // 1 in 8
    assertEquals(expected, sim, EPSILON);
  }
  @Test
  public void testIRIS_COLOBOMA() {
    // IRIS_COLOBOMA - 4 of 8 diseases
    double sim = similarity.getResnikTermSimilarity(IRIS_COLOBOMA, IRIS_COLOBOMA);
    double expected = -1 * Math.log(0.5);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  //
  @Test
  public void testHEART_MURMUR() {
    // HEART_MURMUR - 3 of 8 diseases
    double sim = similarity.getResnikTermSimilarity(HEART_MURMUR, HEART_MURMUR);
    double expected = -1 * Math.log(0.375);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testHEART_MURMUR_vs_ROOT() {
    //  No term has similarity with the root
    double sim = similarity.getResnikTermSimilarity(HEART_MURMUR, PHENOTYPIC_ABNORMALITY);
    double expected = 0.0;  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }
  @Test
  public void testColobomaSim() {
    //IRIS_COLOBOMA disease 1,2,3,4
    //RETINAL_COLOBOMA disease 4, 6
    // MICA -- COLOBOMA, disease 1,2,3,4,6
    double sim = similarity.getResnikTermSimilarity(IRIS_COLOBOMA, RETINAL_COLOBOMA);
    double expected = -1 * Math.log(0.625);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testSymmetric() {
    assertEquals(similarity.getResnikTermSimilarity(IRIS_COLOBOMA, RETINAL_COLOBOMA), similarity.getResnikTermSimilarity(RETINAL_COLOBOMA,IRIS_COLOBOMA));
  }


  /**
   * Test simialrity with disease 2, which has
   *   disease2annotations = Lists.newArrayList(IRIS_COLOBOMA, HEART_MURMUR);
   */
  @Test
  public void testSymmetricQuery() {
    List<TermId> queryTerms = new ArrayList<>();
    queryTerms.add(RETINAL_COLOBOMA);
    queryTerms.add(GALLOP_RHYTHM);

    // The similarity should be the average of the ICs of the MICAs.
    // MICA of RETINAL and Iris coloboma should be coloboma
    double ic1 = similarity.getResnikTermSimilarity(RETINAL_COLOBOMA, IRIS_COLOBOMA);
    double ic2 = similarity.getResnikTermSimilarity(COLOBOMA, COLOBOMA);
    assertEquals(ic1, ic2, EPSILON);
    // MICA of GALLOP rhtyhm and heart murmur should be ABN_HEART_SOUND
    double ic3 = similarity.getResnikTermSimilarity(GALLOP_RHYTHM, HEART_MURMUR);
    double ic4 = similarity.getResnikTermSimilarity(ABN_HEART_SOUND, ABN_HEART_SOUND);
    assertEquals(ic3, ic4, EPSILON);
    // The query to disease similarity should be the average of ic2 and ic4
    double sim = 0.5*(ic2+ic4);
    double querySim = similarity.computeScoreSymmetric(queryTerms, disease2annotations);
    assertEquals(sim, querySim);
  }

  /**
   * Result of asymetric query for disease 1 (with 4 terms) is different in both directions.
   */
  @Test
  public void testAsymmetricQuery() {
    List<TermId> queryTerms = new ArrayList<>();
    queryTerms.add(RETINAL_COLOBOMA);
    queryTerms.add(GALLOP_RHYTHM);
    double d1 = similarity.computeScoreAsymmetric(queryTerms, disease1annotations);
    double d2 = similarity.computeScoreAsymmetric(disease1annotations,queryTerms);
    assertNotEquals(d1,d2);
  }




}
