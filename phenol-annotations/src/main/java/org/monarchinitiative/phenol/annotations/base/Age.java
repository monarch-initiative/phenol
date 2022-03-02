package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Comparator;

/**
 * Either precise or imprecise age.
 * <p>
 * <em>Precise</em> age is modeled as a period since birth, using ISO8601 duration.
 * <p>
 * <em>Imprecise</em> age is modeled as a period since birth plus a pair of period bounds where the age can be anywhere between the two.
 */
public interface Age {

  /**
   * Create precise age to represent the provided <code>age</code>.
   */
  static Age of(Period age) {
    return new AgePrecise(age);
  }

  /**
   * Create possibly imprecise age using the provided bounds.
   */
  @Deprecated
  static Age of(Period age, Period lowerBound, Period upperBound) {
    AgeConfidenceInterval confidenceInterval = AgeConfidenceInterval.of(lowerBound, upperBound);
    return of(age, confidenceInterval);
  }

  static Age of(Period age, AgeConfidenceInterval confidenceInterval) {
    if (confidenceInterval.isPrecise()) {
      return new AgePrecise(age);
    } else {
      return new AgeImprecise(age, confidenceInterval);
    }
  }

  Period age();

  AgeConfidenceInterval confidenceInterval();

  default boolean isPrecise() {
    return confidenceInterval().isPrecise();
  }

  static Comparator<Age> naturalOrder() {
    return Comparators.AgeNaturalOrderComparator.INSTANCE;
  }

  static int compare(Age x, Age y) {
    return Comparators.PeriodNaturalOrderComparator.INSTANCE.compare(x.age(), y.age());
  }
}
