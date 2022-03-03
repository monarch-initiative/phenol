package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;

/**
 * Lifetimes of some common organisms.
 */
@Deprecated
public enum Lifetimes implements Lifetime {

  /**
   * <ul>
   *   <li>Average gestation period in human obstetrics is 40 weeks (280 days)</li>
   *   <li>Birth at day 0</li>
   *   <li>Average age of death in 80 years</li>
   * </ul>
   *
   */
  HUMAN(Period.of(0, 0, -280).normalized(),
    Period.ZERO,
    Period.of(120, 0, 0)
  );

  private final Period conception, birth, death;

  Lifetimes(Period conception, Period birth, Period death) {
    this.conception = conception;
    this.birth = birth;
    this.death = death;
  }

  @Override
  public Period conception() {
    return conception;
  }

  @Override
  public Period birth() {
    return birth;
  }

  @Override
  public Period death() {
    return death;
  }

}
