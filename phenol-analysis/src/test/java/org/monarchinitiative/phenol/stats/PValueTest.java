package org.monarchinitiative.phenol.stats;


import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PValueTest {

  private final static TermId tid1 = TermId.of("HP:0001234");
  private final static TermId tid2 = TermId.of("HP:0001235");

  @Test
  public void testCtor() {
    double p = 0.01;
    PValue pval = new PValue(tid1, p);
    assertEquals(p, pval.getRawPValue());
    assertEquals(tid1, pval.getItem());
  }

  /**
   * Sorting the P-values should put the smaller ("most signficant") p values first.
   */
  @Test
  public void testSort() {
    double p1 = 0.01;
    double p2 = 0.005;
    PValue pval1 = new PValue(tid1, p1);
    PValue pval2 = new PValue(tid2, p2);
    assertTrue(pval1.compareTo(pval2) > 0);
    List<PValue> plist = new ArrayList<>();
    plist.add(pval1);
    plist.add(pval2);
    assertEquals(pval1, plist.get(0));
    Collections.sort(plist);
    assertEquals(pval2, plist.get(0));
  }

}
