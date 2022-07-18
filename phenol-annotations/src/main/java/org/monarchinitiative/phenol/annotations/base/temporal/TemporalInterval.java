package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * {@link TemporalInterval} is a pair of {@link PointInTime}s {@link #start()} and {@link #end()} where
 * {@link #start()} starts at or before {@link #end()}.
 */
public interface TemporalInterval {

  /**
   * Create a {@link TemporalInterval} using <code>start</code> and <code>end</code>.
   * The <code>start</code> must <em>not</em> be after end.
   *
   * @param start non-null {@code start}
   * @param end non-null {@code end}
   * @throws IllegalArgumentException if {@code start} is after {@code end}
   * @return a {@link TemporalInterval}
   */
  static TemporalInterval of(PointInTime start, PointInTime end) {
    int result = PointInTime.compare(Objects.requireNonNull(start), Objects.requireNonNull(end));
    if (result > 0)
      throw new IllegalArgumentException(String.format("Start (%d days) must not be after end (%d days)",
        start.days(), end.days()));

    return TemporalIntervals.of(start, end);
  }

  /**
   * @return an <em>open</em> {@link TemporalInterval}; an instance where both {@link #isStartOpen()}
   * {@link #isEndOpen()} is <code>true</code>.
   */
  static TemporalInterval open() {
    return TemporalIntervals.OPEN;
  }

  /**
   * Create a {@link TemporalInterval} with an open start.
   *
   * @param end {@link PointInTime} to be used as an end. The {@code end} can be open.
   * @return a {@link TemporalInterval} with an open start if {@code end} is closed, or an open {@link TemporalInterval}
   * if {@code end} is open.
   */
  static TemporalInterval openStart(PointInTime end) {
    return Objects.requireNonNull(end).isOpen()
      ? TemporalIntervals.OPEN
      : new TemporalIntervals.TemporalIntervalWithOpenStart(end);
  }

  /**
   * Create a {@link TemporalInterval} with an open end.
   *
   * @param start {@link PointInTime} to be used as a start. The {@code start} can be open.
   * @return a {@link TemporalInterval} with an open end if {@code start} is closed, or an open {@link TemporalInterval}
   * if {@code start} is open.
   */
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
    // TODO - calculate without allocating
    return !intersection(other).isEmpty();
  }

  default boolean contains(PointInTime age) {
    int start = PointInTime.compare(start(), age);
    int end = PointInTime.compare(end(), age);

    return start <= 0 && 0 < end;
  }

  /**
   * A comparator-like function for default sorting of {@link TemporalInterval} instances.
   * <p>
   * In the comparison, the {@link #start()} is compared first.
   * In case of a tie, instances are compared based on {@link #end()}.
   */
  static int compare(TemporalInterval x, TemporalInterval y) {
    int result = PointInTime.compare(x.start(), y.start());
    if (result != 0)
      return result;

    return PointInTime.compare(x.end(), y.end());
  }

  /**
   * Get {@link OverlapStatus} for {@link TemporalInterval}s {@code x} and {@code y}.
   * <p>
   * Note: the method returns {@link OverlapStatus#CONTAINED_IN} if {@code x} and {@code  y} are equal.
   */
  static OverlapStatus overlapStatus(TemporalInterval x, TemporalInterval y) {
    if (x.end().isAtOrBefore(y.start())) {
      return x.isEmpty()
        ? OverlapStatus.CONTAINED_IN
        : OverlapStatus.UPSTREAM;
    } else if (x.start().isAtOrAfter(y.end())) {
      return x.isEmpty()
        ? OverlapStatus.CONTAINED_IN
        : OverlapStatus.DOWNSTREAM;
    }
    else {
      // We have some sort of overlap here.
      int startCompare = PointInTime.compare(x.start(), y.start());
      if (startCompare < 0) {
        // The block handles `x.start().isBefore(y.start())`.
        if (x.end().isBefore(y.end()))
          return OverlapStatus.OVERLAPS_UPSTREAM;
        else
          return OverlapStatus.CONTAINS;
      } else { // The block handles `x.start().isAtOrAfter(y.start())`.
        if (x.end().isAtOrBefore(y.end())) {
          return OverlapStatus.CONTAINED_IN;
        } else return startCompare == 0 // This is true if `x.start().isAt(y.start())`.
          ? OverlapStatus.CONTAINS
          : OverlapStatus.OVERLAPS_DOWNSTREAM;
      }
    }
  }

}
