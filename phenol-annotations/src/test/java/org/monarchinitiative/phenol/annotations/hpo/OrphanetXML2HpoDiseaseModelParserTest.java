package org.monarchinitiative.phenol.annotations.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.category.HpoCategoryMapTest;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the Orphanet HPO file parser.
 * Oprhanet switched its format from the en_product4_HPO.xml to en_product4.xml file.
 * This class was updated in July 2020 accordingly.
 *
 * @author Peter N Robinson
 */
@Disabled("Disabled until we have a toy hpo.json")
class OrphanetXML2HpoDiseaseModelParserTest {

  static private OrphanetXML2HpoDiseaseModelParser parser;

  static private Map<TermId, HpoAnnotationModel> diseaseModels;

  static private final TermId veryFrequent = TermId.of("HP:0040281");

  static private final TermId frequent = TermId.of("HP:0040282");

  @BeforeAll
  public static void init() throws IOException {
    final String hpOboPath = "/hp_head.obo";
    URL hpOboURL = HpoCategoryMapTest.class.getResource(hpOboPath);
    if (hpOboURL == null) {
      throw new IOException("Could not find hpOboPath at " + hpOboPath);
    }
    File file = new File(hpOboURL.getFile());
    Ontology hpoOntology = OntologyLoader.loadOntology(file);

    String orphaXMLpath =  "/annotations/en_product4_small.xml";
    URL orphaURL = HpoCategoryMapTest.class.getResource(orphaXMLpath);
    if (orphaURL == null) {
      throw new IOException("Could not find en_product4_small.xml at " + orphaURL);
    }
    try {
      parser = new OrphanetXML2HpoDiseaseModelParser(orphaURL.getFile(), hpoOntology, false);
    } catch (Exception e) {
      System.err.println("Could not parse Orpha " + e.getMessage());
      throw e;
    }
    diseaseModels = parser.getOrphanetDiseaseMap();
  }


  @Test
  void testNotNull() {
    assertNotNull(parser);
  }

  /**
   * There are three disease entries:
   * $ grep -c '<Disorder id' en_product4_small.xml
   * 3
   */
  @Test
  void ifThreeDiseaseModelsRetrieved_thenOk() {
    Map<TermId, HpoAnnotationModel> diseaseModels = parser.getOrphanetDiseaseMap();
    assertEquals(3, diseaseModels.size());
  }


  /**
   * consult the XML file en_product4_HPO.small.xml for the source of the ground truth here.
   */
  @Test
  void testPhenotypesAndFrequenciesOfDisease1() {
    TermId diseaseId = TermId.of("ORPHA:61");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    List<HpoAnnotationEntry> entrylist = file.getEntryList();
    // the first disease has two annotations
    int expectNumberOfAnnotations = 2;
    assertEquals(expectNumberOfAnnotations, entrylist.size());
    // Lacrimation abnormality = "HP:0000632")
    TermId AbnGlobeLoc = TermId.of("HP:0000632");
    Optional<HpoAnnotationEntry> entryOpt = entrylist.stream().
        filter(e -> e.getPhenotypeId().
        equals(AbnGlobeLoc)).
        findFirst();
    assertTrue(entryOpt.isPresent());
    assertEquals(veryFrequent.getValue(), entryOpt.get().getFrequencyModifier());
    // Anophthalmia = HP:0000528
    TermId Anophthalmia = TermId.of("HP:0000528");
    entryOpt = entrylist.stream().
      filter(e -> e.getPhenotypeId().
        equals(Anophthalmia)).
      findFirst();
    assertTrue(entryOpt.isPresent());
    assertEquals(veryFrequent.getValue(), entryOpt.get().getFrequencyModifier());
  }

  /**
   * consult the XML file en_product4_small.xml for the source of the ground truth here.
   */
  @Test
  void testPhenotypesAndFrequenciesOfDisease3() {
    TermId diseaseId = TermId.of("ORPHA:585");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    List<HpoAnnotationEntry> entrylist = file.getEntryList();
    int expectNumberOfAnnotations = 1;
    assertEquals(expectNumberOfAnnotations, entrylist.size());
    // Abnormal pupillary function = HP:0007686
    TermId AbnPupillaryFunction = TermId.of("HP:0007686");
    Optional<HpoAnnotationEntry> entryOpt = entrylist.stream().
      filter(e -> e.getPhenotypeId().
        equals(AbnPupillaryFunction)).
      findFirst();
    assertTrue(entryOpt.isPresent());
    assertEquals(frequent.getValue(), entryOpt.get().getFrequencyModifier());
  }


  @Test
  void testGetDiseaseName1() {
    String diseaseName = "Alpha-mannosidosis";
    TermId diseaseId = TermId.of("ORPHA:61");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    assertEquals(diseaseName, file.getDiseaseName());
  }

  @Test
  void testGetDiseaseName2() {
    String diseaseName = "Aspartylglucosaminuria";
    TermId diseaseId = TermId.of("ORPHA:93");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    assertEquals(diseaseName, file.getDiseaseName());
  }

  @Test
  void testGetDiseaseName3() {
    String diseaseName = "Multiple sulfatase deficiency";
    TermId diseaseId = TermId.of("ORPHA:585");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    assertEquals(diseaseName, file.getDiseaseName());
  }


}
