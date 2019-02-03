package org.monarchinitiative.phenol.stats;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *  p<-c(0.0001,0.0004,0.0019,0.0095,0.0201,0.0278,0.0298,0.0344,0.0459,0.3240,0.4262,0.5719,0.6528,0.7590,1.000)
 * > p
 *  [1] 0.0001 0.0004 0.0019 0.0095 0.0201 0.0278 0.0298 0.0344 0.0459 0.3240 0.4262 0.5719 0.6528 0.7590 1.0000
 * > p_by=p.adjust(p,method="BY")
 * > p_by
 *  [1] 0.004977343 0.009954687 0.031523175 0.118211908 0.200089208 0.211892623 0.211892623 0.214025770 0.253844518 1.000000000 1.000000000
 * [12] 1.000000000 1.000000000 1.000000000 1.000000000
 */
class BenjaminiYekutieliTest {


  private static final double EPSILON=0.00001;

  private static List<Item2PValue<TermId>> pvallist;



  @BeforeAll
  static void init() {
    pvallist =  new MadeUpPValues().getRawPValues();
  }

  /** Convenience method to retrieve the correct item for testing. */
  private Item2PValue<TermId> getResult(TermId tid, List<Item2PValue<TermId>> lst) {
    for (Item2PValue<TermId> item : lst) {
      if (item.getItem().equals(tid)) {
        return item;
      }
    }
    return null;
  }


  @Test
  void testA() {
    MultipleTestingCorrection<TermId> bonf = new BenjaminiYekutieli<>();
    bonf.adjustPvals(pvallist);
    // index 0
    Item2PValue<TermId> item = getResult(MadeUpPValues.A,pvallist);
    assertNotNull(item);
    double adjpval = item.getAdjustedPValue(); // raw value was 0.0001 // raw value was 0.0001
    assertEquals(0.004977343,adjpval,EPSILON);
  }


  @Test
  void testB() {
    MultipleTestingCorrection<TermId> bonf = new BenjaminiYekutieli<>();
    bonf.adjustPvals(pvallist);
    // index 0
    Item2PValue<TermId> item = getResult(MadeUpPValues.B,pvallist);
    assertNotNull(item);
    double adjpval = item.getAdjustedPValue(); // raw value was 0.0001 // raw value was 0.0001
    assertEquals(0.009954687,adjpval,EPSILON);
  }
}
