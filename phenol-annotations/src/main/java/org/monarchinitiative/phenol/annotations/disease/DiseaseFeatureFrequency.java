package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.annotations.base.Ratio;

import java.util.Optional;

public interface DiseaseFeatureFrequency {

  static DiseaseFeatureFrequency caseReport() {
    return ExactDiseaseFeatureFrequency.getCaseReport();
  }

  static DiseaseFeatureFrequency exact(int n, int m) {
    Ratio ratio = Ratio.of(n, m);
    return ExactDiseaseFeatureFrequency.of(ratio);
  }

  /**
   * @return frequency value represented by a double precision number in bounds <code>[0,1]</code>
   */
  double frequency();

  Optional<Ratio> nOfMProbands();

  default double upperBound() {
    return frequency();
  }

  default double lowerBound() {
    return frequency();
  }

}
