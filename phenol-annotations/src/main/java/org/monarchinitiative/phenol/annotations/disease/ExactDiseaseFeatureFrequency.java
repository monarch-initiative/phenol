package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.annotations.base.Ratio;

import java.util.Objects;
import java.util.Optional;

class ExactDiseaseFeatureFrequency implements DiseaseFeatureFrequency {

  private static final ExactDiseaseFeatureFrequency CASE_REPORT = new ExactDiseaseFeatureFrequency(Ratio.of(1, 1));

  private final Ratio ratio;

  static ExactDiseaseFeatureFrequency getCaseReport() {
    return CASE_REPORT;
  }

  static ExactDiseaseFeatureFrequency of(Ratio ratio) {
    return new ExactDiseaseFeatureFrequency(ratio);
  }

  private ExactDiseaseFeatureFrequency(Ratio ratio) {
    this.ratio = Objects.requireNonNull(ratio, "Ratio must not be null.");
  }

  @Override
  public double frequency() {
    return ratio.frequency();
  }

  @Override
  public Optional<Ratio> nOfMProbands() {
    return Optional.of(ratio);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExactDiseaseFeatureFrequency that = (ExactDiseaseFeatureFrequency) o;
    return Objects.equals(ratio, that.ratio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratio);
  }

  @Override
  public String toString() {
    return "RationalFrequency{" +
      "ratio=" + ratio +
      '}';
  }
}
