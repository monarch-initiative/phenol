package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * {@link Age} is a {@link TemporalPoint} with associated {@link ConfidenceInterval}. The {@link Age}
 * can be <em>precise</em> or <em>imprecise</em> based on the associated {@link ConfidenceInterval}.
 * The {@link ConfidenceInterval} also allows to interpret the {@link Age} as a {@link TemporalRange}.
 * <p>
 * As is the case of the {@link TemporalPoint}, {@link Age} has a day precision.
 */
public interface Age extends TemporalPoint, TemporalRange {

  /**
   * The number of days in Julian year (<code>365.25</code>).
   */
  float DAYS_IN_JULIAN_YEAR = 365.25f;

  /**
   * The average number of days in one month corresponding to one twelfth of {@link #DAYS_IN_JULIAN_YEAR}:
   * (<code>365.25 / 12 = 30.4166667</code>).
   */
  float DAYS_IN_MONTH = DAYS_IN_JULIAN_YEAR / 12;

  /**
   * Create a precise gestational {@link Age} representing the number of weeks and months
   * since the {@link TemporalPoint#lastMenstrualPeriod()}.
   *
   * @param weeks a non-negative number of completed weeks since the {@link TemporalPoint#lastMenstrualPeriod()}.
   * @param days a non-negative number of additional days to the number of completed weeks.
   * @return precise gestational age.
   */
  static Age gestational(int weeks, int days) {
    return gestational(weeks, days, ConfidenceInterval.precise());
  }

  /**
   * Create a possibly imprecise gestational {@link Age} representing the number of weeks and months
   * since the {@link TemporalPoint#lastMenstrualPeriod()}.
   *
   * @param weeks a non-negative number of completed weeks since the {@link TemporalPoint#lastMenstrualPeriod()}.
   * @param days a non-negative number of additional days to the number of completed weeks.
   * @param ci confidence interval determining if the age is precise or imprecise.
   * @return possibly imprecise gestational age.
   * @throws IllegalArgumentException if <code>weeks</code> or <code>days</code> is negative.
   */
  static Age gestational(int weeks, int days, ConfidenceInterval ci) throws IllegalArgumentException {
    if (weeks < 0 || days < 0)
      throw new IllegalArgumentException("'" + weeks + ", " + days + "' Weeks and days must be non-negative!");
    days += weeks * 7;
    return of(days, true, ci);
  }

  /**
   * Create a precise postnatal {@link Age} representing the number of years, months, and days
   * since {@link TemporalPoint#birth()}.
   *
   * @param years a non-negative number of years.
   * @param months a non-negative number of months.
   * @param days a non-negative number of days.
   * @return precise postnatal {@link Age}.
   * @throws IllegalArgumentException if <code>years</code>, <code>months</code>, or <code>days</code> is negative.
   */
  static Age postnatal(int years, int months, int days) {
    return postnatal(years, months, days, ConfidenceInterval.precise());
  }

  /**
   * Create a possibly imprecise postnatal {@link Age} representing the number of years, months, and days
   * since {@link TemporalPoint#birth()}.
   *
   * @param years a non-negative number of years.
   * @param months a non-negative number of months.
   * @param days a non-negative number of days.
   * @param ci confidence interval determining if the age is precise or imprecise.
   * @return possibly imprecise postnatal {@link Age}.
   * @throws IllegalArgumentException if <code>years</code>, <code>months</code>, or <code>days</code> is negative.
   */
  static Age postnatal(int years, int months, int days, ConfidenceInterval ci) throws IllegalArgumentException {
    if (years < 0 || months < 0 || days < 0)
      throw new IllegalArgumentException("'" + years + ", " + months + ", " + days + "' Years, months and days must be non-negative!");
    return postnatal(days + convertYearsAndMonthsToDays(years, months), ci);
  }

  /**
   * The preferred way for converting <code>years</code> and <code>months</code> to days.
   * <p>
   * The result is a sum of <code>years</code> multiplied by {@link #DAYS_IN_JULIAN_YEAR}
   * and <code>months</code> multiplied by {@link #DAYS_IN_MONTH}, <b>rounded up</b> to the nearest day.
   *
   * @return number of days that occur in given <code>years</code> and <code>months</code>
   */
  private static int convertYearsAndMonthsToDays(int years, int months) {
    /*
     * The result is rounded up at the cost of introducing a small imprecision to ensure that
     * the following assertions are valid:
     * assertThat(Age.postnatal(0, 1, 0).completeMonths(), equalTo(1));
     * assertThat(Age.postnatal(1, 0, 0).completeYears(), equalTo(1));
     */
    return Math.toIntExact((long) Math.ceil(years * DAYS_IN_JULIAN_YEAR + months * DAYS_IN_MONTH));
  }

  /**
   * Create a precise {@link Age} representing a number of days since birth.
   *
   * @param days number of days.
   * @return precise postnatal {@link Age}.
   */
  static Age postnatal(int days) {
    return of(days, false, ConfidenceInterval.precise());
  }

  /**
   * Create a possibly imprecise {@link Age} representing a number of days since birth.
   *
   * @param days number of days.
   * @param ci confidence interval determining if the age is precise or imprecise.
   * @return possibly imprecise postnatal {@link Age}.
   */
  static Age postnatal(int days, ConfidenceInterval ci) {
    return of(days, false, ci);
  }

  /**
   * Create {@link Age} representing given number of days and seconds.
   *
   * @param days    number of days.
   * @param isGestational true if the age should be gestational, false if the age should be postnatal.
   * @param ci confidence interval determining if the age is precise or imprecise.
   * @return possibly imprecise {@link Age}.
   * @throws IllegalArgumentException if <code>days</code> is negative or if the number of days ends up being more
   * than {@link #MAX_DAYS} after the normalization.
   */
  static Age of(int days, boolean isGestational, ConfidenceInterval ci) throws IllegalArgumentException {
    Util.checkDays(days);

    if (Objects.requireNonNull(ci).isImprecise())
      ci = clipConfidenceInterval(ci, days);

    return isGestational
      ? Ages.gestational(days, ci)
      : Ages.postnatal(days, ci);
  }

  /**
   * @return {@link Age} that represents event that occurred in unspecified distant past.
   */
  static Age openStart() {
    return Ages.START;
  }

  /**
   * @return {@link Age} that represents event that occurs in unspecified distant future.
   */
  static Age openEnd() {
    return Ages.END;
  }

  /* **************************************************************************************************************** */

  /**
   * @return the {@link ConfidenceInterval} associated with the {@link Age}.
   */
  ConfidenceInterval confidenceInterval();

  /* **************************************************************************************************************** */

  /**
   * @return <code>true</code> if the associated {@link ConfidenceInterval} is precise.
   */
  default boolean isPrecise() {
    return confidenceInterval().isPrecise();
  }

  /**
   * @return <code>true</code> if the associated {@link ConfidenceInterval} is <em>NOT</em> precise.
   */
  default boolean isImprecise() {
    return !isPrecise();
  }

  /**
   * @return the day corresponding to the <em>lower</em> bound of the {@link Age} when considering
   * the associated {@link ConfidenceInterval}.
   */
  default int lowerBound() {
    int days = days();
    return isPrecise()
      ? days
      : days + confidenceInterval().lowerBound();
  }

  /**
   * @return the day corresponding to the <em>upper</em> bound of the {@link Age} when considering
   * the associated {@link ConfidenceInterval}.
   */
  default int upperBound() {
    int days = days();
    return isPrecise()
      ? days
      : days + confidenceInterval().upperBound();
  }

  /**
   * Add <code>other</code> {@link Age} to <code>this</code>. Note, the {@link ConfidenceInterval} associated with
   * the <code>other</code> {@link Age} is not taken into account.
   *
   * @param other gestational or postnatal {@link Age}.
   * @return new {@link Age} representing the total number of days of <code>this</code> and <code>other</code>
   * with <code>this</code>'s {@link ConfidenceInterval}.
   */
  default Age plus(Age other) {
    int days = days() + other.days();
    return Age.of(days, isGestational(), confidenceInterval());
  }

  static Age max(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare >= 0 ? a : b;
  }

  static Age min(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare <= 0 ? a : b;
  }

  static int compare(Age x, Age y) {
    int result = TemporalPoint.compare(x, y);
    if (result != 0)
      return result;

    return ConfidenceInterval.compare(x.confidenceInterval(), y.confidenceInterval());
  }

  /**
   * Ensure the confidence interval does not extend beyond {@link TemporalPoint#lastMenstrualPeriod()} for gestational {@link Age}
   * and beyond {@link TemporalPoint#birth()} for postnatal {@link Age}.
   * <p>
   * The clipping sets the lower bound to include at most {@link TemporalPoint#lastMenstrualPeriod()} or {@link TemporalPoint#birth()}.
   *
   * @param ci confidence interval to clip.
   * @param days number of days either since {@link TemporalPoint#lastMenstrualPeriod()} or {@link TemporalPoint#birth()}.
   * @return clipped {@link ConfidenceInterval} or the <code>ci</code> instance if clipping was not necessary.
   */
  private static ConfidenceInterval clipConfidenceInterval(ConfidenceInterval ci, int days) {
    if (ci.lowerBound() < -days)
      return ConfidenceInterval.of(-days, ci.upperBound());
    return ci;
  }
}
