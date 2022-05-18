package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * {@link Age} represents the duration since the start of the time-line.
 * Start of the timeline is represented by the {@link Age#lastMenstrualPeriod()} for prenatal events
 * and by {@link Age#birth()} for postnatal events.
 * <p>
 * Internally, the duration is stored as a non-negative number of days passed since the start of the time-line.
 */
public interface Age {

  double DAYS_IN_JULIAN_YEAR = 365.25;

  double DAYS_IN_MONTH = DAYS_IN_JULIAN_YEAR / 12;

  /**
   * To prevent numerical overflow, we do not allow creation of a {@link Age} corresponding to more
   * than 2,000 Julian years (730,500 days). It should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = 20 * 36_525;

  /**
   * @return age representing the last menstrual period before the conception.
   */
  static Age lastMenstrualPeriod() {
    return AgeGestational.LMP;
  }

  /**
   * @return age representing birth.
   */
  static Age birth() {
    return AgePostnatal.BIRTH;
  }

  /**
   * Create a precise gestational {@link Age} representing the number of weeks and months
   * since the {@link Age#lastMenstrualPeriod()}.
   *
   * @param weeks a non-negative number of completed weeks since the {@link Age#lastMenstrualPeriod()}.
   * @param days a non-negative number of additional days to the number of completed weeks.
   * @return precise gestational age.
   */
  static Age gestational(int weeks, int days) {
    return gestational(weeks, days, ConfidenceInterval.precise());
  }

  /**
   * Create a possibly imprecise gestational {@link Age} representing the number of weeks and months
   * since the {@link Age#lastMenstrualPeriod()}.
   *
   * @param weeks a non-negative number of completed weeks since the {@link Age#lastMenstrualPeriod()}.
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
   * since {@link Age#birth()}.
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
   * since {@link Age#birth()}.
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
    days += Math.toIntExact(Math.round(convertYearsAndMonthsToDays(years, months)));
    return postnatal(days, ci);
  }

  /**
   * The preferred way for converting <code>years</code> and <code>months</code> to days.
   *
   * @return number of days that occur in given <code>years</code> and <code>months</code>
   */
  private static double convertYearsAndMonthsToDays(int years, int months) {
    return years * DAYS_IN_JULIAN_YEAR + months * DAYS_IN_MONTH;
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
   * @throws IllegalArgumentException if <code>days</code> is negative or equal to {@link Integer#MAX_VALUE}.
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization.
   */
  static Age of(int days, boolean isGestational, ConfidenceInterval ci) throws IllegalArgumentException, ArithmeticException {
    if (days == Integer.MAX_VALUE)
      throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end age");
    else if (days < 0)
      throw new IllegalArgumentException("Days must not be negative, got '" + days + "' days!");

    if (days > MAX_DAYS)
      throw new ArithmeticException("Normalized number of days must not be greater than '" + MAX_DAYS + "'. Got '" + days + '\'');

    if (Objects.requireNonNull(ci).isImprecise())
      ci = clipConfidenceInterval(ci, days);

    return isGestational
      ? AgeGestational.of(days, ci)
      : AgePostnatal.of(days, ci);
  }

  /**
   * @return age that represents event that occurred in unspecified distant past.
   */
  static Age openStart() {
    return AgeGestational.START;
  }

  /**
   * @return age that represents event that occurs in unspecified distant future.
   */
  static Age openEnd() {
    return AgePostnatal.END;
  }

  /* **************************************************************************************************************** */

  /**
   * Get the number of days since start of the time-line. This is the number of days since {@link Age#lastMenstrualPeriod()}
   * for {@link Age#isGestational()} and since {@link Age#birth()} for {@link Age#isPostnatal()}.
   * The number is <em>non-negative</em>.
   *
   * @return the number of days.
   */
  int days();

  /**
   * @return true if the {@link Age} represents the time passed since conception but prior {@link Age#birth()}.
   */
  boolean isGestational();

  boolean isOpen();

  /**
   * @return age confidence interval
   */
  ConfidenceInterval confidenceInterval();

  /* **************************************************************************************************************** */

  /**
   * @return true if the {@link Age} represents the time passed since {@link Age#birth()}.
   */
  default boolean isPostnatal() {
    return !isGestational();
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

  default boolean isPrecise() {
    return confidenceInterval().isPrecise();
  }

  default boolean isImprecise() {
    return !isPrecise();
  }

  /**
   * Get a precise {@link TemporalInterval} spanning the confidence interval associated with this age. The {@link TemporalInterval}
   * has length <code>0</code> if the {@link Age#isPrecise()}.
   *
   * @return the temporal interval.
   */
  default TemporalInterval asTemporalInterval() {
    return confidenceInterval().asTemporalInterval(this);
  }

  /**
   * Add <code>other</code> age to <code>this</code>. Note, the {@link ConfidenceInterval} associated with
   * the <code>other</code> {@link Age} is not taken into account.
   *
   * @param other age
   * @return new {@link Age} representing the total number of days with this {@link ConfidenceInterval}.
   */
  default Age plus(Age other) {
    int days = days() + other.days();
    return Age.of(days, isGestational(), confidenceInterval());
  }

  /* **************************************************************************************************************** */

  static Age max(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare >= 0 ? a : b;
  }

  static Age min(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare <= 0 ? a : b;
  }

  /* **************************************************************************************************************** */

  static int compare(Age x, Age y) {
    if (x.isGestational() ^ y.isGestational())
      return x.isGestational() ? -1 : 1;

    int result = Integer.compare(x.days(), y.days());
    if (result != 0)
      return result;

    return ConfidenceInterval.compare(x.confidenceInterval(), y.confidenceInterval());
  }


  /**
   * Ensure the confidence interval does not extend beyond {@link Age#lastMenstrualPeriod()} for gestational {@link Age}
   * and beyond {@link Age#birth()} for postnatal {@link Age}.
   * <p>
   * The clipping sets the lower bound to include at most {@link Age#lastMenstrualPeriod()} or {@link Age#birth()}.
   *
   * @param ci confidence interval to clip.
   * @param days number of days either since {@link Age#lastMenstrualPeriod()} or {@link Age#birth()}.
   * @return clipped {@link ConfidenceInterval} or the <code>ci</code> instance if clipping was not necessary.
   */
  private static ConfidenceInterval clipConfidenceInterval(ConfidenceInterval ci, int days) {
    if (ci.lowerBound() < -days)
      return ConfidenceInterval.of(-days, ci.upperBound());
    return ci;
  }
}
