package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Optional;

/**
 * Either precise or imprecise age.
 * <p>
 * <em>Precise</em> age is modeled as a period since birth, using ISO8601 duration.
 * <p>
 * <em>Imprecise</em> age is modeled as a pair of period bounds where the age can be anywhere between the two.
 */
public interface Age {

  static Age precise(Period age) {
    return new AgePrecise(age);
  }

  /**
   * Create imprecise age.
   *
   * @return imprecise age
   * @throws IllegalArgumentException if the lower bound is greater than the upper bound
   */
  static Age imprecise(Period lowerBound, Period upperBound) {
    if (upperBound.minus(lowerBound).isNegative())
      throw new IllegalArgumentException("Lower bound " + lowerBound + " must be before upper bound " + upperBound);
    return new AgeImprecise(lowerBound, upperBound);
  }

  Optional<Period> age();

  /**
   * @return lower bound (included)
   */
  Period lowerBound();

  /**
   * @return upper bound (excluded)
   */
  Period upperBound();

  default boolean isPrecise() {
    return age().isPresent();
  }

}
