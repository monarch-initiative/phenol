package org.monarchinitiative.phenol.annotations.base.temporal;


/**
 * {@link PointInTime} is an instant on a timeline with the lowest resolution in days.
 * <p>
 * {@link PointInTime} is located either on the <em>gestational</em> timeline ({@link #isGestational()})
 * or on the <em>postnatal</em> timeline ({@link #isPostnatal()}).
 * <p>
 * {@link PointInTime} can be <em>open</em> ({@link #isOpen()}) or <em>closed</em> ({@link #isClosed()}).
 * The <em>open</em> {@link PointInTime}s represent instants that happened in unspecified past or future.
 * Use {@link #openStart()} to get the {@link PointInTime} for instant in unspecified past,
 * and {@link #openEnd()} to get the {@link PointInTime} for instant in unspecified future.
 * Note, this has nothing in common with a concept of including/excluding range endpoints.
 * <p>
 * Use {@link #compare(PointInTime, PointInTime)} for default sorting of the {@link PointInTime}s. The sorting
 * uses the following invariants:
 * <ul>
 *   <li>gestational {@link PointInTime}s are <em>before</em> (less) than postnatal {@link PointInTime}s, and</li>
 *   <li>{@link #openStart()} is <em>before</em> (less) and {@link #openEnd()} is <em>after</em> (more)
 *   than any gestational or postnatal {@link PointInTime}.</li>
 * </ul>
 * <p>
 */
public interface PointInTime extends TimelineAware {

  /**
   * To prevent numerical overflow, creation of a {@link PointInTime} corresponding to more than
   * 2,000 Julian years (730,500 days) is not allowed.
   * 2,000 years should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_YEARS = 2_000;

  /**
   * To prevent numerical overflow, creation of a {@link PointInTime} corresponding to more than
   * 2,000 Julian years (730,500 days) is not allowed.
   * 2,000 years should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = MAX_YEARS / 100 * 36_525; // dividing by 100 since 36_525 is 100 * 365.25 (Julian year).

  /**
   * @return {@link PointInTime} representing the last menstrual period before the conception.
   */
  static PointInTime lastMenstrualPeriod() {
    return PointsInTime.LMP;
  }

  /**
   * @return {@link PointInTime} representing birth.
   */
  static PointInTime birth() {
    return PointsInTime.BIRTH;
  }

  /**
   * Create a {@link PointInTime} to represent a specific day on a timeline.
   *
   * @param days a day to represent on the timeline.
   * @param isGestational {@code true} if {@link PointInTime} is on gestational timeline {@code false}.
   * @return a {@link PointInTime} instance.
   */
  static PointInTime of(int days, boolean isGestational) {
    return PointsInTime.of(Util.checkDays(days), isGestational);
  }

  /**
   * @return {@link PointInTime} to represent an instant that occurred in unspecified distant past.
   */
  static PointInTime openStart() {
    return PointsInTime.OPEN_START;
  }

  /**
   * @return {@link PointInTime} to represent an instant that occurs in unspecified distant future.
   */
  static PointInTime openEnd() {
    return PointsInTime.OPEN_END;
  }

  /* **************************************************************************************************************** */

  /**
   * Get the number of days since start of the timeline. This is the number of days since {@link PointInTime#lastMenstrualPeriod()}
   * for gestational timeline and since {@link PointInTime#birth()} for postnatal timeline.
   * <p>
   * The number is <em>non-negative</em>.
   *
   * @return the number of days.
   */
  int days();

  /**
   * @return <code>true</code> if the {@link PointInTime} is <em>open</em>.
   */
  boolean isOpen();

  /* **************************************************************************************************************** */


  /**
   * Get the number of complete weeks present between {@link #lastMenstrualPeriod()} or {@link #birth()}
   * and this {@link PointInTime}. Each week has 7 days. The number of weeks is non-negative.
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
   * and this {@link PointInTime}. A month is defined as one twelfth of a Julian year and Julian year consists
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
   * and this {@link PointInTime}. Julian year consists of <code>365.25</code> days.
   * <p>
   * The number of years is non-negative.
   *
   * @return the number of years.
   */
  default int completeYears() {
    return (int) (days() / Age.DAYS_IN_JULIAN_YEAR);
  }

  /**
   * @return <code>true</code> if the {@link PointInTime} is <em>closed</em>.
   */
  default boolean isClosed() {
    return !isOpen();
  }

  /**
   * @return <code>true</code> if the {@link PointInTime} represents a beginning of a timeline.
   */
  default boolean isZero() {
    return days() == 0;
  }

  /**
   * @return <code>true</code> if the {@link PointInTime} does not represent a beginning of a timeline.
   */
  default boolean isPositive() {
    return days() > 0;
  }

  /**
   * @return {@code true} if {@code this} {@link PointInTime} occurs <em>before</em> the {@code other}.
   */
  default boolean isBefore(PointInTime other) {
    return compare(this, other) < 0;
  }

  /**
   * @return {@code true} if {@code this} {@link PointInTime} occurs <em>at the same time</em> or <em>before</em> the {@code other}.
   */
  default boolean isAtOrBefore(PointInTime other) {
    return compare(this, other) <= 0;
  }

  /**
   * @return {@code true} if {@code this} {@link PointInTime} occurs <em>after</em> the {@code other}.
   */
  default boolean isAfter(PointInTime other) {
    return !isAtOrBefore(other);
  }

  /**
   * @return {@code true} if {@code this} {@link PointInTime} occurs <em>at the same time</em> or <em>after</em> the {@code other}.
   */
  default boolean isAtOrAfter(PointInTime other) {
    return !isBefore( other);
  }

  /**
   * @return {@code true} if {@code this} {@link PointInTime} occurs <em>at the same time</em> as the {@code other}.
   */
  default boolean isAt(PointInTime other) {
    return compare(this, other) == 0;
  }

  /**
   * @return the greater {@link PointInTime} based on comparing {@code a} and {@code b} using {@link #compare(PointInTime, PointInTime)}.
   * {@code a} is returned in case of a tie.
   */
  static PointInTime max(PointInTime a, PointInTime b) {
    int compare = PointInTime.compare(a, b);
    return compare >= 0 ? a : b;
  }

  /**
   * @return the smaller {@link PointInTime} based on comparing {@code a} and {@code b} using {@link #compare(PointInTime, PointInTime)}.
   * {@code a} is returned in case of a tie.
   */
  static PointInTime min(PointInTime a, PointInTime b) {
    int compare = PointInTime.compare(a, b);
    return compare <= 0 ? a : b;
  }

  /**
   * A comparator-like function for default sorting of {@link PointInTime} instances.
   * <p>
   * The sorting uses the following invariants:
   * <ul>
   *   <li>gestational {@link PointInTime}s are <em>before</em> (less) than postnatal {@link PointInTime}s,</li>
   *   <li>a {@link PointInTime} with fewer days is less than a {@link PointInTime} with more days,</li>
   *   <li>{@link #openStart()} is <em>before</em> (less) and {@link #openEnd()} is <em>after</em> (more)
   *   than any closed {@link PointInTime}.</li>
   * </ul>
   */
  static int compare(PointInTime x, PointInTime y) {
    if (x.isGestational() ^ y.isGestational())
      return x.isGestational() ? -1 : 1;

    return Integer.compare(x.days(), y.days());
  }

}
