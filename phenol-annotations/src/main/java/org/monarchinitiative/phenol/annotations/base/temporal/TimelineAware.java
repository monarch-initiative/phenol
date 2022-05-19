package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * Implementors know if they are located on the gestational or on the postnatal timeline.
 */
public interface TimelineAware {

  /**
   * @return true if the {@link TemporalPoint} represents the time passed since conception but prior {@link TemporalPoint#()}.
   */
  boolean isGestational();

  /* **************************************************************************************************************** */

  /**
   * @return true if the {@link Age} represents the time passed since {@link Age#birth()}.
   */
  default boolean isPostnatal() {
    return !isGestational();
  }

}
