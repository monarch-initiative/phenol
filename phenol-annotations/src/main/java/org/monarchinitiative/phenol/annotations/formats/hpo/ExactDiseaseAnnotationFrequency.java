package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;

import java.util.Objects;
import java.util.Optional;

@InProgress
public class ExactDiseaseAnnotationFrequency implements DiseaseAnnotationFrequency {

  private final Ratio ratio;

  public static ExactDiseaseAnnotationFrequency of(Ratio ratio) {
    return new ExactDiseaseAnnotationFrequency(ratio);
  }

  private ExactDiseaseAnnotationFrequency(Ratio ratio) {
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
    ExactDiseaseAnnotationFrequency that = (ExactDiseaseAnnotationFrequency) o;
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
