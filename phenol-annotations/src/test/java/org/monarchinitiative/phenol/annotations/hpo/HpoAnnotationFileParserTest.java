package org.monarchinitiative.phenol.annotations.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.category.HpoCategoryMapTest;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * See also {@link HpoAnnotationModelTest}. This class tests merging od phenotype entries and calculation of the
 * correct frequencies.
 */
@Disabled("Disabled until we have a toy hpo.json")
class HpoAnnotationFileParserTest {

  private static HpoAnnotationModel smallFileOmim123456;
  private static HpoAnnotationModel smallFileOmim614114;


  @BeforeAll
  public static void init() throws PhenolException, IOException {
    final String hpOboPath = "/hp_head.obo";
    URL hpOboURL = HpoCategoryMapTest.class.getResource(hpOboPath);
    if (hpOboURL == null) {
      throw new IOException("Could not find hpOboPath at " + hpOboPath);
    }
    File file = new File(hpOboURL.getFile());
    Ontology ontology = OntologyLoader.loadOntology(file);
    String omimPath =  "/annotations/OMIM-123456.tab";
    URL omimURL = HpoCategoryMapTest.class.getResource(omimPath);
    HpoAnnotationFileParser parser = new HpoAnnotationFileParser(new File(omimURL.getFile()),ontology);
    smallFileOmim123456 = parser.parse();
    // get small file 2
    String omim614114path ="/annotations/OMIM-614114.tab";
    URL omim614114URL = HpoCategoryMapTest.class.getResource(omim614114path);
    // TODO there is some error here, but we need to refactor to JSON anyway
//    HpoAnnotationFileParser parser2 = new HpoAnnotationFileParser(new File(omim614114URL.getFile()),ontology);
//    smallFileOmim614114 = parser2.parse();
  }

  /**
   * The raw small file has three annotation lines: HP:0000006	Autosomal dominant inheritance
   * and HP:0000528	Anophthalmia (once with frequency 3/4, and once with frequency 2/5).
   * There are also two annotations to HP:0007686	Abnormal pupillary function
   */
  @Test
  void testNumberOfAnnotationsPremerge() {
    int expected=7;
    assertEquals(expected, smallFileOmim123456.getEntryList().size());
  }

  /**
   * There are four unique HPO terms used in annotations.
   */
  @Test
  void testNumberOfAnnotationsPostmerge() {
    int expected=4;
    HpoAnnotationModel mergedModel = smallFileOmim123456.getMergedModel();
    assertEquals(expected,mergedModel.getEntryList().size());
    // There were two entries for Anopthalmia, 3/4 and 2/5. Thus we expect 5/9
    String expectedFrequency = "5/9";
    TermId anopthalmia = TermId.of("HP:0000528");
    List<HpoAnnotationEntry> entryList =mergedModel.getEntryList();
    HpoAnnotationEntry anophthalmiaEntry=null;
    for (HpoAnnotationEntry entry : entryList) {
      if (entry.getPhenotypeId().equals(anopthalmia)) {
        anophthalmiaEntry=entry;
      }
    }
    assertNotNull(anophthalmiaEntry);
    assertEquals(expectedFrequency,anophthalmiaEntry.getFrequencyModifier());
    Set<String> expectedBiocuration= Set.of("HP:probinson[2018-05-28]","HP:probinson[2019-05-28]");
    String actualBiocuration=anophthalmiaEntry.getBiocuration();
    String[] bioc=actualBiocuration.split(";");
    for (String biocurationItem : bioc) {
      assertTrue(expectedBiocuration.contains(biocurationItem));
    }

    Set<String> expectedPublication=Set.of("PMID:3214","PMID:1234");
    String actualPub=anophthalmiaEntry.getPublication();
    String []pubs=actualPub.split(";");
    for (String pub:pubs) {
      assertTrue(expectedPublication.contains(pub));
    }
  }

//  @Test
//  public void test614114() throws HpoAnnotationModelException {
//      List<HpoAnnotationEntry> entries = smallFileOmim123456.getEntryList();
//      assertEquals(2, entries);
//  }



}
