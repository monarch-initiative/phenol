package org.monarchinitiative.phenol.annotations.hpo;


import org.junit.jupiter.api.Assertions;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Until deprecation")
class HpoAnnotationModelTest {
    private static HpoAnnotationModel v2sf=null;


    @BeforeAll
    public static void init() throws PhenolException, IOException {
      URL hpOboURL = HpoCategoryMapTest.class.getResource("/hp_head.obo");
        File file = new File(hpOboURL.getFile());
        Ontology ontology = OntologyLoader.loadOntology(file);
      URL omim123456URL = HpoCategoryMapTest.class.getResource("/annotations/OMIM-123456.tab");
        HpoAnnotationFileParser parser = new HpoAnnotationFileParser(omim123456URL.getFile(),ontology);
        v2sf = parser.parse();
    }

    @Test
    void testParse() {
       Assertions.assertNotNull(v2sf);
    }

    @Test
    void basenameTest() {
        Assertions.assertEquals("OMIM-123456.tab", v2sf.getBasename());
    }

    @Test
    void isOmimTest() {
        Assertions.assertTrue(v2sf.isOMIM());
        Assertions.assertFalse(v2sf.isDECIPHER());
    }

    /** Our test file has seven annotation lines. */
    @Test
    void numberOfAnnotationsTest() {
        Assertions.assertEquals(7,v2sf.getNumberOfAnnotations());
    }

    @Test
  void testFrequencyMerge() {
     HpoAnnotationModel merged= v2sf.getMergedModel();
     assertEquals(4,merged.getNumberOfAnnotations());
    }


  @Test
  void testMergeAbnormalPupillaryFunction() {
    int expected=4;
    HpoAnnotationModel mergedModel = v2sf.getMergedModel();
    assertEquals(expected,mergedModel.getEntryList().size());
    // There were two entries for Anopthalmia, 48% and 2/5. Thus we expect 5/10+2/5=7/15
    //HP:0040282
    String expectedFrequency = "7/15";
    TermId abnormalPupillaryFunction = TermId.of("HP:0007686");
    List<HpoAnnotationEntry> entryList =mergedModel.getEntryList();
    for (HpoAnnotationEntry entry : entryList) {
      if (entry.getPhenotypeId().equals(abnormalPupillaryFunction)) {
        assertEquals(expectedFrequency,entry.getFrequencyModifier());
      }
    }
  }

  /**
   * This tests whether we can merge a frequency from an HPO Frequency term
   * HP:0040282 is "Frequent", defined as the mean of 30% and 79%
   * thus 109/2=54.5, or 5/10 after rounding and normalizing to ten observations.
   *
   */
  @Test
  void testMergeLacrimationAbnormalityFunction() {
    HpoAnnotationModel mergedModel = v2sf.getMergedModel();
    // There were two entries for Lacrimation Abnormality, HP:0040282 and 12/42.
    //HP:0040282 is frequent, 30% to 79, thus 109/2=54.5, or 5/10
    // thus we have 12/42+5/10=17/52
    String expectedFrequency = "17/52";
    TermId LacrimationAbnormality = TermId.of("HP:0040282");
    List<HpoAnnotationEntry> entryList =mergedModel.getEntryList();
    for (HpoAnnotationEntry entry : entryList) {
      if (entry.getPhenotypeId().equals(LacrimationAbnormality)) {
        assertEquals(expectedFrequency,entry.getFrequencyModifier());
      }
    }
  }

}
