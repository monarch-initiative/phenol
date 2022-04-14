package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;

import java.util.Objects;
import java.util.Optional;

class AnnotationFrequencyDefault implements AnnotationFrequency {

  private final Ratio ratio;

  AnnotationFrequencyDefault(Ratio ratio) {
    this.ratio = ratio;
  }

  @Override
  public float frequency() {
    return ratio.frequency();
  }

  @Override
  public Optional<Ratio> ratio() {
    return Optional.of(ratio);
  }

  @Override
  public Optional<Integer> numerator() {
    return Optional.of(ratio.numerator());
  }

  @Override
  public Optional<Integer> denominator() {
    return Optional.of(ratio.denominator());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnnotationFrequencyDefault that = (AnnotationFrequencyDefault) o;
    return Objects.equals(ratio, that.ratio);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratio);
  }

  @Override
  public String toString() {
    return "AnnotationFrequencyDefault{" +
      "ratio=" + ratio +
      '}';
  }

}
