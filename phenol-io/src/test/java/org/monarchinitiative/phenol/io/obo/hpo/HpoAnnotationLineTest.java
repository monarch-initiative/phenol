package org.monarchinitiative.phenol.io.obo.hpo;


import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.stream.Collectors;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HpoAnnotationLineTest {

  private final static String EMPTY_STRING="";

  private HpoAnnotationLine makeLine(String items[]) throws PhenolException {
    String line = Arrays.stream(items).collect(Collectors.joining("\t"));
    return HpoAnnotationLine.constructFromString(line);
  }



  @Test
  public void test1() throws PhenolException{
    String items[]={"OMIM:269150",
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
    HpoAnnotationLine hpoAnnot = makeLine(items);
    assertEquals("OMIM:269150",hpoAnnot.getDiseaseId());
    assertEquals("SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME", hpoAnnot.getDbObjectName());
    assertEquals("TAS",hpoAnnot.getEvidence());
    assertEquals("OMIM:269150",hpoAnnot.getPublication());
    assertEquals("P",hpoAnnot.getAspect());
    assertEquals("HPO:skoehler[2017-07-13]", hpoAnnot.getBiocuration());
    assertFalse(hpoAnnot.isNOT());
    assertEquals(EMPTY_STRING,hpoAnnot.getFrequency());
    TermId diseaseId = hpoAnnot.getDiseaseTermId();
    TermId expected = TermId.constructWithPrefix("OMIM:269150");
    assertEquals(expected,diseaseId);

  }

  /**
   * The first field ("BadPrefix" instead of "OMIM") is invalid and should throw an exception.
   * @throws PhenolException expected to throw because prefix is malformed
   */
  @Test(expected = PhenolException.class)
  public void malformedDiseaseDatabasePrefix() throws PhenolException {
    String items[]={"BadPrefix",
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
    HpoAnnotationLine hpoAnnot = makeLine(items);
    TermId diseaseId = hpoAnnot.getDiseaseTermId();

  }


}
