package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

public class ConfidenceInterval {

  private static final ConfidenceInterval PRECISE = new ConfidenceInterval(Timestamp.zero(), Timestamp.zero());

  /**
   *
   * @param lowerBound number of days (must be negative)
   * @param upperBound number of days
   * @return confidence interval
   */
  public static ConfidenceInterval of(int lowerBound, int upperBound) {
    return of(Timestamp.of(lowerBound), Timestamp.of(upperBound));
  }

  public static ConfidenceInterval of(Timestamp lowerBound, Timestamp upperBound) {
    Objects.requireNonNull(lowerBound, "Lower bound must not be null");
    Objects.requireNonNull(upperBound, "Upper bound must not be null");

    if (lowerBound.isZero() && upperBound.isZero()) {
      return PRECISE;
    } else {
      if ((!lowerBound.isNegative() && !lowerBound.isZero())
        || (upperBound.isNegative() && !upperBound.isZero()))
        throw new IllegalArgumentException("The lower bound [days=" + lowerBound.days() + ",seconds=" + lowerBound.seconds() + "]" +
          " must not be positive and the upper bound [days=" + upperBound.days() + ",seconds=" + upperBound.seconds() + "] must not be negative!");
      return new ConfidenceInterval(lowerBound, upperBound);
    }
  }

  public static ConfidenceInterval precise() {
    return PRECISE;
  }

  private final Timestamp lowerBound;
  private final Timestamp upperBound;

  private ConfidenceInterval(Timestamp lowerBound, Timestamp upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  /**
   * @return lower bound (included)
   */
  public Timestamp lowerBound() {
    return lowerBound;
  }

  /**
   * @return upper bound (excluded)
   */
  public Timestamp upperBound() {
    return upperBound;
  }

  public Timestamp length() {
    if (isPrecise()) {
      return Timestamp.zero();
    } else {
      // TODO - may blow up
      return Timestamp.of(upperBound.days() - lowerBound.days(), upperBound.seconds() - lowerBound.seconds());
    }
  }

  public boolean isPrecise() {
    return this.equals(PRECISE);
  }

  public static int compare(ConfidenceInterval x, ConfidenceInterval y) {
    return Timestamp.compare(x.length(), y.length());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfidenceInterval that = (ConfidenceInterval) o;
    return Objects.equals(lowerBound, that.lowerBound) && Objects.equals(upperBound, that.upperBound);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    return isPrecise() ? "" : "(-" + lowerBound + ", +" + upperBound + ')';
  }

}
