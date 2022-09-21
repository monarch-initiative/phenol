package org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;

import java.util.Optional;

/**
 * An entity that has a {@link #ratio()} and <em>may have</em> a {@link #temporalInterval()}.
 */
public interface RatioAndTemporalIntervalAware {

  static RatioAndTemporalIntervalAware of(Ratio ratio, TemporalInterval temporalInterval) {
    return new RatioAndTemporalIntervalAwareDefault(ratio, temporalInterval);
  }

  Ratio ratio();

  /**
   * @return {@link TemporalInterval} that represents the interval when the entity was observed.
   */
  Optional<TemporalInterval> temporalInterval();

  /**
   * @return the opposite of what {@link #isAbsent()} returns.
   * @see #isAbsent()
   */
  default boolean isPresent() {
    return !isAbsent();
  }

  /**
   * Return {@code true} if the entity in question was annotated to be absent. This means that the numerator of
   * {@link #ratio()} is zero, because an annotation such as <em>0/k</em> exists
   * that represents a study in which zero of <em>k</em> study participants were observed not to have the HPO term
   * in question.
   *
   * @return {@code true} if the entity in question was annotated to be absent.
   */
  default boolean isAbsent() {
    return ratio().isZero();
  }

  /**
   * @return {@link Optional} with start of the {@link #temporalInterval()} or an empty {@link Optional}
   * if {@link #temporalInterval()} is absent.
   */
  default Optional<PointInTime> start() {
    return temporalInterval()
      .map(TemporalInterval::start);
  }

  /**
   * @return {@link Optional} with end of the {@link #temporalInterval()} or an empty {@link Optional}
   * if {@link #temporalInterval()} is absent.
   */
  default Optional<PointInTime> end() {
    return temporalInterval()
      .map(TemporalInterval::end);
  }

}
