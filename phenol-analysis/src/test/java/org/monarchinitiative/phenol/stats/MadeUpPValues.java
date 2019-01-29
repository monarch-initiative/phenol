package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;


/**
 * This is a set of p-values taken from the Benjamini Hochberg paper that we will use to test the MTC classes.
 */

class MadeUpPValues implements IPValueCalculation {
  public static final TermId A = TermId.of("FAKE:1");
  public static final TermId B = TermId.of("FAKE:2");
  public static final TermId C = TermId.of("FAKE:3");
  public static final TermId D = TermId.of("FAKE:4");
  public static final TermId E = TermId.of("FAKE:5");
  public static final TermId F = TermId.of("FAKE:6");
  public static final TermId G = TermId.of("FAKE:7");
  public static final TermId H = TermId.of("FAKE:8");
  public static final TermId I = TermId.of("FAKE:9");
  public static final TermId J = TermId.of("FAKE:10");
  public static final TermId K = TermId.of("FAKE:11");
  public static final TermId L = TermId.of("FAKE:12");
  public static final TermId M = TermId.of("FAKE:13");
  public static final TermId N = TermId.of("FAKE:14");
  public static final TermId O = TermId.of("FAKE:15");

  private Map<TermId,PValue> pvalmap;

  public MadeUpPValues() {
    pvalmap=new HashMap<>();
    pvalmap.put(A,new PValue(0.0001));
    pvalmap.put(B,new PValue(0.0004));
    pvalmap.put(C,new PValue(0.0019));
    pvalmap.put(D,new PValue(0.0095));
    pvalmap.put(E,new PValue(0.0201));
    pvalmap.put(F,new PValue(0.0278));
    pvalmap.put(G,new PValue(0.0298));
    pvalmap.put(H,new PValue(0.0344));
    pvalmap.put(I,new PValue(0.0459));
    pvalmap.put(J,new PValue(0.3240));
    pvalmap.put(K,new PValue(0.4262));
    pvalmap.put(L,new PValue(0.5719));
    pvalmap.put(M,new PValue(0.6528));
    pvalmap.put(N,new PValue(0.7590));
    pvalmap.put(O,new PValue(1.000));
  }


  public Map<TermId, PValue> calculatePValues() {
    return pvalmap;
  }

}
