package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * Lifelines of some common organisms.
 */
public enum Lifelines implements Lifeline {

  HUMAN(Age.of(Timestamp.of(-280))),
  MOUSE(Age.of(Timestamp.of(-20), ConfidenceInterval.of(-1, 1)));

  private final Age conception;

  Lifelines(Age conception) {
    this.conception = conception;
  }

  @Override
  public Age conception() {
    return conception;
  }
}
