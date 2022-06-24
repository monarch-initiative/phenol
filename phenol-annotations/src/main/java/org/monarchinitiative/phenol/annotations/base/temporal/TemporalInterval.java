package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * {@link TemporalInterval} is a pair of {@link PointInTime}s {@link #start()} and {@link #end()} where
 * {@link #start()} starts at or before {@link #end()}.
 */
public interface TemporalInterval {

  static TemporalInterval of(PointInTime start, PointInTime end) {
    int result = PointInTime.compare(Objects.requireNonNull(start), Objects.requireNonNull(end));
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days) must not be after end (%d days)",
        start.days(), end.days()));

    return TemporalIntervals.of(start, end);
  }

  static TemporalInterval open() {
    return TemporalIntervals.OPEN;
  }

  static TemporalInterval openStart(PointInTime end) {
    return Objects.requireNonNull(end).isOpen()
      ? TemporalIntervals.OPEN
      : new TemporalIntervals.TemporalIntervalWithOpenStart(end);
  }

  static TemporalInterval openEnd(PointInTime start) {
    return Objects.requireNonNull(start).isOpen()
      ? TemporalIntervals.OPEN
      : new TemporalIntervals.TemporalIntervalWithOpenEnd(start);
  }

  /* **************************************************************************************************************** */

  PointInTime start();

  PointInTime end();

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

  /**
   * Get the number of days spanned by <code>this</code> {@link TemporalInterval}.
   * If {@link #isStartOpen()} or {@link #isEndOpen()}, then the length is equal to {@link Integer#MAX_VALUE}.
   *
   * @return the number of days.
   */
  default int length() {
    return isFullyClosed()
      ? end().days() - start().days()
      : Integer.MAX_VALUE;
  }

  /**
   * @return <code>true</code> if {@link #length()} is equal to <code>0</code>.
   */
  default boolean isEmpty() {
    return length() == 0;
  }

  default TemporalInterval intersection(TemporalInterval other) {
    PointInTime start = PointInTime.max(start(), other.start());
    PointInTime end = PointInTime.min(end(), other.end());
    int compare = PointInTime.compare(start, end);
    if (compare > 0)
      return TemporalIntervals.BIRTH;

    return TemporalInterval.of(start, end);
  }

  default boolean overlapsWith(TemporalInterval other) {
    return !intersection(other).isEmpty();
  }

  default boolean contains(PointInTime age) {
    int start = PointInTime.compare(start(), age);
    int end = PointInTime.compare(end(), age);

    return start <= 0 && 0 < end;
  }

  static int compare(TemporalInterval x, TemporalInterval y) {
    int result = PointInTime.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return PointInTime.compare(x.end(), y.end());
  }

}
