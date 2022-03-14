package org.monarchinitiative.phenol.annotations.base.temporal;

public interface TemporalInterval {

  static TemporalInterval zero() {
    return TemporalIntervalDefault.of(Timestamp.zero(), Timestamp.zero());
  }

  static TemporalInterval of(Timestamp start, Timestamp end) {
    int result = Timestamp.compare(start, end);
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days, %d seconds) must not be after end (%d days, %d seconds)",
        start.days(), start.seconds(), end.days(), end.seconds()));

    if (start.equals(Timestamp.zero()) && end.equals(Timestamp.zero()))
      return zero();

    return TemporalIntervalDefault.of(start, end);
  }

  /**
   * @return interval spanning the temporal domain starting at negative infinity and ending in <code>end</code>.
   */
  static TemporalInterval openStart(Timestamp end) {
    return TemporalIntervalDefault.of(Timestamp.openStart(), end);
  }

  /**
   * @return interval spanning the temporal domain starting at <code>start</code> and ending in positive infinity.
   */
  static TemporalInterval openEnd(Timestamp start) {
    return TemporalIntervalDefault.of(start, Timestamp.openEnd());
  }

  /**
   * @return interval spanning the entire temporal domain.
   */
  static TemporalInterval open() {
    return TemporalInterval.of(Timestamp.openStart(), Timestamp.openEnd());
  }

  Timestamp start();

  Timestamp end();

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
   * @return length represented as {@link TemporalInterval} that starts on {@link Timestamp#zero()}. The length is a
   * {@link TemporalInterval} with open end if start or end of <code>this</code> {@link TemporalInterval} is open.   *
   */
  default TemporalInterval length() {
    if (isFullyClosed()) {
      Timestamp start = start();
      Timestamp end = end();
      int secondsDifference = end.seconds() - start.seconds();
      int daysDifference = end.days() - start.days();
      if (secondsDifference < 0) {
        secondsDifference = Timestamp.SECONDS_IN_DAY + secondsDifference;
        --daysDifference;
      }

      return TemporalInterval.of(Timestamp.zero(), Timestamp.of(daysDifference, secondsDifference));
    } else {
      return TemporalInterval.openEnd(Timestamp.zero());
    }
  }

  default boolean isEmpty() {
    TemporalInterval length = length();
    return length.end().isZero();
  }

  default TemporalInterval intersection(TemporalInterval other) {
    Timestamp start = Timestamp.max(start(), other.start());
    Timestamp end = Timestamp.min(end(), other.end());
    int compare = Timestamp.compare(start, end);
    if (compare > 0)
      return zero();

    return TemporalInterval.of(start, end);
  }

  default boolean overlapsWith(TemporalInterval other) {
    return !intersection(other).isEmpty();
  }

  default boolean contains(Timestamp timestamp) {
    int start = Timestamp.compare(start(), timestamp);
    int end = Timestamp.compare(end(), timestamp);

    return start <= 0 && 0 < end;
  }

  /* **************************************************************************************************************** */

  static int compare(TemporalInterval x, TemporalInterval y) {
    int result = Timestamp.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return Timestamp.compare(x.end(), y.end());
  }



  /* **************************************************************************************************************** */

}
