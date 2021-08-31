package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;

@InProgress
public interface Frequency {

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
