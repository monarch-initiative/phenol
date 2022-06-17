package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;

import java.util.Optional;

/**
 * Has a {@link #ratio()} and <em>may have</em> {@link #temporalRange()}.
 */
public interface KnowsRatioAndMaybeTemporalRange {

  // TODO(ielis) - find the proper interface target
  static KnowsRatioAndMaybeTemporalRange of(Ratio ratio, TemporalRange temporalRange) {
    return new KnowsRatioAndMaybeTemporalRangeDefault(ratio, temporalRange);
  }

  Ratio ratio();

  Optional<TemporalRange> temporalRange();

}
