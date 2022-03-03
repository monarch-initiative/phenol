package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Objects;

@Deprecated
public class AgeConfidenceInterval implements Comparable<AgeConfidenceInterval> {

  private static final AgeConfidenceInterval PRECISE = new AgeConfidenceInterval(Period.ZERO, Period.ZERO);

  public static AgeConfidenceInterval of(Period lowerBound, Period upperBound) {
    Objects.requireNonNull(lowerBound, "Lower bound must not be null");
    Objects.requireNonNull(upperBound, "Upper bound must not be null");

    if (lowerBound.isZero() && upperBound.isZero()) {
      return PRECISE;
    } else {
      if ((!lowerBound.isNegative() && !lowerBound.isZero())
        || (upperBound.isNegative() && !upperBound.isZero()))
        throw new IllegalArgumentException("The lower bound " + lowerBound + " must be negative and the upper bound " + upperBound + " must be positive!");
      return new AgeConfidenceInterval(lowerBound, upperBound);
    }
  }

  public static AgeConfidenceInterval precise() {
    return PRECISE;
  }

  private final Period lowerBound;
  private final Period upperBound;

  private AgeConfidenceInterval(Period lowerBound, Period upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  /**
   * @return lower bound (included)
   */
  public Period lowerBound() {
    return lowerBound;
  }

  /**
   * @return upper bound (excluded)
   */
  public Period upperBound() {
    return upperBound;
  }

  public Period length() {
    return upperBound.minus(lowerBound).normalized();
  }

  public boolean isPrecise() {
    return this.equals(PRECISE);
  }

  @Override
  public int compareTo(AgeConfidenceInterval o) {
    return Comparators.PeriodNaturalOrderComparator.INSTANCE.compare(length(), o.length());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgeConfidenceInterval that = (AgeConfidenceInterval) o;
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
