package org.monarchinitiative.phenol.ontology.similarity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.testdata.hpo.HpoOntologyTestBase;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HpoResnikSimilarityTest extends HpoOntologyTestBase {

  private static final double EPSILON = 5e-7;
  private static final Map<TermPair, Double> TERM_PAIR2IC = prepareTermPairIcMap();

  private HpoResnikSimilarity similarity;

  @BeforeEach
  public void init() {
    similarity = new HpoResnikSimilarity(TERM_PAIR2IC);
  }

  @Test
  public void testMicaAtRoot() {
    // These two terms have their MICA at the root, the similarity is zero
    double sim = similarity.computeScore(GALLOP_RHYTHM, HYPERTELORISM);
    assertEquals(0.0, sim, EPSILON);
  }

  @Test
  public void testABN_GLOBE_LOCATION() {
    // HYPERTELORISM - disease1
    //PROPTOSIS - disease1, disease7
    // MICA -- ABN_GLOBE_LOCATION -- disease1, disease7, frequency 2/8
    double sim = similarity.computeScore(HYPERTELORISM, PROPTOSIS);
    double expected = -1 * Math.log(0.25);
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testHYPERTELORISM() {
    // HYPERTELORISM - disease1
    double sim = similarity.computeScore(HYPERTELORISM, HYPERTELORISM);
    double expected = -1 * Math.log(0.125);  // 1 in 8
    assertEquals(expected, sim, EPSILON);
  }
  @Test
  public void testIRIS_COLOBOMA() {
    // IRIS_COLOBOMA - 4 of 8 diseases
    double sim = similarity.computeScore(IRIS_COLOBOMA, IRIS_COLOBOMA);
    double expected = -1 * Math.log(0.5);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  //
  @Test
  public void testHEART_MURMUR() {
    // HEART_MURMUR - 3 of 8 diseases
    double sim = similarity.computeScore(HEART_MURMUR, HEART_MURMUR);
    double expected = -1 * Math.log(0.375);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testHEART_MURMUR_vs_ROOT() {
    //  No term has similarity with the root
    double sim = similarity.computeScore(HEART_MURMUR, PHENOTYPIC_ABNORMALITY);
    double expected = 0.0;  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }
  @Test
  public void testColobomaSim() {
    //IRIS_COLOBOMA disease 1,2,3,4
    //RETINAL_COLOBOMA disease 4, 6
    // MICA -- COLOBOMA, disease 1,2,3,4,6
    double sim = similarity.computeScore(IRIS_COLOBOMA, RETINAL_COLOBOMA);
    double expected = -1 * Math.log(0.625);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testSymmetric() {
    assertEquals(similarity.computeScore(IRIS_COLOBOMA, RETINAL_COLOBOMA), similarity.computeScore(RETINAL_COLOBOMA,IRIS_COLOBOMA));
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
    double ic1 = similarity.computeScore(RETINAL_COLOBOMA, IRIS_COLOBOMA);
    double ic2 = similarity.computeScore(COLOBOMA, COLOBOMA);
    assertEquals(ic1, ic2, EPSILON);
    // MICA of GALLOP rhtyhm and heart murmur should be ABN_HEART_SOUND
    double ic3 = similarity.computeScore(GALLOP_RHYTHM, HEART_MURMUR);
    double ic4 = similarity.computeScore(ABN_HEART_SOUND, ABN_HEART_SOUND);
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



  private static Map<TermPair, Double> prepareTermPairIcMap() {
    Map<TermPair, Double> map = new HashMap<>();
    // Manually crafted based on the mock ontology and `hpoAnnotations`.
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0001279")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012668"), TermId.of("HP:0001279")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0012670"), TermId.of("HP:0012668")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0030148"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000520"), TermId.of("HP:0000316")), 1.386294);
    map.put(TermPair.symmetric(TermId.of("HP:0000589"), TermId.of("HP:0000589")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0012372")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000520"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0011025"), TermId.of("HP:0001279")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0030148"), TermId.of("HP:0030148")), 0.980829);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0000612")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0030148"), TermId.of("HP:0001279")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000520"), TermId.of("HP:0000480")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0000589")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0030148")), 0.693147);
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012668"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0012372")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0031657")), 0.693147);
    map.put(TermPair.symmetric(TermId.of("HP:0001626"), TermId.of("HP:0001279")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000478"), TermId.of("HP:0000316")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0011025"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012670"), TermId.of("HP:0011025")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000612"), TermId.of("HP:0000316")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000478"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0012670"), TermId.of("HP:0012670")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0000612"), TermId.of("HP:0000520")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0000612")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0000589")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000612"), TermId.of("HP:0000480")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012670"), TermId.of("HP:0001279")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0011025"), TermId.of("HP:0011025")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0001626"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0012668")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0000520")), 1.386294);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0100886")), 1.386294);
    map.put(TermPair.symmetric(TermId.of("HP:0000480"), TermId.of("HP:0000480")), 1.386294);
    map.put(TermPair.symmetric(TermId.of("HP:0000316"), TermId.of("HP:0000316")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0000480")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000612"), TermId.of("HP:0000589")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012668"), TermId.of("HP:0012668")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0012668"), TermId.of("HP:0011025")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0000316")), 1.386294);
    map.put(TermPair.symmetric(TermId.of("HP:0000612"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0011025")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012670"), TermId.of("HP:0001626")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0100886"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0031657")), 0.693147);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0000520")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0030148")), 0.693147);
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0012670")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0000480")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0012670")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000480"), TermId.of("HP:0000316")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0033113")), 2.079442);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0011025")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0030148"), TermId.of("HP:0011025")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000480"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000520"), TermId.of("HP:0000520")), 1.386294);
    map.put(TermPair.symmetric(TermId.of("HP:0000589"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000589"), TermId.of("HP:0000316")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0030148"), TermId.of("HP:0012670")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000612"), TermId.of("HP:0000612")), 0.693147);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0000316")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0000589"), TermId.of("HP:0000520")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0012372"), TermId.of("HP:0000478")), 0.287682);
    map.put(TermPair.symmetric(TermId.of("HP:0033113"), TermId.of("HP:0001279")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0031657"), TermId.of("HP:0012668")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0030148"), TermId.of("HP:0012668")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0000589"), TermId.of("HP:0000480")), 0.470004);
    map.put(TermPair.symmetric(TermId.of("HP:0001279"), TermId.of("HP:0001279")), 2.079442);

    return map;
  }

}
