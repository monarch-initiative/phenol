package org.monarchinitiative.phenol.annotations.base.temporal;

class Util {

  private Util() {
  }

  /**
   * Check that <code>days</code> is not greater than {@link PointInTime#MAX_DAYS} or negative.
   *
   * @param days number of days to check.
   * @return the number of days inputted as <code>days</code>
   * @throws IllegalArgumentException if the number of days does not pass the checks.
   */
  static int checkDays(int days) {
    if (days > PointInTime.MAX_DAYS) {
      if (days == Integer.MAX_VALUE)
        throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end age");
      else
        throw new IllegalArgumentException("Normalized number of days must not be greater than '" + PointInTime.MAX_DAYS + "'. Got '" + days + '\'');
    }

    if (days < 0) {
      if (days == Integer.MIN_VALUE)
        throw new IllegalArgumentException("Integer MIN_VALUE is reserved for open end age");
      else
        throw new IllegalArgumentException("Days must not be negative, got '" + days + "' days!");
    }
    return days;
  }

}
