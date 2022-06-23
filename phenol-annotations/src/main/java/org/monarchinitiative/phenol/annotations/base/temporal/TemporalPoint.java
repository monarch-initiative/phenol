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
   * To prevent numerical overflow, creation of a {@link TemporalPoint} corresponding to more than
   * 2,000 Julian years (730,500 days) is not allowed.
   * 2,000 years should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = 20 * 36_525;

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
    Util.checkDays(days);

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
   * Get the number of complete weeks present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link TemporalPoint}. Each week has 7 days. The number of weeks is non-negative.
   * <p>
   * For instance, there are 2 complete weeks in 20 days, 3 complete weeks in 21 days, and so on.
   *
   * @return the number of weeks.
   */
  default int completeWeeks() {
    return days() / 7;
  }

  /**
   * Get the number of complete months present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link TemporalPoint}. A month is defined as one twelfth of a Julian year and Julian year consists
   * of <code>365.25</code> days.
   * <p>
   * The number of months is non-negative.
   * <p>
   * For instance, there are 2 complete weeks in 20 days, 3 complete weeks in 21 days, and so on.
   *
   * @return the number of months.
   */
  default int completeMonths() {
    return (int) (days() / Age.DAYS_IN_MONTH);
  }

  /**
   * Get the number of complete Julian years present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link TemporalPoint}. Julian year consists of <code>365.25</code> days.
   * <p>
   * The number of years is non-negative.
   *
   * @return the number of years.
   */
  default int completeYears() {
    return (int) (days() / Age.DAYS_IN_JULIAN_YEAR);
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
