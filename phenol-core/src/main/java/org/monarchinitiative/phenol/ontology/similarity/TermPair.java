package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;

/**
 * Class to represent a pair of TermId objects that we will use to index Term-pair Resnik similarities.
 * @author Peter Robinson
 */
public class TermPair {

  private final TermId tidA;
  private final TermId tidB;

  private TermPair(TermId a, TermId b) {
    this.tidA = a;
    this.tidB = b;
  }

  public TermId getTidA() {
    return tidA;
  }

  public TermId getTidB() {
    return tidB;
  }

  @Override
  public int hashCode() {
    return Objects.hash(tidA, tidB);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (! (obj instanceof TermPair)) return false;
    TermPair that = (TermPair) obj;
    return this.tidA.equals(that.tidA) && this.tidB.equals(that.tidB);
  }

  public static TermPair symmetric(TermId a, TermId b) {
    if (a.getId().compareTo(b.getId())>0) {
      return new TermPair(a,b);
    } else {
      return new TermPair(b,a);
    }
  }

  public static TermPair asymmetric(TermId a, TermId b) {
    return new TermPair(a,b);
  }

}
