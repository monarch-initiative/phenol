package org.monarchinitiative.phenol.ontology.similarity;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.testdata.hpo.HpoOntologyTestBase;
import org.monarchinitiative.phenol.ontology.testdata.hpo.ToyHpoAnnotation;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HpoResnikSimilarityTest extends HpoOntologyTestBase {

  private static HpoResnikSimilarity similarity;

  private static final double EPSILON = 0.0000000001;


  @BeforeAll
  public static void  init() {
    final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();
    for(ToyHpoAnnotation annot : hpoAnnotations) {
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, Sets.newHashSet(annot.getTermId()), true);
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
    // These two terms have their MICA at the root, the similaroty is zero
    double sim = similarity.getResnikSymmetric(GALLOP_RHYTHM, HYPERTELORISM);
    assertEquals(0.0, sim, EPSILON);
  }

  @Test
  public void testABN_GLOBE_LOCATION() {
    // HYPERTELORISM - disease1
    //PROPTOSIS - disease1, disease7
    // MICA -- ABN_GLOBE_LOCATION -- disease1, disease7, frequency 2/8
    double sim = similarity.getResnikSymmetric(HYPERTELORISM, PROPTOSIS);
    double expected = -1 * Math.log(0.25);
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testHYPERTELORISM() {
    // HYPERTELORISM - disease1
    double sim = similarity.getResnikSymmetric(HYPERTELORISM, HYPERTELORISM);
    double expected = -1 * Math.log(0.125);  // 1 in 8
    assertEquals(expected, sim, EPSILON);
  }
  @Test
  public void testIRIS_COLOBOMA() {
    // IRIS_COLOBOMA - 4 of 8 diseases
    double sim = similarity.getResnikSymmetric(IRIS_COLOBOMA, IRIS_COLOBOMA);
    double expected = -1 * Math.log(0.5);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  //
  @Test
  public void testHEART_MURMUR() {
    // HEART_MURMUR - 3 of 8 diseases
    double sim = similarity.getResnikSymmetric(HEART_MURMUR, HEART_MURMUR);
    double expected = -1 * Math.log(0.375);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testHEART_MURMUR_vs_ROOT() {
    //  No term has similarity with the root
    double sim = similarity.getResnikSymmetric(HEART_MURMUR, PHENOTYPIC_ABNORMALITY);
    double expected = 0.0;  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }
  @Test
  public void testColobomaSim() {
    //IRIS_COLOBOMA disease 1,2,3,4
    //RETINAL_COLOBOMA disease 4, 6
    // MICA -- COLOBOMA, disease 1,2,3,4,6
    double sim = similarity.getResnikSymmetric(IRIS_COLOBOMA, RETINAL_COLOBOMA);
    double expected = -1 * Math.log(0.625);  // 1 in 2
    assertEquals(expected, sim, EPSILON);
  }

  @Test
  public void testSymmetric() {
    assertEquals(similarity.getResnikSymmetric(IRIS_COLOBOMA, RETINAL_COLOBOMA), similarity.getResnikSymmetric(RETINAL_COLOBOMA,IRIS_COLOBOMA));
  }




}
