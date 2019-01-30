package org.monarchinitiative.phenol.stats;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonferroniTest {

  static List<Item2PValue<TermId>> pvallist;

  static final double EPSILON=0.00001;

  @BeforeAll
  static void init() {
    pvallist =  new MadeUpPValues().getRawPValues();
  }


  @Test
  void testGet15Pvalues() {
    MultipleTestingCorrection<TermId> bonf = new Bonferroni<>();
    bonf.adjustPvals(pvallist);
    int expectedNumberOfPValues=15;
    assertEquals(expectedNumberOfPValues,pvallist.size());
  }


  @Test
  void testA() {
    MultipleTestingCorrection<TermId> bonf = new Bonferroni<>();
    bonf.adjustPvals(pvallist);
    // index 0
    PValue pval = pvallist.get(0).getPVal(); // raw value was 0.0001
    // Bonferoni is 15x0.0001
    assertEquals(15*0.0001,pval.getAdjustedPValue(),EPSILON);

  }

  @Test
  void testB() {
    MultipleTestingCorrection<TermId> bonf = new Bonferroni<>();
    bonf.adjustPvals(pvallist);
    PValue pval = pvallist.get(1).getPVal();// raw value was 0.0004
    // Bonferoni is 15x0.0001
    assertEquals(15*0.0004,pval.getAdjustedPValue(),EPSILON);
  }



  @Test
  void testN() {
    //pvalmap.put(N,new PValue(0.7590));
    MultipleTestingCorrection<TermId> bonf = new Bonferroni<>();
    bonf.adjustPvals(pvallist);
    // index of N is 14
    PValue pval = pvallist.get(14).getPVal(); // raw value was 0.7590
    // Bonferoni is 15x0.7590 thus should be 1.0
    assertEquals(1.000,pval.getAdjustedPValue(),EPSILON);
  }


}

