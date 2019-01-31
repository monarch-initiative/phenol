package org.monarchinitiative.phenol.stats;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test whether the BH implementation is working correctly
 * p<-c(0.0001,0.0004,0.0019,0.0095,0.0201,0.0278,0.0298,0.0344,0.0459,0.3240,0.4262,0.5719,0.6528,0.7590,1.000)
 * > p_adj=p.adjust(p,method="BH")
 * > p_adj
 *  [1] 0.00150000 0.00300000 0.00950000 0.03562500 0.06030000 0.06385714 0.06385714 0.06450000 0.07650000 0.48600000 0.58118182
 * [12] 0.71487500 0.75323077 0.81321429 1.00000000
 */
public class BenjaminiHochbergTest {


  private static PValueCalculation ipval;

  private static final double EPSILON=0.00001;

  private static List<Item2PValue<TermId>> pvallist;



  @BeforeAll
  static void init() {
    pvallist =  new MadeUpPValues().getRawPValues();
  }

  /** 0.0001 and  0.00150000 */
  @Test
  void testA() {
    MultipleTestingCorrection<TermId> bonf = new BenjaminiHochberg<>();
    bonf.adjustPvals(pvallist);
    // index 0
    double adjpval = pvallist.get(0).getAdjustedPValue(); // raw value was 0.0001 // raw value was 0.0001
    assertEquals(0.00150000,adjpval,EPSILON);
  }

  /** 0.0001 and  0.00150000 */
  @Test
  void testB() {
    MultipleTestingCorrection<TermId> bonf = new BenjaminiHochberg<>();
    bonf.adjustPvals(pvallist);
    // index 0
    double adjustedPValue = pvallist.get(1).getAdjustedPValue(); // raw value was 0.0001 // raw value was 0.0001
    assertEquals(0.00300000,adjustedPValue,EPSILON);
  }

  /** 0.0001 and  0.00150000 */
  @Test
  void testC() {
    MultipleTestingCorrection<TermId> bonf = new BenjaminiHochberg<>();
    bonf.adjustPvals(pvallist);
    // index 0
    double adjustedPValue = pvallist.get(2).getAdjustedPValue(); // raw value was 0.0001// raw value was 0.0001
    assertEquals(0.00950000,adjustedPValue,EPSILON);
  }



}
