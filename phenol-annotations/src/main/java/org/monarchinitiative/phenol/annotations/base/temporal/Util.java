package org.monarchinitiative.phenol.annotations.base.temporal;

class Util {

  private Util() {
  }

  /**
   * Check that <code>days</code> is not greater than {@link TemporalPoint#MAX_DAYS} or negative.
   *
   * @param days number of days to check.
   * @throws {@link IllegalArgumentException} if the number of days does not pass the checks.
   */
  static void checkDays(int days) {
    if (days > TemporalPoint.MAX_DAYS) {
      if (days == Integer.MAX_VALUE)
        throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end age");
      else
        throw new IllegalArgumentException("Normalized number of days must not be greater than '" + TemporalPoint.MAX_DAYS + "'. Got '" + days + '\'');
    }

    if (days < 0) {
      if (days == Integer.MIN_VALUE)
        throw new IllegalArgumentException("Integer MIN_VALUE is reserved for open end age");
      else
        throw new IllegalArgumentException("Days must not be negative, got '" + days + "' days!");
    }
  }

}
