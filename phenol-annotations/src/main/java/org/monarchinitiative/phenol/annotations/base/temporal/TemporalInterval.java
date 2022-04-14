package org.monarchinitiative.phenol.annotations.base.temporal;

public interface TemporalInterval {

  static TemporalInterval birth() {
    return TemporalIntervalDefault.of(Age.birth(), Age.birth());
  }

  static TemporalInterval of(Age start, Age end) {
    int result = Age.compare(start, end);
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days, %d seconds) must not be after end (%d days, %d seconds)",
        start.days(), start.seconds(), end.days(), end.seconds()));

    return TemporalIntervalDefault.of(start, end);
  }

  /**
   * @return interval spanning the temporal domain starting at negative infinity and ending in <code>end</code>.
   */
  static TemporalInterval openStart(Age end) {
    return TemporalIntervalDefault.of(Age.openStart(), end);
  }

  /**
   * @return interval spanning the temporal domain starting at <code>start</code> and ending in positive infinity.
   */
  static TemporalInterval openEnd(Age start) {
    return TemporalIntervalDefault.of(start, Age.openEnd());
  }

  /**
   * @return interval spanning the entire temporal domain.
   */
  static TemporalInterval open() {
    return TemporalInterval.of(Age.openStart(), Age.openEnd());
  }

  Age start();

  Age end();

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
   * @return length represented as {@link TemporalInterval} that starts either on {@link Age#lastMenstrualPeriod()} or
   * on {@link Age#birth()}.
   * <p>
   * If the start or the end of <code>this</code> {@link TemporalInterval} is open,
   * then the length is {@link TemporalInterval#open()}.
   */
  default TemporalInterval length() {
    if (isFullyClosed()) {
      Age start = start();
      Age end = end();
      int secondsDifference = end.seconds() - start.seconds();
      int daysDifference = end.days() - start.days();
      if (secondsDifference < 0) {
        secondsDifference = Age.SECONDS_IN_DAY + secondsDifference;
        --daysDifference;
      }

      return TemporalInterval.of(Age.birth(), Age.postnatal(daysDifference, secondsDifference));
    } else {
      return TemporalInterval.open();
    }
  }

  default boolean isEmpty() {
    TemporalInterval length = length();
    return length.end().isZero();
  }

  default TemporalInterval intersection(TemporalInterval other) {
    Age start = Age.max(start(), other.start());
    Age end = Age.min(end(), other.end());
    int compare = Age.compare(start, end);
    if (compare > 0)
      return birth();

    return TemporalInterval.of(start, end);
  }

  default boolean overlapsWith(TemporalInterval other) {
    return !intersection(other).isEmpty();
  }

  default boolean contains(Age age) {
    int start = Age.compare(start(), age);
    int end = Age.compare(end(), age);

    return start <= 0 && 0 < end;
  }

  /* **************************************************************************************************************** */

  static int compare(TemporalInterval x, TemporalInterval y) {
    int result = Age.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return Age.compare(x.end(), y.end());
  }



  /* **************************************************************************************************************** */

}
