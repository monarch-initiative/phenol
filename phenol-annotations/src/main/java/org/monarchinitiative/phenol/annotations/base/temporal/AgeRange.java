package org.monarchinitiative.phenol.annotations.base.temporal;

public interface AgeRange extends TemporalRange {

  static AgeRange of(Age start, Age end) {
    int result = Age.compare(start, end);
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days) must not be after end (%d days)",
        start.days(), end.days()));

    return AgeRangeDefault.of(start, end);
  }

  /**
   * @return interval spanning the temporal domain starting at negative infinity and ending in <code>end</code>.
   */
  static AgeRange openStart(Age end) {
    return AgeRangeDefault.of(Age.openStart(), end);
  }

  /**
   * @return interval spanning the temporal domain starting at <code>start</code> and ending in positive infinity.
   */
  static AgeRange openEnd(Age start) {
    return AgeRangeDefault.of(start, Age.openEnd());
  }

  /**
   * @return interval spanning the entire temporal domain.
   */
  static AgeRange open() {
    return AgeRange.of(Age.openStart(), Age.openEnd());
  }

  /* **************************************************************************************************************** */

  Age start();

  Age end();

  /* **************************************************************************************************************** */

  /**
   * @return <code>true</code> if both {@link AgeRange#start()} and {@link AgeRange#end()} are precise.
   */
  default boolean isPrecise() {
    return start().isPrecise() && end().isPrecise();
  }

  /**
   * @return <code>true</code> if any of {@link AgeRange#start()} and {@link AgeRange#end()} is imprecise.
   */
  default boolean isImprecise() {
    return !isPrecise();
  }

  static int compare(AgeRange x, AgeRange y) {
    int result = Age.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return Age.compare(x.end(), y.end());
  }

}
