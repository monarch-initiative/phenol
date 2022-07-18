package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * Representation of different relative positions of two {@link TemporalInterval}s {@code x}
 * and {@code y} in that particular order.
 *
 * The relative positions are exclusive. For instance, if an interval is {@link #UPSTREAM} then it is
 * <em>not</em> {@link #OVERLAPS_UPSTREAM}, <em>not</em> {@link #OVERLAPS_DOWNSTREAM}, etc.
 */
public enum OverlapStatus {

  /**
   * Indicates that {@code x} starts and ends upstream of {@code y}.
   */
  UPSTREAM,

  /**
   * Indicates that {@code x} starts upstream of {@code y} but ends in {@code y}, implying a certain overlap.
   * However, {@code x} does <em>not</em> contain {@code y}.
   */
  OVERLAPS_UPSTREAM,

  /**
   * Indicates that {@code x} contains {@code y}.
   * In other words, {@code x} starts upstream of {@code y} and ends downstream of {@code y}.
   */
  CONTAINS,
  /**
   * Indicates that {@code x} is contained in {@code y}.
   * In other words, {@code x} starts at or after {@code y}'s start and {@code x} ends at or before {@code y}'s end.
   */
  CONTAINED_IN,

  /**
   * Indicates that {@code x} starts within {@code y} and ends downstream of {@code y}, implying a certain overlap.
   * However, {@code x} does <em>not</em> contain {@code y}.
   */
  OVERLAPS_DOWNSTREAM,

  /**
   * Indicates that {@code x} starts and ends downstream of {@code y}.
   */
  DOWNSTREAM

}
