package org.monarchinitiative.phenol.annotations.analysis;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;

import java.util.Iterator;

public class HpoOnsetDistributionSimple implements HpoOnsetDistribution {

  static HpoOnsetDistributionSimple of() {
    return new HpoOnsetDistributionSimple();
  }

  private HpoOnsetDistributionSimple() {
  }

  @Override
  public boolean isObservableInAge(HpoDisease disease, TemporalInterval interval) {
    for (Iterator<HpoDiseaseAnnotation> iterator = disease.phenotypicAbnormalities(); iterator.hasNext(); ) {
      HpoDiseaseAnnotation annotation = iterator.next();

      boolean isObservable = annotation.observedInInterval(interval)
        .map(Ratio::isPositive)
        .orElse(false);
      if (isObservable)
        return true;
    }
    return false;
  }

}
