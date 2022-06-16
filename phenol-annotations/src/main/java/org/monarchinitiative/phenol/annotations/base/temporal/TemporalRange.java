package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * {@link TemporalRange} is a pair of {@link TemporalPoint}s {@link #start()} and {@link #end()} where
 * {@link #start()} starts at or before {@link #end()}.
 */
public interface TemporalRange {

  static TemporalRange of(TemporalPoint start, TemporalPoint end) {
    int result = TemporalPoint.compare(Objects.requireNonNull(start), Objects.requireNonNull(end));
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days) must not be after end (%d days)",
        start.days(), end.days()));

    return TemporalRangeDefault.of(start, end);
  }

  static TemporalRange open() {
    return TemporalRanges.OPEN;
  }

  static TemporalRange openStart(TemporalPoint end) {
    return Objects.requireNonNull(end).isOpen()
      ? TemporalRanges.OPEN
      : new TemporalRanges.TemporalRangeOpenStart(end);
  }

  static TemporalRange openEnd(TemporalPoint start) {
    return Objects.requireNonNull(start).isOpen()
      ? TemporalRanges.OPEN
      : new TemporalRanges.TemporalRangeOpenEnd(start);
  }

  /* **************************************************************************************************************** */

  TemporalPoint start();

  TemporalPoint end();

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
   * Get the number of days spanned by <code>this</code> {@link TemporalRange}.
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

  default TemporalRange intersection(TemporalRange other) {
    TemporalPoint start = TemporalPoint.max(start(), other.start());
    TemporalPoint end = TemporalPoint.min(end(), other.end());
    int compare = TemporalPoint.compare(start, end);
    if (compare > 0)
      return TemporalRanges.BIRTH;

    return TemporalRange.of(start, end);
  }

  default boolean overlapsWith(TemporalRange other) {
    return !intersection(other).isEmpty();
  }

  default boolean contains(TemporalPoint age) {
    int start = TemporalPoint.compare(start(), age);
    int end = TemporalPoint.compare(end(), age);

    return start <= 0 && 0 < end;
  }

  static int compare(TemporalRange x, TemporalRange y) {
    int result = TemporalPoint.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return TemporalPoint.compare(x.end(), y.end());
  }

}
