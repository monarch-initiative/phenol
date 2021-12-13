package org.monarchinitiative.phenol.annotations.obo.hpo;


import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.category.HpoCategoryMapTest;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HpoAnnotationLineTest {

  private final static double EPSILON = 0.00001;

  private final Ontology ontology;

  HpoAnnotationLineTest() throws PhenolException, IOException {
    final String hpOboPath = "hp_head.obo";
    ClassLoader classLoader = HpoCategoryMapTest.class.getClassLoader();
    URL hpOboURL = classLoader.getResource(hpOboPath);
    if (hpOboURL == null) {
      throw new IOException("Could not find hpOboPath at " + hpOboPath);
    }
    File file = new File(hpOboURL.getFile());
    this.ontology = OntologyLoader.loadOntology(file);
  }

  private HpoAnnotationLine makeLine(String[] items) throws PhenolException {
    String line = String.join("\t", items);
    return HpoAnnotationLine.constructFromString(line);
  }

  @Test
  void test1() throws PhenolException{
    String[] items ={"OMIM:269150",
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
    HpoAnnotationLine hpoAnnot = makeLine(items);
    assertEquals("OMIM:269150",hpoAnnot.getDiseaseId());
    assertEquals("SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME", hpoAnnot.getDbObjectName());
    assertEquals("TAS",hpoAnnot.getEvidence());
    assertEquals(publist,hpoAnnot.getPublication());
    assertEquals("P",hpoAnnot.getAspect());
    assertEquals("HPO:skoehler[2017-07-13]", hpoAnnot.getBiocuration());
    assertFalse(hpoAnnot.isNOT());
    assertEquals("", hpoAnnot.getFrequency());
    TermId diseaseId = hpoAnnot.getDiseaseTermId();
    TermId expected = TermId.of("OMIM:269150");
    assertEquals(expected,diseaseId);

  }

  /**
   * The first field ("BadPrefix" instead of "OMIM") is invalid and should throw an exception.
   * @throws PhenolException expected to throw because prefix is malformed
   */
  @Test
  void malformedDiseaseDatabasePrefix() throws PhenolException {
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
  void testGetFrequencyFromVeryFrequentTerm() throws PhenolException{
    String[] items ={
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
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line, ontology);
    TermId expectedId = TermId.of("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.895;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }




  /**
   * Very rare HP:0040284; Present in 1% to 4% of the cases.
   * The expected mean is (0.01+0.04)/2=0.025
   */
  @Test
  void testGetFrequencyFromVeryRareTerm() throws PhenolException{
    String[] items ={
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
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.of("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.025;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }

  /**
   * Occasional HP:0040283; Present in 5% to 29% of the cases.
   * The expected mean is (0.05+0.29)/2=0.17
   */
  @Test
  void testGetFrequencyFromOccassionalTerm() throws PhenolException{
    String[] items ={
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
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.of("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.17;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }


  /**
   * Frequent HP:0040282; Present in 30% to 79% of the cases.
   * The expected mean is (0.30+0.79)/2=0.545
   */
  @Test
  void testGetFrequencyFromFrequentTerm() throws PhenolException{
    String[] items ={
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
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.of("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.545;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }

  /**
   * Frequent HP:0040280; Present in 100%  of the cases.
   * The expected mean is 1.0
   */
  @Test
  void testGetFrequencyFromObligateTerm() throws PhenolException{
    String[] items ={
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
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.of("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=1.0;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }

  /**
   * Excluded HP:0040285; Present in 0%  of the cases.
   * The expected mean is 0.0
   */
  @Test
  void testGetFrequencyFromExcludedTerm() throws PhenolException{
    String[] items ={
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
    HpoAnnotationLine line = makeLine(items);
    HpoAnnotation annot = HpoAnnotationLine.toHpoAnnotation(line,ontology);
    TermId expectedId = TermId.of("OMIM:123456");
    assertEquals(expectedId,line.getDiseaseTermId());
    double expectedFrequency=0.0;
    assertEquals(expectedFrequency,annot.getFrequency(),EPSILON);
  }



}
