package org.monarchinitiative.phenol.io.annotations.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.monarchinitiative.phenol.formats.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT;
import static org.monarchinitiative.phenol.formats.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_RECESSIVE;

public class OrphanetInheritanceXMLParserTest {


  static private OrphanetInheritanceXMLParser parser;

  @BeforeAll
  private static void init()  {
    Path orphaXMLpath = Paths.get("src", "test", "resources", "annotations", "en_product9_ages-small.xml");
    //orphaXMLpath = Paths.get("..","en_product9_ages.xml");
    Path hpOboPath = Paths.get("src", "test", "resources", "annotations", "hp_head.obo");
    Ontology ontology = OntologyLoader.loadOntology(hpOboPath.toFile());
    try {
      parser = new OrphanetInheritanceXMLParser(orphaXMLpath.toAbsolutePath().toString(), ontology);
    } catch (Exception e) {
      System.err.println("Could not parse Orpha " + e.getMessage());
      throw e;
    }
  }

  List<HpoAnnotationEntry> getModesIfInheritance(TermId diseaseId) {
    if (parser.getDisease2inheritanceMultimap().containsKey(diseaseId)) {
      Collection<HpoAnnotationEntry> c = parser.getDisease2inheritanceMultimap().get(diseaseId);
      return new ArrayList<>(c);
    }
    return new ArrayList<>(); // empty
  }

  @Test
  void testNotNull() {
    assertNotNull(parser);
  }

  /*
  166024: Multiple epiphyseal dysplasia, Al-Gazali type has one MoI: autosomal recessive
   */
  @Test
  void testMED_AlGazali() {
    TermId diseaseId = TermId.of("ORPHA:166024");
    List<HpoAnnotationEntry> entrylist = getModesIfInheritance(diseaseId);
    assertEquals(1,entrylist.size());
    HpoAnnotationEntry entry = entrylist.get(0);
    assertEquals(AUTOSOMAL_RECESSIVE,entry.getPhenotypeId());
    assertEquals("Autosomal recessive inheritance",entry.getPhenotypeLabel());
    String biocuration = entry.getBiocuration();
    // the biocuration string is ORPHA:orphadata[%s] where %s is today's date
    assertTrue(biocuration.startsWith("ORPHA:orphadata"));
    assertEquals(diseaseId.getValue(),entry.getDiseaseID());
  }


  /*
  166032:Multiple epiphyseal dysplasia, with miniepiphyses
  Has no inheritance annotations
   */
  @Test
  void testMED_miniepiphyses() {
    TermId diseaseId = TermId.of("ORPHA:166032");
    List<HpoAnnotationEntry> entrylist = getModesIfInheritance(diseaseId);
    assertTrue(entrylist.isEmpty());
  }

  /*
  58:Alexander disease  autosomal dominant
   */
  @Test
  void testAlexander() {
    TermId diseaseId = TermId.of("ORPHA:58");
    List<HpoAnnotationEntry> entrylist = getModesIfInheritance(diseaseId);
    assertEquals(1,entrylist.size());
    HpoAnnotationEntry entry = entrylist.get(0);
    assertEquals(AUTOSOMAL_DOMINANT,entry.getPhenotypeId());
    assertEquals("Autosomal dominant inheritance",entry.getPhenotypeLabel());
    String biocuration = entry.getBiocuration();
    // the biocuration string is ORPHA:orphadata[%s] where %s is today's date
    assertTrue(biocuration.startsWith("ORPHA:orphadata"));
    assertEquals(diseaseId.getValue(),entry.getDiseaseID());
  }

  /*
  166029:Multiple epiphyseal dysplasia, with severe proximal femoral dysplasia
   Has no inheritance annotations
   */
  @Test
  void testMED_severeProximalFemoralDysplasia() {
    TermId diseaseId = TermId.of("ORPHA:166029");
    List<HpoAnnotationEntry> entrylist = getModesIfInheritance(diseaseId);
    assertTrue(entrylist.isEmpty());
  }


  /*
  *  61:Alpha-mannosidosis: autosomal recessive
   */
  @Test
  void testAlphaMannosidosis() {
    TermId diseaseId = TermId.of("ORPHA:61");
    List<HpoAnnotationEntry> entrylist = getModesIfInheritance(diseaseId);
    assertEquals(1,entrylist.size());
    HpoAnnotationEntry entry = entrylist.get(0);
    assertEquals(AUTOSOMAL_RECESSIVE,entry.getPhenotypeId());
    assertEquals("Autosomal recessive inheritance",entry.getPhenotypeLabel());
    String biocuration = entry.getBiocuration();
    // the biocuration string is ORPHA:orphadata[%s] where %s is today's date
    assertTrue(biocuration.startsWith("ORPHA:orphadata"));
    assertEquals(diseaseId.getValue(),entry.getDiseaseID());
  }

  /*
  98994: Total early-onset cataract: both recessive and dominant
   */
  @Test
  void testTotalEarlyOnsetCataract() {
    TermId diseaseId = TermId.of("ORPHA:98994");
    List<HpoAnnotationEntry> entrylist = getModesIfInheritance(diseaseId);
    assertEquals(2,entrylist.size());
  }


  @Test
  void aufspueren() {

  }
}
