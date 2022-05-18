package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

public class ConfidenceInterval implements Comparable<ConfidenceInterval> {

  private static final ConfidenceInterval PRECISE = new ConfidenceInterval(0, 0);

  private final int lowerBound;
  private final int upperBound;

  private ConfidenceInterval(int lowerBound, int upperBound) {
    if (lowerBound > 0 || upperBound < 0)
      throw new IllegalArgumentException("'" + lowerBound + ", " + upperBound + "' ConfidenceInterval must have non-positive lowerBound and non-negative upperBound!");

    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  public static ConfidenceInterval precise() {
    return PRECISE;
  }

  public static ConfidenceInterval of(int lowerBound, int upperBound) {
    if (lowerBound == 0 && upperBound == 0)
      return PRECISE;
    return new ConfidenceInterval(lowerBound, upperBound);
  }


  public int lowerBound() {
    return lowerBound;
  }

  public int upperBound() {
    return upperBound;
  }

  public TemporalInterval asTemporalInterval(Age age) {
    return TemporalInterval.of(
      Age.of(age.days() + lowerBound, age.isGestational(), ConfidenceInterval.precise()),
      Age.of(age.days() + upperBound, age.isGestational(), ConfidenceInterval.precise())
    );
  }

  public boolean isPrecise() {
    return this.equals(PRECISE);
  }

  public boolean isImprecise() {
    return !isPrecise();
  }

  /**
   * @return length of the confidence interval, precise CI has length <code>0</code>.
   */
  public int length() {
    return -lowerBound + upperBound;
  }

  /**
   * Shorter confidence interval is better.
   *
   * @param o confidence interval to compare with
   * @return comparison result as specified in {@link Comparable}
   */
  @Override
  public int compareTo(ConfidenceInterval o) {
    return compare(this, o);
  }

  public static int compare(ConfidenceInterval x, ConfidenceInterval y) {
    return Integer.compare(y.length(), x.length()); // !
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfidenceInterval that = (ConfidenceInterval) o;
    return lowerBound == that.lowerBound && upperBound == that.upperBound;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    return isPrecise() ? "" : "(-" + -lowerBound + ", +" + upperBound + ')';
  }

}
