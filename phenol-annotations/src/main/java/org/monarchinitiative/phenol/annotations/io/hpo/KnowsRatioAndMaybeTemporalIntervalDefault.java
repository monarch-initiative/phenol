package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;

import java.util.Objects;
import java.util.Optional;

class KnowsRatioAndMaybeTemporalIntervalDefault implements KnowsRatioAndMaybeTemporalInterval {

  private final TemporalInterval temporalInterval;
  private final Ratio ratio;

  KnowsRatioAndMaybeTemporalIntervalDefault(Ratio ratio, TemporalInterval temporalInterval) {
    this.ratio = Objects.requireNonNull(ratio);
    this.temporalInterval = temporalInterval; // nullable
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    KnowsRatioAndMaybeTemporalIntervalDefault that = (KnowsRatioAndMaybeTemporalIntervalDefault) o;
    return Objects.equals(temporalInterval, that.temporalInterval) && Objects.equals(ratio, that.ratio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temporalInterval, ratio);
  }

  @Override
  public String toString() {
    return "KnowsRatioAndMaybeTemporalIntervalDefault{" +
      "temporalRange=" + temporalInterval +
      ", ratio=" + ratio +
      '}';
  }
}
