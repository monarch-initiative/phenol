package org.monarchinitiative.phenol.annotations.base;

/**
 * Entity to represent the fact that <em>n</em> out of <em>m</em> subjects meet a condition. For instance,
 * <em>9</em> out of <em>10</em> kids love lasagna.
 */
public interface Ratio {
  /**
   * @param numerator   non-negative numerator
   * @param denominator positive denominator
   * @throws IllegalArgumentException if the numerator or denominator do not meet the above requirements,
   *                                  or if the numerator is greater than denominator
   */
  static Ratio of(int numerator, int denominator) {
    if (numerator < 0)
      throw new IllegalArgumentException("Numerator must be non-negative");
    if (denominator <= 0)
      throw new IllegalArgumentException("Denominator must be positive");

    if (numerator > denominator)
      throw new IllegalArgumentException("Numerator " + numerator + " must be less than or equal to denominator " + denominator);

    return new RatioDefault(numerator, denominator);
  }

  /**
   * @return number of subjects meeting a condition.
   */
  int numerator();

  /**
   * @return the total number of evaluated subjects.
   */
  int denominator();

  /**
   * @return frequency of the subjects meeting a condition calculated as {@link #numerator()} over {@link #denominator()}.
   */
  default float frequency() {
    return ((float) numerator()) / denominator();
  }

  /**
   * @return <code>true</code> if {@link #numerator()} is equal to <code>0</code>.
   */
  default boolean isZero() {
    return numerator() == 0;
  }

  /**
   * @return <code>true</code> if {@link #numerator()} is greater than <code>0</code>.
   */
  default boolean isPositive() {
    return numerator() > 0;
  }

  /**
   * Sum the two {@link Ratio}s.
   * (i.e. <code>1/2 + 1/4 = 3/6</code>).
   * <p>
   * Note: this is not summation of two fractions, but summation of two <code>n</code> over <code>m</code> counts
   * coming from two clinical studies.
   * In other words, if <code>n<sub>1</sub></code> of <code>m<sub>1</sub></code> members of population <em>a</em>
   * and <code>n<sub>2</sub></code> of <code>m<sub>2</sub></code> of population <em>b</em> like lasagna, then
   * <code>n<sub>1</sub> + n<sub>2</sub></code> of <code>m<sub>1</sub> + m<sub>2</sub></code> people like lasagna
   * in total.
   *
   * @return {@link Ratio} representing the sum of <code>left</code> and <code>right</code>.
   */
  static Ratio sum(Ratio left, Ratio right) {
    return of(left.numerator() + right.numerator(), left.denominator() + right.denominator());
  }

  /**
   * Compare two {@link Ratio}s by their {@link #frequency()}. The {@link Ratio} with <em>greater</em> frequency
   * is greater.
   */
  static int compareByFrequency(Ratio left, Ratio right) {
    return Float.compare(left.frequency(), right.frequency());
  }

}
