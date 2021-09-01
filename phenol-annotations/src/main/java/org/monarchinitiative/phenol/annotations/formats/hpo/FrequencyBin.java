package org.monarchinitiative.phenol.annotations.formats.hpo;

public interface FrequencyBin {

  /**
   * @return frequency value represented by a double precision number in bounds <code>[0,1]</code>
   */
  double frequency();

  default double upperBound() {
    return frequency();
  }

  default double lowerBound() {
    return frequency();
  }

}
