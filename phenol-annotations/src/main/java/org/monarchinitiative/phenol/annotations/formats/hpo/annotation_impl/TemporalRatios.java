package org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;

import java.util.Collection;
import java.util.Optional;

/**
 * {@link TemporalRatios} represents temporal distribution of concept ratios and frequencies.
 * <p>
 * {@link TemporalRatios} has {@link Ratio} that represents the overall numbers.
 * <p>
 * {@link TemporalRatios} optionally has {@link TemporalInterval} and provides {@link #start()} and {@link #end()} methods
 * that return {@link org.monarchinitiative.phenol.annotations.base.temporal.PointInTime}s representing
 * the earliest start and latest end. Note that {@link #start()} and {@link #end()} may be missing either if the source
 * {@link RatioAndTemporalIntervalAware} lacks the temporal information or if the implementation chooses to use
 * subset of {@link RatioAndTemporalIntervalAware} instances (e.g. only include those with {@link Ratio#isPositive()}).
 */
public interface TemporalRatios {

  static TemporalRatios of(Collection<? extends RatioAndTemporalIntervalAware> ratios) {
    return TemporalRatiosNaive.of(ratios);
  }

  Ratio ratio();

  Optional<TemporalInterval> temporalInterval();

  default Optional<PointInTime> start() {
    return temporalInterval()
      .map(TemporalInterval::start);
  }


  default Optional<PointInTime> end() {
    return temporalInterval()
      .map(TemporalInterval::end);
  }

  /**
   * Get the number of individuals where concept was observable during {@code query} interval.
   *
   * @param query query interval
   * @return the number of patients
   */
  int observedInInterval(TemporalInterval query);

}
