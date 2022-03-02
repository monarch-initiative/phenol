package org.monarchinitiative.phenol.annotations.base;

public interface TemporalRangeAware {

  TemporalRange temporalRange();

  default Age start() {
    return temporalRange().start();
  }

  default Age end() {
    return temporalRange().end();
  }

}
