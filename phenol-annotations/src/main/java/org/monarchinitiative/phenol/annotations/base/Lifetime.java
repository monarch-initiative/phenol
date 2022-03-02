package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;

/**
 * Implementors provide important landmarks of the lifetime of an organism.
 */
public interface Lifetime {

  Period conception();

  Period birth();

  Period death();

  default Age conceptionAge() {
    return Age.of(conception());
  }

  default Age birthAge() {
    return Age.of(birth());
  }

  default Age deathAge() {
    return Age.of(death());
  }

}
