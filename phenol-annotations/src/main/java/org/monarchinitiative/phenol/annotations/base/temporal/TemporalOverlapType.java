package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * Representation of different relative positions of two {@link TemporalInterval}s {@code x}
 * and {@code y} on a timeline.
 *
 * The relative positions are exclusive. For instance, if an interval is {@link #BEFORE} then it is
 * <em>not</em> {@link #BEFORE_AND_DURING}, <em>not</em> {@link #DURING_AND_AFTER}, etc.
 */
public enum TemporalOverlapType {

  /**
   * Indicates that {@code x} starts and ends before {@code y}.
   */
  BEFORE,

  /**
   * Indicates that {@code x} starts before {@code y}'s start and ends during {@code y}, implying a certain overlap.
   * However, {@code x} does <em>not</em> contain {@code y}.
   */
  BEFORE_AND_DURING,

  /**
   * Indicates that {@code x} contains {@code y}.
   * In other words, {@code x} starts before {@code y}'s start and ends after {@code y}'s end.
   */
  CONTAINS,

  /**
   * Indicates that {@code x} is contained in {@code y}.
   * In other words, {@code x} starts at or after {@code y}'s start, and ends before or at {@code y}'s end.
   */
  CONTAINED_IN,

  /**
   * Indicates that {@code x} starts during {@code y} and ends after {@code y}'s end, implying a certain overlap.
   * However, {@code x} does <em>not</em> contain {@code y}.
   */
  DURING_AND_AFTER,

  /**
   * Indicates that {@code x} starts and ends after of {@code y}.
   */
  AFTER

}
