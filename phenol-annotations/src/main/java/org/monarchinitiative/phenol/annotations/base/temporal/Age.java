package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * @deprecated we should not think of an {@link Age} as of a point on a timeline. Instead, we should always work with
 * {@link TemporalInterval}s.
 */
@Deprecated(forRemoval = true)
public interface Age {

  static Age birth() {
    return AgeDefault.BIRTH;
  }

  static Age of(AgeSinceBirth ageSinceBirth) {
    return of(ageSinceBirth, ConfidenceInterval.precise());
  }

  static Age of(AgeSinceBirth ageSinceBirth, ConfidenceInterval confidenceInterval) {
    return AgeDefault.of(ageSinceBirth, confidenceInterval);
  }

  AgeSinceBirth timestamp();

  /**
   * @return confidence interval relative to the {@link #timestamp()} timestamp
   * (<em>not</em> with respect to start of the time-line!)
   */
  ConfidenceInterval confidenceInterval();

  /**
   * @return representation of the {@link Age}.
   * This is a {@link TemporalInterval} with length 0 if the {@link Age} is precise,
   * and {@link TemporalInterval} with length of the {@link #confidenceInterval()} if the {@link Age} is imprecise.
   */
  TemporalInterval asTemporalInterval();

  /* **************************************************************************************************************** */

  default boolean isPrecise() {
    return confidenceInterval().isPrecise();
  }

  static int compare(Age x, Age y) {
    int result = AgeSinceBirth.compare(x.timestamp(), y.timestamp());
    if (result != 0)
      return result;

    return ConfidenceInterval.compare(x.confidenceInterval(), y.confidenceInterval());
  }
}
