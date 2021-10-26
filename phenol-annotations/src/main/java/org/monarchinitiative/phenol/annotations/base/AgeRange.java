package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;

/**
 * Interface that represents temporal interval in the life of an organism.
 * <p>
 * <em>Birth</em> is represented by {@link Period#ZERO} and <em>death</em> does not have any specific marker.
 * <p>
 * The smallest resolution unit is the <em>day</em>.
 */
public interface AgeRange {

  /**
   * @param startAge start age (inclusive)
   * @param endAge   end age (exclusive)
   * @return range to represent timespan starting at <code>startAge</code> and ending at <code>endAge</code>
   * @throws IllegalArgumentException if <code>endAge</code> is after <code>startAge</code> or if
   */
  static AgeRange of(Period startAge, Period endAge) {
    if (startAge.getYears() == endAge.getYears()
      && startAge.getMonths() == endAge.getMonths()
      && startAge.getDays() == endAge.getDays())
      return new AgePrecise(startAge);

    if (startAge.getYears() < endAge.getYears()
      || startAge.getMonths() < endAge.getMonths()
      || startAge.getDays() < endAge.getDays()) {
      AgePrecise start = new AgePrecise(startAge);
      AgePrecise end = new AgePrecise(endAge);
      return of(start, end);
    }

    throw new IllegalArgumentException("End age must be after start! Start: " + startAge + ", End: " + endAge);
  }

  static AgeRange of(Age start, Age end) {
    return new AgeRangeDefault(start, end);
  }

  /**
   * @return start age (inclusive)
   */
  Age start();

  /**
   * @return end age (exclusive)
   */
  Age end();

}
