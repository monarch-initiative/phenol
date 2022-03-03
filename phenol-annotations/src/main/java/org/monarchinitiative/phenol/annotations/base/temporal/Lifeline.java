package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 *
 */
public interface Lifeline {

  Age conception();

  default Age birth() {
    return Age.ZERO;
  }

}
