package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * {@link Age} is a {@link PointInTime} with associated {@link ConfidenceRange}. The {@link Age}
 * can be <em>precise</em> or <em>imprecise</em> based on the associated {@link ConfidenceRange}.
 * The {@link ConfidenceRange} also allows to interpret the {@link Age} as a {@link TemporalInterval}.
 * <p>
 * As is the case of the {@link PointInTime}, {@link Age} has a day precision.
 */
public interface Age extends PointInTime, TemporalInterval {

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
   * since the {@link PointInTime#lastMenstrualPeriod()}.
   *
   * @param weeks a non-negative number of completed weeks since the {@link PointInTime#lastMenstrualPeriod()}.
   * @param days a non-negative number of additional days to the number of completed weeks.
   * @return precise gestational age.
   */
  static Age gestational(int weeks, int days) {
    return gestational(weeks, days, ConfidenceRange.precise());
  }

  /**
   * Create a possibly imprecise gestational {@link Age} representing the number of weeks and months
   * since the {@link PointInTime#lastMenstrualPeriod()}.
   *
   * @param weeks a non-negative number of completed weeks since the {@link PointInTime#lastMenstrualPeriod()}.
   * @param days a non-negative number of additional days to the number of completed weeks.
   * @param cr {@link ConfidenceRange} determining if the {@link Age} is precise or imprecise.
   * @return possibly imprecise gestational age.
   * @throws IllegalArgumentException if <code>weeks</code> or <code>days</code> is negative.
   */
  static Age gestational(int weeks, int days, ConfidenceRange cr) throws IllegalArgumentException {
    if (weeks < 0 || days < 0)
      throw new IllegalArgumentException("'" + weeks + ", " + days + "' Weeks and days must be non-negative!");
    days += weeks * 7;
    return of(days, true, cr);
  }

  /**
   * Create a precise postnatal {@link Age} representing the number of years, months, and days
   * since {@link PointInTime#birth()}.
   *
   * @param years a non-negative number of years.
   * @param months a non-negative number of months.
   * @param days a non-negative number of days.
   * @return precise postnatal {@link Age}.
   * @throws IllegalArgumentException if <code>years</code>, <code>months</code>, or <code>days</code> is negative.
   */
  static Age postnatal(int years, int months, int days) {
    return postnatal(years, months, days, ConfidenceRange.precise());
  }

  /**
   * Create a possibly imprecise postnatal {@link Age} representing the number of years, months, and days
   * since {@link PointInTime#birth()}.
   *
   * @param years a non-negative number of years.
   * @param months a non-negative number of months.
   * @param days a non-negative number of days.
   * @param cr {@link ConfidenceRange} determining if the {@link Age} is precise or imprecise.
   * @return possibly imprecise postnatal {@link Age}.
   * @throws IllegalArgumentException if <code>years</code>, <code>months</code>, or <code>days</code> is negative.
   */
  static Age postnatal(int years, int months, int days, ConfidenceRange cr) throws IllegalArgumentException {
    if (years < 0 || months < 0 || days < 0)
      throw new IllegalArgumentException("'" + years + ", " + months + ", " + days + "' Years, months and days must be non-negative!");
    return postnatal(days + convertYearsAndMonthsToDays(years, months), cr);
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
    return of(days, false, ConfidenceRange.precise());
  }

  /**
   * Create a possibly imprecise {@link Age} representing a number of days since birth.
   *
   * @param days number of days.
   * @param cr {@link ConfidenceRange} determining if the {@link Age} is precise or imprecise.
   * @return possibly imprecise postnatal {@link Age}.
   */
  static Age postnatal(int days, ConfidenceRange cr) {
    return of(days, false, cr);
  }

  /**
   * Create {@link Age} representing given number of days and seconds.
   *
   * @param days    number of days.
   * @param isGestational true if the age should be gestational, false if the age should be postnatal.
   * @param cr {@link ConfidenceRange} determining if the {@link Age} is precise or imprecise.
   * @return possibly imprecise {@link Age}.
   * @throws IllegalArgumentException if <code>days</code> is negative or if the number of days ends up being more
   * than {@link #MAX_DAYS} after the normalization.
   */
  static Age of(int days, boolean isGestational, ConfidenceRange cr) throws IllegalArgumentException {
    Util.checkDays(days);

    if (Objects.requireNonNull(cr).isImprecise())
      cr = clipConfidenceInterval(cr, days);

    return isGestational
      ? Ages.gestational(days, cr)
      : Ages.postnatal(days, cr);
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
   * @return the {@link ConfidenceRange} associated with the {@link Age}.
   */
  ConfidenceRange confidenceRange();

  /* **************************************************************************************************************** */

  /**
   * @return <code>true</code> if the associated {@link ConfidenceRange} is precise.
   */
  default boolean isPrecise() {
    return confidenceRange().isPrecise();
  }

  /**
   * @return <code>true</code> if the associated {@link ConfidenceRange} is <em>NOT</em> precise.
   */
  default boolean isImprecise() {
    return !isPrecise();
  }

  /**
   * @return the day corresponding to the <em>lower</em> bound of the {@link Age} when considering
   * the associated {@link ConfidenceRange}.
   */
  default int lowerBound() {
    int days = days();
    return isPrecise()
      ? days
      : days + confidenceRange().lowerBound();
  }

  /**
   * @return the day corresponding to the <em>upper</em> bound of the {@link Age} when considering
   * the associated {@link ConfidenceRange}.
   */
  default int upperBound() {
    int days = days();
    return isPrecise()
      ? days
      : days + confidenceRange().upperBound();
  }

  /**
   * Add <code>other</code> {@link Age} to <code>this</code>. Note, the {@link ConfidenceRange} associated with
   * the <code>other</code> {@link Age} is not taken into account.
   *
   * @param other gestational or postnatal {@link Age}.
   * @return new {@link Age} representing the total number of days of <code>this</code> and <code>other</code>
   * with <code>this</code>'s {@link ConfidenceRange}.
   */
  default Age plus(Age other) {
    int days = days() + other.days();
    return Age.of(days, isGestational(), confidenceRange());
  }

  /**
   * @return the greater {@link Age} based on comparing {@code a} and {@code b} using {@link #compare(Age, Age)}.
   * {@code a} is returned in case of a tie.
   */
  static Age max(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare >= 0 ? a : b;
  }

  /**
   * @return the smaller {@link Age} based on comparing {@code a} and {@code b} using {@link #compare(Age, Age)}.
   * {@code a} is returned in case of a tie.
   */
  static Age min(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare <= 0 ? a : b;
  }

  /**
   * A comparator-like function for default sorting of {@link Age} instances.
   * <p>
   * The {@link Age}s are first compared based on the {@link PointInTime} attributes. In case of a tie,
   * the {@link Age}s are compared based on the {@link ConfidenceRange}, where the {@link Age} with a narrower
   * {@link ConfidenceRange} is better/greater.
   */
  static int compare(Age x, Age y) {
    int result = PointInTime.compare(x, y);
    if (result != 0)
      return result;

    return ConfidenceRange.compare(x.confidenceRange(), y.confidenceRange());
  }

  /**
   * Ensure the {@link ConfidenceRange} does not extend beyond {@link PointInTime#lastMenstrualPeriod()} for gestational {@link Age}
   * and beyond {@link PointInTime#birth()} for postnatal {@link Age}.
   * <p>
   * The clipping sets the lower bound to include at most {@link PointInTime#lastMenstrualPeriod()} or {@link PointInTime#birth()}.
   *
   * @param cr {@link ConfidenceRange} to clip.
   * @param days number of days either since {@link PointInTime#lastMenstrualPeriod()} or {@link PointInTime#birth()}.
   * @return clipped {@link ConfidenceRange} or the <code>cr</code> instance if clipping was not necessary.
   */
  private static ConfidenceRange clipConfidenceInterval(ConfidenceRange cr, int days) {
    if (cr.lowerBound() < -days)
      return ConfidenceRange.of(-days, cr.upperBound());
    return cr;
  }
}
