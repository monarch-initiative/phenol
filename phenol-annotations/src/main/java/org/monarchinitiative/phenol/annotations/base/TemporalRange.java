package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;

/**
 * Interface that represents temporal interval in the life of an organism.
 * <p>
 * <em>Birth</em> is represented by {@link Period#ZERO} and <em>death</em> does not have any specific marker.
 * <p>
 * The smallest resolution unit is the <em>day</em>.
 */
public interface TemporalRange {

  /**
   * @return start age (inclusive)
   */
  Age start();

  /**
   * @return end age (exclusive)
   */
  Age end();

  /**
   * @param startAge start age (inclusive)
   * @param endAge   end age (exclusive)
   * @return range to represent timespan starting at <code>startAge</code> and ending at <code>endAge</code>
   * @throws IllegalArgumentException if <code>endAge</code> is after <code>startAge</code> or if
   */
  static TemporalRange of(Period startAge, Period endAge) {
    return of(Age.of(startAge), Age.of(endAge));

  }

  static TemporalRange of(Age start, Age end) {
    int result = Age.compare(start, end);
    if (result > 0) {
      throw new IllegalArgumentException("End age must be after start! Start: " + start + ", End: " + end);
    }
    return new TemporalRangeDefault(start, end);
  }

  static int compare(TemporalRange x, TemporalRange y) {
    int result = Age.compare(x.start(), y.start());
    if (result != 0) return result;

    return Age.compare(x.end(), y.end());
  }

}
