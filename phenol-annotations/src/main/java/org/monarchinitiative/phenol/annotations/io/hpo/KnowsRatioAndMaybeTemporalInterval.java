package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;

import java.util.Optional;

/**
 * Has a {@link #ratio()} and <em>may have</em> {@link #temporalInterval()}.
 */
public interface KnowsRatioAndMaybeTemporalInterval {

  // TODO(ielis) - find the proper interface target
  static KnowsRatioAndMaybeTemporalInterval of(Ratio ratio, TemporalInterval temporalInterval) {
    return new KnowsRatioAndMaybeTemporalIntervalDefault(ratio, temporalInterval);
  }

  Ratio ratio();

  Optional<TemporalInterval> temporalInterval();

}
