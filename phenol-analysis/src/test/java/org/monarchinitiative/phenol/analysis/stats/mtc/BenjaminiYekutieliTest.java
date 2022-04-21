package org.monarchinitiative.phenol.analysis.stats.mtc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.analysis.stats.PValue;

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
public class BenjaminiYekutieliTest {


  private static final double EPSILON=0.00001;

  private static List<PValue> pvallist;



  @BeforeAll
  public static void init() {
    pvallist =  new MadeUpPValues().getRawPValues();
  }

  /** Convenience method to retrieve the correct item for testing. */
  private PValue getResult(TermId tid, List<PValue> lst) {
    for (PValue item : lst) {
      if (item.getItem().equals(tid)) {
        return item;
      }
    }
    return null;
  }


  @Test
  public void testA() {
    MultipleTestingCorrection bonf = new BenjaminiYekutieli();
    bonf.adjustPvals(pvallist);
    // index 0
    PValue item = getResult(MadeUpPValues.A,pvallist);
    assertNotNull(item);
    double adjpval = item.getAdjustedPValue(); // raw value was 0.0001 // raw value was 0.0001
    assertEquals(0.004977343,adjpval,EPSILON);
  }


  @Test
  public void testB() {
    MultipleTestingCorrection bonf = new BenjaminiYekutieli();
    bonf.adjustPvals(pvallist);
    // index 0
    PValue item = getResult(MadeUpPValues.B,pvallist);
    assertNotNull(item);
    double adjpval = item.getAdjustedPValue(); // raw value was 0.0001 // raw value was 0.0001
    assertEquals(0.009954687,adjpval,EPSILON);
  }
}
