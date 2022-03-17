package org.monarchinitiative.phenol.annotations.base.temporal;

public interface TemporalInterval {

  static TemporalInterval zero() {
    return TemporalIntervalDefault.of(AgeSinceBirth.zero(), AgeSinceBirth.zero());
  }

  static TemporalInterval of(AgeSinceBirth start, AgeSinceBirth end) {
    int result = AgeSinceBirth.compare(start, end);
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days, %d seconds) must not be after end (%d days, %d seconds)",
        start.days(), start.seconds(), end.days(), end.seconds()));

    if (start.equals(AgeSinceBirth.zero()) && end.equals(AgeSinceBirth.zero()))
      return zero();

    return TemporalIntervalDefault.of(start, end);
  }

  /**
   * @return interval spanning the temporal domain starting at negative infinity and ending in <code>end</code>.
   */
  static TemporalInterval openStart(AgeSinceBirth end) {
    return TemporalIntervalDefault.of(AgeSinceBirth.openStart(), end);
  }

  /**
   * @return interval spanning the temporal domain starting at <code>start</code> and ending in positive infinity.
   */
  static TemporalInterval openEnd(AgeSinceBirth start) {
    return TemporalIntervalDefault.of(start, AgeSinceBirth.openEnd());
  }

  /**
   * @return interval spanning the entire temporal domain.
   */
  static TemporalInterval open() {
    return TemporalInterval.of(AgeSinceBirth.openStart(), AgeSinceBirth.openEnd());
  }

  AgeSinceBirth start();

  AgeSinceBirth end();

  /* **************************************************************************************************************** */

  default boolean isStartOpen() {
    return start().isOpen();
  }

  default boolean isStartClosed() {
    return !isStartOpen();
  }

  default boolean isEndOpen() {
    return end().isOpen();
  }

  default boolean isEndClosed() {
    return !isEndOpen();
  }

  default boolean isFullyOpen() {
    return isStartOpen() && isEndOpen();
  }

  default boolean isFullyClosed() {
    return isStartClosed() && isEndClosed();
  }

  /* **************************************************************************************************************** */

  /**
   * @return length represented as {@link TemporalInterval} that starts on {@link AgeSinceBirth#zero()}. The length is a
   * {@link TemporalInterval} with open end if start or end of <code>this</code> {@link TemporalInterval} is open.   *
   */
  default TemporalInterval length() {
    if (isFullyClosed()) {
      AgeSinceBirth start = start();
      AgeSinceBirth end = end();
      int secondsDifference = end.seconds() - start.seconds();
      int daysDifference = end.days() - start.days();
      if (secondsDifference < 0) {
        secondsDifference = AgeSinceBirth.SECONDS_IN_DAY + secondsDifference;
        --daysDifference;
      }

      return TemporalInterval.of(AgeSinceBirth.zero(), AgeSinceBirth.of(daysDifference, secondsDifference));
    } else {
      return TemporalInterval.openEnd(AgeSinceBirth.zero());
    }
  }

  default boolean isEmpty() {
    TemporalInterval length = length();
    return length.end().isZero();
  }

  default TemporalInterval intersection(TemporalInterval other) {
    AgeSinceBirth start = AgeSinceBirth.max(start(), other.start());
    AgeSinceBirth end = AgeSinceBirth.min(end(), other.end());
    int compare = AgeSinceBirth.compare(start, end);
    if (compare > 0)
      return zero();

    return TemporalInterval.of(start, end);
  }

  default boolean overlapsWith(TemporalInterval other) {
    return !intersection(other).isEmpty();
  }

  default boolean contains(AgeSinceBirth ageSinceBirth) {
    int start = AgeSinceBirth.compare(start(), ageSinceBirth);
    int end = AgeSinceBirth.compare(end(), ageSinceBirth);

    return start <= 0 && 0 < end;
  }

  /* **************************************************************************************************************** */

  static int compare(TemporalInterval x, TemporalInterval y) {
    int result = AgeSinceBirth.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return AgeSinceBirth.compare(x.end(), y.end());
  }



  /* **************************************************************************************************************** */

}
