package org.monarchinitiative.phenol.stats;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonferroniTest {

  static IPValueCalculation ipval;

  static final double EPSILON=0.00001;

  @BeforeAll
  static void init() {
    ipval =  new MadeUpPValues();

  }


  @Test
  void testGet15Pvalues() {
    AbstractTestCorrection bonf = new Bonferroni();
    Map<TermId, PValue> pvalmap = bonf.adjustPValues(ipval);
    int expectedNumberOfPValues=15;
    assertEquals(expectedNumberOfPValues,pvalmap.size());
  }


  @Test
  void testA() {
    //  pvalmap.put(A,new PValue(0.0001));
    //    pvalmap.put(B,new PValue(0.0004));
    AbstractTestCorrection bonf = new Bonferroni();
    Map<TermId, PValue> pvalmap = bonf.adjustPValues(ipval);
    PValue pval = pvalmap.get(MadeUpPValues.A); // raw value was 0.0001
    // Bonferoni is 15x0.0001
    assertEquals(15*0.0001,pval.getAdjustedPValue(),EPSILON);

  }

  @Test
  void testB() {
    //    pvalmap.put(B,new PValue(0.0004));
    AbstractTestCorrection bonf = new Bonferroni();
    Map<TermId, PValue> pvalmap = bonf.adjustPValues(ipval);
    PValue pval = pvalmap.get(MadeUpPValues.B); // raw value was 0.0004
    // Bonferoni is 15x0.0001
    assertEquals(15*0.0004,pval.getAdjustedPValue(),EPSILON);
  }



  @Test
  void testN() {
    //pvalmap.put(N,new PValue(0.7590));
    AbstractTestCorrection bonf = new Bonferroni();
    Map<TermId, PValue> pvalmap = bonf.adjustPValues(ipval);
    PValue pval = pvalmap.get(MadeUpPValues.N); // raw value was 0.7590
    // Bonferoni is 15x0.7590 thus should be 1.0
    assertEquals(1.000,pval.getAdjustedPValue(),EPSILON);
  }


}

