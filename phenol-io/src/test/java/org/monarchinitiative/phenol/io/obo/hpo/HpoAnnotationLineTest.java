package org.monarchinitiative.phenol.io.obo.hpo;


import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;

import java.util.Arrays;
import java.util.stream.Collectors;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HpoAnnotationLineTest {

  private final static String EMPTY_STRING="";

  private HpoAnnotationLine makeLine(String items[]) {
    String line = Arrays.stream(items).collect(Collectors.joining("\t"));
    return new HpoAnnotationLine(line);
  }



  @Test
  public void test1(){
    String items[]={"OMIM",
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
    assertEquals("OMIM",hpoAnnot.getDatabase());
    assertEquals("269150", hpoAnnot.getDBObjectId());
    assertEquals("OMIM:269150", hpoAnnot.getDiseaseId());
    assertEquals("SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME", hpoAnnot.getDbObjectName());
    assertFalse(hpoAnnot.isNOT());
    assertEquals(EMPTY_STRING,hpoAnnot.getFrequency());

  }


}
