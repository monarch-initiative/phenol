package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;

import java.util.Optional;

@InProgress
public interface DiseaseAnnotationFrequency {

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
