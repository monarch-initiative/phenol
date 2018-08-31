package org.monarchinitiative.phenol.stats;

import org.junit.Test;
import org.monarchinitative.phenol.stats.Hypergeometric;
import org.monarchinitative.phenol.stats.TermForTermGOTermProperties;

import static junit.framework.TestCase.assertTrue;

public class HypergeometricTest {


  @Test
  public void testHypergeometric() {
    Hypergeometric hyperg = new Hypergeometric();
    TermForTermGOTermProperties myP = new TermForTermGOTermProperties();
    int pop=324;
    int popAnnot=82;
    int study=74;
    int studyAnnot=38;
    myP.p = hyperg.phypergeometric(pop, (double)popAnnot / (double)pop,
      study, studyAnnot);
    System.err.println(myP.p);
    assertTrue(myP.p < 1.0); // TODO develop some test cases, document.
  }


}

