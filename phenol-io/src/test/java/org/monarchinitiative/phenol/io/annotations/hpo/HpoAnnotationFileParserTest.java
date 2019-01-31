package org.monarchinitiative.phenol.io.annotations.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * See also {@link HpoAnnotationModelTest}. This class tests merging od phenotype entries and calculation of the
 * correct frequencies.
 */
class HpoAnnotationFileParserTest {

  private static Ontology ontology;
  private static HpoAnnotationModel smallFile1 =null;


  @BeforeAll
  static void init() throws PhenolException {
    Path hpOboPath = Paths.get("src","test","resources","annotations","hp_head.obo");
    ontology = OntologyLoader.loadOntology(hpOboPath.toFile());
    Path omim123456path = Paths.get("src","test","resources","annotations","OMIM-123456.tab");
    String omim123456file = omim123456path.toAbsolutePath().toString();
    HpoAnnotationFileParser parser = new HpoAnnotationFileParser(omim123456file,ontology);
    smallFile1 = parser.parse();
  }

  /**
   * The raw small file has three annotation lines: HP:0000006	Autosomal dominant inheritance
   * and HP:0000528	Anophthalmia (once with frequency 3/4, and once with frequency 2/5).
   */
  @Test
  void testNumberOfAnnotationsPremerge() {
    int expected=3;
    assertEquals(expected,smallFile1.getEntryList().size());
  }

  @Test
  void testNumberOfAnnotationsPostmerge() {
    int expected=2;
    HpoAnnotationModel mergedModel = smallFile1.getMergedModel();
    assertEquals(expected,mergedModel.getEntryList().size());
    // There were two entries for Anopthalmia, 3/4 and 2/5. Thus we expect 5/9
    String expectedFrequency = "5/9";
    TermId anopthalmia = TermId.of("HP:0000528");
    List<HpoAnnotationEntry> entryList =mergedModel.getEntryList();
    for (HpoAnnotationEntry entry : entryList) {
      if (entry.getPhenotypeId().equals(anopthalmia)) {
        assertEquals(expectedFrequency,entry.getFrequencyModifier());
      }
    }
  }

}
