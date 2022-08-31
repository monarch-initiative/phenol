package org.monarchinitiative.phenol.annotations.analysis;

import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;

public class HpoOnsetDistributionSimple implements HpoOnsetDistribution {

  static HpoOnsetDistributionSimple of() {
    return new HpoOnsetDistributionSimple();
  }

  private HpoOnsetDistributionSimple() {
  }

  @Override
  public boolean isObservableInAge(HpoDisease disease, TemporalInterval interval) {
    for (HpoDiseaseAnnotation annotation : disease.annotations()) {
      int nPatients = annotation.observedInInterval(interval);
      if (nPatients > 0)
        return true;
    }
    return false;
  }

}
