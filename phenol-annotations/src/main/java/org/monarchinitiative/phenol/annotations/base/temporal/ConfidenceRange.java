package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * Class to represent confidence of an {@link Age}.
 * <p>
 * Note, {@link ConfidenceRange} is a loose concept that does <em>not</em> correspond to confidence interval as used
 * in statistics.
 */
public class ConfidenceRange implements Comparable<ConfidenceRange> {

  private static final ConfidenceRange PRECISE = new ConfidenceRange(0, 0);

  private final int lowerBound;
  private final int upperBound;

  public static ConfidenceRange precise() {
    return PRECISE;
  }

  /**
   * Create a {@link ConfidenceRange} from the provided <em>lower</em> and <em>upper</em> bounds.
   *
   * @param lowerBound non-positive lower bound.
   * @param upperBound non-negative upper bound.
   * @return the {@link ConfidenceRange}.
   */
  public static ConfidenceRange of(int lowerBound, int upperBound) {
    if (lowerBound == 0 && upperBound == 0)
      return PRECISE;
    return new ConfidenceRange(lowerBound, upperBound);
  }

  private ConfidenceRange(int lowerBound, int upperBound) {
    if (lowerBound > 0 || upperBound < 0)
      throw new IllegalArgumentException("'" + lowerBound + ", " + upperBound + "' ConfidenceRange must have non-positive lowerBound and non-negative upperBound!");

    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }


  public int lowerBound() {
    return lowerBound;
  }

  public int upperBound() {
    return upperBound;
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
   * Shorter {@link ConfidenceRange} is better.
   *
   * @param o {@link ConfidenceRange} to compare with
   * @return comparison result as specified in {@link Comparable}
   */
  @Override
  public int compareTo(ConfidenceRange o) {
    return compare(this, o);
  }

  public static int compare(ConfidenceRange x, ConfidenceRange y) {
    return Integer.compare(y.length(), x.length()); // !
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfidenceRange that = (ConfidenceRange) o;
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
