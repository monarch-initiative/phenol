package org.monarchinitiative.phenol.annotations.analysis;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;

public class HpoOnsetDistributionSimple implements HpoOnsetDistribution {

  static HpoOnsetDistributionSimple of() {
    return new HpoOnsetDistributionSimple();
  }

  private HpoOnsetDistributionSimple() {
  }

  @Override
  public boolean isObservableInAge(HpoDisease disease, TemporalRange interval) {
    for (HpoDiseaseAnnotation annotation : disease.annotations()) {
      Ratio ratio = annotation.observedInInterval(interval);
      boolean isObservable = ratio.isPositive();
      if (isObservable)
        return true;
    }
    return false;
  }

}
