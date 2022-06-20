package org.monarchinitiative.phenol.annotations.base.temporal;


/**
 * {@link TemporalPoint} is an instant on a timeline with the lowest resolution in days.
 * <p>
 * {@link TemporalPoint} is located either on the <em>gestational</em> timeline ({@link #isGestational()})
 * or on the <em>postnatal</em> timeline ({@link #isPostnatal()}).
 * <p>
 * {@link TemporalPoint} can be <em>open</em> ({@link #isOpen()}) or <em>closed</em> ({@link #isClosed()}).
 * There are only two <em>open</em> {@link TemporalPoint}s to represent instants
 * that happened in unspecified future or past: {@link #openStart()} and {@link #openEnd()}.
 * <p>
 * Use {@link #compare(TemporalPoint, TemporalPoint)} for default sorting of the {@link TemporalPoint}s. The sorting
 * uses the following invariants:
 * <ul>
 *   <li>gestational {@link TemporalPoint}s are <em>before</em> (less) than postnatal {@link TemporalPoint}s, and</li>
 *   <li>{@link #openStart()} is <em>before</em> (less) and {@link #openEnd()} is <em>after</em> (more)
 *   than any gestational or postnatal {@link TemporalPoint}.</li>
 * </ul>
 * <p>
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


  /**
   * Get the number of weeks present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link TemporalPoint}. Each week has 7 days. The number of weeks is non-negative.
   *
   * @return the number of weeks.
   */
  default double weeks() {
    return days() / 7.;
  }

  /**
   * Get the number of months present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link TemporalPoint}. A month is one twelfth of a Julian year and Julian year consists
   * of <code>365.25</code> days.
   * <p>
   * The number of months is non-negative.
   *
   * @return the number of months.
   */
  default double months() {
    return days() / Age.DAYS_IN_MONTH;
  }

  /**
   * Get the number of Julian years present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link TemporalPoint}. Julian year consists of <code>365.25</code> days.
   * <p>
   * The number of years is non-negative.
   *
   * @return the number of years.
   */
  default double years() {
    return days() / Age.DAYS_IN_JULIAN_YEAR;
  }

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
