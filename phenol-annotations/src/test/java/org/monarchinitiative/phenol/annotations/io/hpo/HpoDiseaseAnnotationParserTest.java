package org.monarchinitiative.phenol.annotations.io.hpo;


import org.junit.jupiter.api.Disabled;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * These tests were constructed with a mini hpo file and a mini annotation file -- look at them to
 * figure out the logic!
 */
@Deprecated
@Disabled("Disabled due to deprecation")
class HpoDiseaseAnnotationParserTest {
//
//  private static Map<TermId, HpoDisease> diseaseMap;
//
//  @BeforeAll
//  public static void init() throws Exception {
//    final String hpOboPath = "/hp_head.obo";
//    URL hpOboURL = HpoDiseaseAnnotationParserTest.class.getResource(hpOboPath);
//    if (hpOboURL == null) {
//      throw new IOException("Could not find hpOboPath at " + hpOboPath);
//    }
//    File file = new File(hpOboURL.getFile());
//    Ontology hpoOntology = OntologyLoader.loadOntology(file);
//    URL annotURL = HpoDiseaseAnnotationParserTest.class.getResource("/annotations/phenotype_hpoa_head.tab");
//    diseaseMap = HpoDiseaseAnnotationParser.loadDiseaseMap(Paths.get(annotURL.toURI()), hpoOntology);
//  }
//
//  /**
//   * There are three different diseases in "annotations/phenotype_hpoa_head.tab"
//   */
//  @Test
//  void testSizeOfDiseaseMap() {
//    assertEquals(diseaseMap.size(), 3);
//  }
//
//
//  @Test
//  void testDiseaseOneInheritance() {
//    // Test that the parser correctly parses disease Id and number of annotations (4) of disease 1
//    HpoDisease testDisease = diseaseMap.get(TermId.of("OMIM:123456"));
//    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.of("OMIM:123456"));
//    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
//    assertEquals(1,testDisease.getModesOfInheritance().size());
//    TermId autosomalRecessive = TermId.of("HP:0000007");
//    assertEquals(autosomalRecessive,testDisease.getModesOfInheritance().get(0));
//  }
//
//  @Test
//  void testDiseaseOneAnnotationCount() {
//    // Test that the parser correctly parses disease Id and number of annotations (4) of disease 1
//    HpoDisease testDisease = diseaseMap.get(TermId.of("OMIM:123456"));
//    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.of("OMIM:123456"));
//    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
//    assertEquals(3,testDisease.getPhenotypicAbnormalities().size());
//    //Lacrimation abnormality HP:0000632
//    TermId lacrimation = TermId.of("HP:0000632");
//    //Abnormal pupillary function HP:0007686
//    TermId pupillary = TermId.of("HP:0007686");
//    // Microphthalmia HP:0000568
//    TermId microphthalmia = TermId.of("HP:0000568");
//    List<TermId> abnormalities = testDisease.getPhenotypicAbnormalities().
//      stream().
//      map(HpoAnnotation::id).
//      collect(Collectors.toList());
//    assertTrue(abnormalities.contains(lacrimation));
//    assertTrue(abnormalities.contains(pupillary));
//    assertTrue(abnormalities.contains(microphthalmia));
//    assertEquals(3,abnormalities.size());
//  }
//
//  @Test
//  void testDiseaseTwoInheritance()  {
//    // Test that the parser correctly parses disease Id and number of annotations (3) of disease 1
//    HpoDisease testDisease = diseaseMap.get(TermId.of("OMIM:654321"));
//    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.of("OMIM:654321"));
//    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
//    assertEquals(1,testDisease.getModesOfInheritance().size());
//    TermId autosomalDominant = TermId.of("HP:0000006");
//    assertEquals(autosomalDominant,testDisease.getModesOfInheritance().get(0));
//  }
//
//  @Test
//  void testDiseaseTwoAnnotations() {
//    // Test that the parser correctly parses disease Id and number of annotations (2) of disease 1
//    HpoDisease testDisease = diseaseMap.get(TermId.of("OMIM:654321"));
//    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.of("OMIM:654321"));
//    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
//    assertEquals(2,testDisease.getPhenotypicAbnormalities().size());
//    //Lacrimation abnormality HP:0000632
//    TermId lacrimation = TermId.of("HP:0000632");
//    //Abnormal pupillary function HP:0007686
//    TermId pupillary = TermId.of("HP:0007686");
//    // Microphthalmia HP:0000568
//    TermId microphthalmia = TermId.of("HP:0000568"); // a NOT for disease 2
//    List<TermId> abnormalities = testDisease.getPhenotypicAbnormalities().
//      stream().
//      map(HpoAnnotation::id).
//      collect(Collectors.toList());
//    assertTrue(abnormalities.contains(lacrimation));
//    assertTrue(abnormalities.contains(pupillary));
//   // assertTrue(abnormalities.contains(microphthalmia));
//    assertEquals(2,abnormalities.size());
//
//    List<TermId> notAbnormal = testDisease.getNegativeAnnotations();
//    assertEquals(1,notAbnormal.size());
//    assertTrue(notAbnormal.contains(microphthalmia));
//  }



}
