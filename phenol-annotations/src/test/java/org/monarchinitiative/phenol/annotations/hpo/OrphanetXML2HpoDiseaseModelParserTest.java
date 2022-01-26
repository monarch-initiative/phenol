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
@Disabled // until we have a small HPO json with the required terms (e.g. Anopthalmia)
public class OrphanetXML2HpoDiseaseModelParserTest {

  private static OrphanetXML2HpoDiseaseModelParser parser;
  private static Map<TermId, HpoAnnotationModel> diseaseModels;
  private static final TermId veryFrequent = TermId.of("HP:0040281");
  private static final TermId frequent = TermId.of("HP:0040282");

  @BeforeAll
  public static void init() throws IOException {
    URL hpOboURL = HpoCategoryMapTest.class.getResource("/hpo_toy.json");
    Ontology hpoOntology = OntologyLoader.loadOntology(new File(hpOboURL.getFile()));

    URL orphaURL = HpoCategoryMapTest.class.getResource("/annotations/en_product4_small.xml");
    parser = new OrphanetXML2HpoDiseaseModelParser(orphaURL.getFile(), hpoOntology, false);

    diseaseModels = parser.getOrphanetDiseaseMap();
  }


  @Test
  public void testNotNull() {
    assertNotNull(parser);
  }

  /**
   * There are three disease entries:
   * $ grep -c '<Disorder id' en_product4_small.xml
   * 3
   */
  @Test
  public void ifThreeDiseaseModelsRetrieved_thenOk() {
    Map<TermId, HpoAnnotationModel> diseaseModels = parser.getOrphanetDiseaseMap();
    assertEquals(3, diseaseModels.size());
  }


  /**
   * consult the XML file en_product4_HPO.small.xml for the source of the ground truth here.
   */
  @Test
  public void testPhenotypesAndFrequenciesOfDisease1() {
    TermId diseaseId = TermId.of("ORPHA:61");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    List<HpoAnnotationEntry> entrylist = file.getEntryList();
    // the first disease has two annotations
    assertEquals(2, entrylist.size());
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
  public void testPhenotypesAndFrequenciesOfDisease3() {
    TermId diseaseId = TermId.of("ORPHA:585");
    HpoAnnotationModel file = diseaseModels.get(diseaseId);
    List<HpoAnnotationEntry> entrylist = file.getEntryList();
    assertEquals(1, entrylist.size());
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
  public void testGetDiseaseName1() {
    HpoAnnotationModel file = diseaseModels.get(TermId.of("ORPHA:61"));
    assertEquals("Alpha-mannosidosis", file.getDiseaseName());
  }

  @Test
  public void testGetDiseaseName2() {
    HpoAnnotationModel file = diseaseModels.get(TermId.of("ORPHA:93"));
    assertEquals("Aspartylglucosaminuria", file.getDiseaseName());
  }

  @Test
  public void testGetDiseaseName3() {
    HpoAnnotationModel file = diseaseModels.get(TermId.of("ORPHA:585"));
    assertEquals("Multiple sulfatase deficiency", file.getDiseaseName());
  }


}
