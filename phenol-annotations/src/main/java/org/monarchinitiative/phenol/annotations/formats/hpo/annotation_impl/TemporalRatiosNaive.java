package org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Naive {@link TemporalRatios} implementation that maintains an {@link Iterable} of {@link RatioAndTemporalIntervalAware}
 * and goes through the entire {@link Iterable} during {@link #observedInInterval(TemporalInterval)} invocation.
 */
class TemporalRatiosNaive implements TemporalRatios {

  private final Ratio ratio;

  private final TemporalInterval temporalInterval; // nullable
  private final Iterable<? extends RatioAndTemporalIntervalAware> ratios;

  static TemporalRatiosNaive of(Collection<? extends RatioAndTemporalIntervalAware> ratios) {
    if (ratios.isEmpty())
      throw new IllegalArgumentException("Ratios must not be empty");

    int numerator = 0, denominator = 0;
    PointInTime start = null, end = null;

    for (RatioAndTemporalIntervalAware ratio : ratios) {
      // Update ratio
      Ratio r = ratio.ratio();
      numerator += r.numerator();
      denominator += r.denominator();

      // Update endpoints
      if (r.isPositive()) {
        PointInTime queryStart = ratio.temporalInterval()
          .map(TemporalInterval::start)
          .orElse(null);
        start = start == null
          ? queryStart
          : getEarlierPointInTime(queryStart, start);

        PointInTime queryEnd = ratio.temporalInterval()
          .map(TemporalInterval::end)
          .orElse(null);
        end = end == null
          ? queryEnd
          : getLaterPointInTime(queryEnd, end);
      }
    }

    Ratio ratio = Ratio.of(numerator, denominator);
    TemporalInterval interval = start != null && end != null
      ? TemporalInterval.of(start, end)
      : null;

    return new TemporalRatiosNaive(ratio, interval, ratios);
  }

  private TemporalRatiosNaive(Ratio ratio,
                              TemporalInterval temporalInterval,
                              Iterable<? extends RatioAndTemporalIntervalAware> ratios) {
    this.ratio = Objects.requireNonNull(ratio);
    this.temporalInterval = temporalInterval; // nullable
    this.ratios = ratios;
  }

  @Override
  public Ratio ratio() {
    return ratio;
  }

  @Override
  public Optional<TemporalInterval> temporalInterval() {
    return Optional.ofNullable(temporalInterval);
  }

  @Override
  public int observedInInterval(TemporalInterval query) {
    if (temporalInterval != null && !query.overlapsWith(temporalInterval))
      return 0;

    int numerator = 0;
    for (RatioAndTemporalIntervalAware ratio : ratios) {
      if (ratio.temporalInterval()
        .filter(query::overlapsWith)
        .isPresent()) {
        numerator += ratio.ratio().numerator();
      }
    }

    return numerator;
  }

  private static PointInTime getLaterPointInTime(PointInTime query, PointInTime end) {
    return query != null
      ? PointInTime.max(query, end)
      : end;
  }

  private static PointInTime getEarlierPointInTime(PointInTime query,
                                                   PointInTime start) {
    return query == null
      ? start
      : PointInTime.min(query, start);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TemporalRatiosNaive that = (TemporalRatiosNaive) o;
    return Objects.equals(ratio, that.ratio) && Objects.equals(temporalInterval, that.temporalInterval) && Objects.equals(ratios, that.ratios);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratio, temporalInterval, ratios);
  }

  @Override
  public String toString() {
    return "TemporalRatiosNaive{" +
      "ratio=" + ratio +
      ", temporalInterval=" + temporalInterval +
      ", ratios=" + ratios +
      '}';
  }
}
