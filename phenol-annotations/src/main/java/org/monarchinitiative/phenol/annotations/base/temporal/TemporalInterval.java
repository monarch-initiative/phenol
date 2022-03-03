package org.monarchinitiative.phenol.annotations.base.temporal;

public interface TemporalInterval {

  TemporalInterval ZERO = TemporalInterval.of(Timestamp.ZERO, Timestamp.ZERO);

  static TemporalInterval of(Timestamp start, Timestamp end) {
    int result = Timestamp.compare(start, end);
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days, %d seconds) must not be after end (%d days, %d seconds)",
        start.days(), start.seconds(), end.days(), end.seconds()));

    if (start.equals(Timestamp.ZERO) && end.equals(Timestamp.ZERO))
      return ZERO;

    return TemporalIntervalDefault.of(start, end);
  }

  static TemporalInterval openStart(Timestamp end) {
    return TemporalIntervalDefault.of(Timestamp.openStart(), end);
  }

  static TemporalInterval openEnd(Timestamp start) {
    return TemporalIntervalDefault.of(start, Timestamp.openEnd());
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
    return !isFullyOpen();
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
