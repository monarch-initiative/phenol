package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;



/**
 * This is a set of p-values taken from the Benjamini Hochberg paper that we will use to test the MTC classes.
 */

class MadeUpPValues {
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


  private List<Item2PValue<TermId>> pvallist;

  public MadeUpPValues() {
    pvallist=new ArrayList<>();
    pvallist.add(new Item2PValue<>(A,0.0001));
    pvallist.add(new Item2PValue<>(B,0.0004));
    pvallist.add(new Item2PValue<>(C,0.0019));
    pvallist.add(new Item2PValue<>(D,0.0095));
    pvallist.add(new Item2PValue<>(E,0.0201));
    pvallist.add(new Item2PValue<>(F,0.0278));
    pvallist.add(new Item2PValue<>(G,0.0298));
    pvallist.add(new Item2PValue<>(H,0.0344));
    pvallist.add(new Item2PValue<>(I,0.0459));
    pvallist.add(new Item2PValue<>(J,0.3240));
    pvallist.add(new Item2PValue<>(K,0.4262));
    pvallist.add(new Item2PValue<>(L,0.5719));
    pvallist.add(new Item2PValue<>(M,0.6528));
    pvallist.add(new Item2PValue<>(N,0.7590));
    pvallist.add(new Item2PValue<>(O,1.000));
  }


  public List<Item2PValue<TermId>> getRawPValues() {
    return pvallist;
  }

}
