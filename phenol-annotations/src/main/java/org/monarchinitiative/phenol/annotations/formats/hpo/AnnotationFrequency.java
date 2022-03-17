package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;

import java.util.Optional;

public interface AnnotationFrequency {

  static AnnotationFrequency of(Ratio ratio) {
    return new AnnotationFrequencyDefault(ratio);
  }

  float frequency();

  Optional<Ratio> ratio();

  default Optional<Integer> numerator() {
    return ratio()
      .map(Ratio::numerator);
  }

  default Optional<Integer> denominator() {
    return ratio()
      .map(Ratio::denominator);
  }

  static int compare(AnnotationFrequency x, AnnotationFrequency y) {
    return Float.compare(x.frequency(), y.frequency());
  }

}
