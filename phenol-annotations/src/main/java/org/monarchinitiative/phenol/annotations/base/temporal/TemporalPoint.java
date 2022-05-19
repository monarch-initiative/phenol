package org.monarchinitiative.phenol.annotations.base.temporal;


/**
 * {@link TemporalPoint} is an instant on a timeline.
 * <p>
 * The {@link TemporalPoint} can be <em>open</em> ({@link #isOpen()})
 * or <em>closed</em> ({@link #isClosed()}). There are only two <em>open</em> {@link TemporalPoint}s to represent instants
 * that happened in unspecified future or past: {@link TemporalPoint#openStart()} and {@link TemporalPoint#openEnd()}.
 * <p>
 * Internally, the duration is stored as a non-negative number of days passed since the start of the time-line.
 */
public interface TemporalPoint extends TimelineAware {

  /**
   * @return {@link TemporalPoint} representing the last menstrual period before the conception.
   */
  static TemporalPoint lastMenstrualPeriod() {
    return TemporalPoints.LMP;
  }

  /**
   * @return {@link TemporalPoint} representing birth.
   */
  static TemporalPoint birth() {
    return TemporalPoints.BIRTH;
  }


  static TemporalPoint of(int days, boolean isGestational) {
    return isGestational
      ? TemporalPoints.gestational(days)
      : TemporalPoints.postnatal(days);
  }

  /**
   * @return {@link TemporalPoint} to represent an instant that occurred in unspecified distant past.
   */
  static TemporalPoint openStart() {
    return TemporalPoints.OPEN_START;
  }

  /**
   * @return {@link TemporalPoint} to represent an instant that occurs in unspecified distant future.
   */
  static TemporalPoint openEnd() {
    return TemporalPoints.OPEN_END;
  }

  /* **************************************************************************************************************** */

  /**
   * Get the number of days since start of the timeline. This is the number of days since {@link TemporalPoint#lastMenstrualPeriod()}
   * for gestational timeline and since {@link TemporalPoint#birth()} for postnatal timeline.
   * <p>
   * The number is <em>non-negative</em>.
   *
   * @return the number of days.
   */
  int days();

  boolean isOpen();

  /* **************************************************************************************************************** */

  default boolean isClosed() {
    return !isOpen();
  }

  default boolean isZero() {
    return days() == 0;
  }

  default boolean isPositive() {
    return days() > 0;
  }

  static TemporalPoint max(TemporalPoint a, TemporalPoint b) {
    int compare = TemporalPoint.compare(a, b);
    return compare >= 0 ? a : b;
  }

  static TemporalPoint min(TemporalPoint a, TemporalPoint b) {
    int compare = TemporalPoint.compare(a, b);
    return compare <= 0 ? a : b;
  }

  static int compare(TemporalPoint x, TemporalPoint y) {
    if (x.isGestational() ^ y.isGestational())
      return x.isGestational() ? -1 : 1;

    return Integer.compare(x.days(), y.days());
  }

}
