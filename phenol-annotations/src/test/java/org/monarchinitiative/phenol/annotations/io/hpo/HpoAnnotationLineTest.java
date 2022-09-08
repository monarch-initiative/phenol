package org.monarchinitiative.phenol.annotations.io.hpo;


import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HpoAnnotationLineTest {

  private static HpoAnnotationLineOld makeLine(String[] items) throws PhenolException {
    String line = String.join("\t", items);
    return HpoAnnotationLineOld.constructFromString(line);
  }

  @Test
  public void test1() throws PhenolException {
    String[] items = {"OMIM:269150",
      "SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME",
      "",
      "HP:0030736",
      "OMIM:269150",
      "TAS",
      "",
      "",
      "",
      "",
      "P",
      "HPO:skoehler[2017-07-13]"};
    List<String> publist = new ArrayList<>();
    publist.add("OMIM:269150");
    HpoAnnotationLineOld hpoAnnot = makeLine(items);
    assertEquals("OMIM:269150", hpoAnnot.getDiseaseId());
    assertEquals("SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME", hpoAnnot.getDatabaseObjectName());
    assertEquals("TAS", hpoAnnot.getEvidence());
    assertEquals(publist, hpoAnnot.getPublication());
    assertEquals("P", hpoAnnot.getAspect());
    assertEquals("HPO:skoehler[2017-07-13]", hpoAnnot.getBiocuration());
    assertFalse(hpoAnnot.isNOT());
    assertEquals("", hpoAnnot.getFrequency());
    TermId diseaseId = hpoAnnot.getDiseaseTermId().get();
    TermId expected = TermId.of("OMIM:269150");
    assertEquals(expected, diseaseId);

  }

  /**
   * The first field ("BadPrefix" instead of "OMIM") is invalid and should throw an exception.
   *
   */
  @Test
  public void malformedDiseaseDatabasePrefix() {
    String[] items = {"BadPrefix",
      "269150",
      "SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME",
      "",
      "HP:0030736",
      "OMIM:269150",
      "TAS",
      "",
      "",
      "",
      "",
      "P",
      "2017-07-13",
      "HPO:skoehler"};
    assertThrows(PhenolException.class, () -> makeLine(items));
  }


  /**
   * Very frequent HP:0040281; Present in 80% to 99% of the cases.
   * The expected mean is (0.8+0.99)/2=0.895
   */
  @Test
  public void testGetFrequencyFromVeryFrequentTerm() throws PhenolException {
    String[] items = {
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040281",  //Frequency
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLineOld line = makeLine(items);
    assertEquals("OMIM:123456", line.getDiseaseId());
    assertEquals("HP:0040281", line.getFrequency());
  }


  /**
   * Very rare HP:0040284; Present in 1% to 4% of the cases.
   * The expected mean is (0.01+0.04)/2=0.025
   */
  @Test
  public void testGetFrequencyFromVeryRareTerm() throws PhenolException {
    String[] items = {
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040284",  //Frequency [VERY RARE]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLineOld line = makeLine(items);

    assertEquals("OMIM:123456", line.getDiseaseId());
    assertEquals("HP:0040284", line.getFrequency());
  }

  /**
   * Occasional HP:0040283; Present in 5% to 29% of the cases.
   * The expected mean is (0.05+0.29)/2=0.17
   */
  @Test
  public void testGetFrequencyFromOccassionalTerm() throws PhenolException {
    String[] items = {
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040283",  //Frequency [OCCASIONAL]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLineOld line = makeLine(items);
    assertEquals("OMIM:123456", line.getDiseaseId());
    assertEquals("HP:0040283", line.getFrequency());
  }


  /**
   * Frequent HP:0040282; Present in 30% to 79% of the cases.
   * The expected mean is (0.30+0.79)/2=0.545
   */
  @Test
  public void testGetFrequencyFromFrequentTerm() throws PhenolException {
    String[] items = {
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040282",  //Frequency [OCCASIONAL]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLineOld line = makeLine(items);
    assertEquals("OMIM:123456", line.getDiseaseId());

  }

  /**
   * Frequent HP:0040280; Present in 100%  of the cases.
   * The expected mean is 1.0
   */
  @Test
  public void testGetFrequencyFromObligateTerm() throws PhenolException {
    String[] items = {
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040280",  //Frequency [OBLIGATE]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLineOld line = makeLine(items);
    assertEquals("OMIM:123456", line.getDiseaseId());
    assertEquals("HP:0040280", line.getFrequency());
  }

  /**
   * Excluded HP:0040285; Present in 0%  of the cases.
   * The expected mean is 0.0
   */
  @Test
  public void testGetFrequencyFromExcludedTerm() throws PhenolException {
    String[] items = {
      "OMIM:123456", // DatabaseID
      "Example",     //DiseaseName
      "",            //Qualifier
      "HP:0030736",  //HPO_ID
      "OMIM:123456", //Reference
      "TAS",         //Evidence
      "",            // Onset
      "HP:0040285",  //Frequency [EXCLUDED]
      "",            //Sex
      "",            //Modifier
      "P",           //Aspect
      "HPO:skoehler[2017-07-13]" //Biocuration
    };
    HpoAnnotationLineOld line = makeLine(items);
    assertEquals("OMIM:123456", line.getDiseaseId());
    assertEquals("HP:0040285", line.getFrequency());
  }

}
