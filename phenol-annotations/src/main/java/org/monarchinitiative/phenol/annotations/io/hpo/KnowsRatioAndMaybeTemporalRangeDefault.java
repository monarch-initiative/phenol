package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;

import java.util.Objects;
import java.util.Optional;

class KnowsRatioAndMaybeTemporalRangeDefault implements KnowsRatioAndMaybeTemporalRange {

  private final TemporalRange temporalRange;
  private final Ratio ratio;

  KnowsRatioAndMaybeTemporalRangeDefault(Ratio ratio, TemporalRange temporalRange) {
    this.ratio = Objects.requireNonNull(ratio);
    this.temporalRange = temporalRange; // nullable
  }

  @Override
  public Ratio ratio() {
    return ratio;
  }

  @Override
  public Optional<TemporalRange> temporalRange() {
    return Optional.ofNullable(temporalRange);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    KnowsRatioAndMaybeTemporalRangeDefault that = (KnowsRatioAndMaybeTemporalRangeDefault) o;
    return Objects.equals(temporalRange, that.temporalRange) && Objects.equals(ratio, that.ratio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temporalRange, ratio);
  }

  @Override
  public String toString() {
    return "TemporalRatioDefault{" +
      "temporalRange=" + temporalRange +
      ", ratio=" + ratio +
      '}';
  }
}
