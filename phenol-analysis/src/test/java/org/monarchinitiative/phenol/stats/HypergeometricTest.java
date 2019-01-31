package org.monarchinitiative.phenol.stats;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.jupiter.api.Assertions.assertTrue;


class HypergeometricTest {


  @Test
  void testHypergeometric() {
    Hypergeometric hyperg = new Hypergeometric();
    TermId fakeId = TermId.of("GO:123");

    int pop=324;
    int popAnnot=82;
    int study=74;
    int studyAnnot=38;
    double raw_pval = hyperg.phypergeometric(pop, (double)popAnnot / (double)pop,
      study, studyAnnot);
    GoTerm2PValAndCounts myP = new GoTerm2PValAndCounts(fakeId,raw_pval,studyAnnot,popAnnot);
    assertTrue(myP.getRawPValue() < 1.0); // TODO develop some test cases, document.
  }


}

