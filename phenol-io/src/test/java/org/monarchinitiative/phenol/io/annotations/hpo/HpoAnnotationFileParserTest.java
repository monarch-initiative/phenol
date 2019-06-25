package org.monarchinitiative.phenol.io.annotations.hpo;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * See also {@link HpoAnnotationModelTest}. This class tests merging od phenotype entries and calculation of the
 * correct frequencies.
 */
class HpoAnnotationFileParserTest {

  private static HpoAnnotationModel smallFile1 =null;


  @BeforeAll
  static void init() throws PhenolException {
    Path hpOboPath = Paths.get("src","test","resources","annotations","hp_head.obo");
    Ontology ontology = OntologyLoader.loadOntology(hpOboPath.toFile());
    Path omim123456path = Paths.get("src","test","resources","annotations","OMIM-123456.tab");
    String omim123456file = omim123456path.toAbsolutePath().toString();
    HpoAnnotationFileParser parser = new HpoAnnotationFileParser(omim123456file,ontology);
    smallFile1 = parser.parse();
  }

  /**
   * The raw small file has three annotation lines: HP:0000006	Autosomal dominant inheritance
   * and HP:0000528	Anophthalmia (once with frequency 3/4, and once with frequency 2/5).
   * There are also two annotations to HP:0007686	Abnormal pupillary function
   */
  @Test
  void testNumberOfAnnotationsPremerge() {
    int expected=7;
    assertEquals(expected,smallFile1.getEntryList().size());
  }

  /**
   * There are four unique HPO terms used in annotations.
   */
  @Test
  void testNumberOfAnnotationsPostmerge() {
    int expected=4;
    HpoAnnotationModel mergedModel = smallFile1.getMergedModel();
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
    Set<String> expectedBiocuration= ImmutableSet.of("HP:probinson[2018-05-28]","HP:probinson[2019-05-28]");
    String actualBiocuration=anophthalmiaEntry.getBiocuration();
    String[] bioc=actualBiocuration.split(";");
    for (String biocurationItem : bioc) {
      assertTrue(expectedBiocuration.contains(biocurationItem));
    }

    Set<String> expectedPublication=ImmutableSet.of("PMID:3214","PMID:1234");
    String actualPub=anophthalmiaEntry.getPublication();
    String []pubs=actualPub.split(";");
    for (String pub:pubs) {
      assertTrue(expectedPublication.contains(pub));
    }
  }

}
