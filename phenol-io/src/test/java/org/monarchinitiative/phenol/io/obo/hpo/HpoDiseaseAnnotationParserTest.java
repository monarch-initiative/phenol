package org.monarchinitiative.phenol.io.obo.hpo;


import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These tests were constructed with a mini hpo file and a mini annotation file -- look at them to
 * figure out the logic!
 */
public class HpoDiseaseAnnotationParserTest {
  private static Map<TermId, HpoDisease> diseaseMap;

  @ClassRule
  public static TemporaryFolder tmpFolder = new TemporaryFolder();

  @BeforeClass
  public static void setUp() throws IOException, PhenolException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(
    File hpoHeadFile;


    hpoHeadFile = tmpFolder.newFile("hp_head.obo");
    ResourceUtils.copyResourceToFile("/hp_head.obo", hpoHeadFile);
    final HpOboParser oboParser = new HpOboParser(hpoHeadFile, true);
    final HpoOntology ontology = oboParser.parse();

    File hpoDiseaseAnnotationToyFile = tmpFolder.newFile("phenotype.100lines.hpoa.tmp");
    ResourceUtils.copyResourceToFile("/phenotype_annotation_head.tab", hpoDiseaseAnnotationToyFile);
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(hpoDiseaseAnnotationToyFile.getAbsolutePath(), ontology);
    diseaseMap = parser.parse();
  }


  @Test
  public void testSizeOfDiseaseMap() {
    assertEquals(diseaseMap.size(), 2);
  }


  @Test
  public void testDiseaseOneInheritance() {
    // Test that the parser correctly parses disease Id and number of annotations (4) of disease 1
    HpoDisease testDisease = diseaseMap.get(TermId.constructWithPrefix("OMIM:123456"));
    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.constructWithPrefix("OMIM:123456"));
    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
    assertEquals(1,testDisease.getModesOfInheritance().size());
    TermId autosomalRecessive = TermId.constructWithPrefix("HP:0000007");
    assertEquals(autosomalRecessive,testDisease.getModesOfInheritance().get(0));
  }

  @Test
  public void testDiseaseOneAnnotationCount() {
    // Test that the parser correctly parses disease Id and number of annotations (4) of disease 1
    HpoDisease testDisease = diseaseMap.get(TermId.constructWithPrefix("OMIM:123456"));
    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.constructWithPrefix("OMIM:123456"));
    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
    assertEquals(3,testDisease.getPhenotypicAbnormalities().size());
    //Lacrimation abnormality HP:0000632
    TermId lacrimation = TermId.constructWithPrefix("HP:0000632");
    //Abnormal pupillary function HP:0007686
    TermId pupillary = TermId.constructWithPrefix("HP:0007686");
    // Microphthalmia HP:0000568
    TermId microphthalmia = TermId.constructWithPrefix("HP:0000568");
    List<TermId> abnormalities = testDisease.getPhenotypicAbnormalities().
      stream().
      map(HpoAnnotation::getTermId).
      collect(Collectors.toList());
    assertTrue(abnormalities.contains(lacrimation));
    assertTrue(abnormalities.contains(pupillary));
    assertTrue(abnormalities.contains(microphthalmia));
    assertEquals(3,abnormalities.size());
  }

  @Test
  public void testDiseaseTwoInheritance()  {
    // Test that the parser correctly parses disease Id and number of annotations (3) of disease 1
    HpoDisease testDisease = diseaseMap.get(TermId.constructWithPrefix("OMIM:654321"));
    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.constructWithPrefix("OMIM:654321"));
    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
    assertEquals(1,testDisease.getModesOfInheritance().size());
    TermId autosomalDominant = TermId.constructWithPrefix("HP:0000006");
    assertEquals(autosomalDominant,testDisease.getModesOfInheritance().get(0));
  }

  @Test
  public void testDiseaseTwoAnnotations() {
    // Test that the parser correctly parses disease Id and number of annotations (2) of disease 1
    HpoDisease testDisease = diseaseMap.get(TermId.constructWithPrefix("OMIM:654321"));
    assertEquals(testDisease.getDiseaseDatabaseId(), TermId.constructWithPrefix("OMIM:654321"));
    // This disease is annotated with Autosomal recessive inheritance HP:0000007 con
    assertEquals(2,testDisease.getPhenotypicAbnormalities().size());
    //Lacrimation abnormality HP:0000632
    TermId lacrimation = TermId.constructWithPrefix("HP:0000632");
    //Abnormal pupillary function HP:0007686
    TermId pupillary = TermId.constructWithPrefix("HP:0007686");
    // Microphthalmia HP:0000568
    TermId microphthalmia = TermId.constructWithPrefix("HP:0000568"); // a NOT for disease 2
    List<TermId> abnormalities = testDisease.getPhenotypicAbnormalities().
      stream().
      map(HpoAnnotation::getTermId).
      collect(Collectors.toList());
    assertTrue(abnormalities.contains(lacrimation));
    assertTrue(abnormalities.contains(pupillary));
   // assertTrue(abnormalities.contains(microphthalmia));
    assertEquals(2,abnormalities.size());

    List<TermId> notAbnormal = testDisease.getNegativeAnnotations();
    assertEquals(1,notAbnormal.size());
    assertTrue(notAbnormal.contains(microphthalmia));
  }


}
