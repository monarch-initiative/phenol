package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * Implementors know if they are located on the gestational or on the postnatal timeline.
 */
public interface TimelineAware {

  /**
   * @return true if the temporal concept resides on the <em>gestational</em> timeline, occurring at or after the last menstrual period but prior birth.
   */
  boolean isGestational();

  /* **************************************************************************************************************** */

  /**
   * @return true if the temporal concept resides on the <em>postnatal</em> timeline, occurring at or after birth.
   */
  default boolean isPostnatal() {
    return !isGestational();
  }

}
