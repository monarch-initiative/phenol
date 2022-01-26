package org.monarchinitiative.phenol.analysis.stats.mtc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.analysis.stats.PValue;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonferroniTest {

  private static List<PValue> pvallist;

  private static final double EPSILON=0.00001;

  @BeforeAll
  public static void init() {
    pvallist =  new MadeUpPValues().getRawPValues();
  }


  @Test
  public void testGet15Pvalues() {
    MultipleTestingCorrection bonf = new Bonferroni();
    bonf.adjustPvals(pvallist);
    int expectedNumberOfPValues=15;
    assertEquals(expectedNumberOfPValues,pvallist.size());
  }


  @Test
  public void testA() {
    MultipleTestingCorrection bonf = new Bonferroni();
    bonf.adjustPvals(pvallist);
    // index 0
    double adjustedPValue = pvallist.get(0).getAdjustedPValue(); // raw value was 0.0001
    // Bonferoni is 15x0.0001
    assertEquals(15*0.0001,adjustedPValue,EPSILON);

  }

  @Test
  public void testB() {
    MultipleTestingCorrection bonf = new Bonferroni();
    bonf.adjustPvals(pvallist);
    double adjustedPValue = pvallist.get(1).getAdjustedPValue();// raw value was 0.0004
    // Bonferoni is 15x0.0001
    assertEquals(15*0.0004,adjustedPValue,EPSILON);
  }



  @Test
  public void testN() {
    //pvalmap.put(N,new PValue(0.7590));
    MultipleTestingCorrection bonf = new Bonferroni();
    bonf.adjustPvals(pvallist);
    // index of N is 14
    double adjustedPValue = pvallist.get(14).getAdjustedPValue(); // raw value was 0.7590
    // Bonferoni is 15x0.7590 thus should be 1.0
    assertEquals(1.000,adjustedPValue,EPSILON);
  }


}

