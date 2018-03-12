package org.monarchinitiative.phenol.io.obo.hpo;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class HpoAnnotationLineTest {

  private final static double EPSILON=0.000001;
  private final static String EMPTY_STRING="";

  private HpoAnnotationLine makeLine(String items[]) throws PhenolException {
    String line = Arrays.stream(items).collect(Collectors.joining("\t"));
    return new HpoAnnotationLine(line);
  }



  @Test
  public void test1() throws PhenolException{
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
    double expectedFrequency=1.0;
    assertEquals(EMPTY_STRING,hpoAnnot.getFrequency());

  }


}
