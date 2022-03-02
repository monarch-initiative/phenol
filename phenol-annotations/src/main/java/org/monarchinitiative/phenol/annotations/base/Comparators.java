package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Comparator;

class Comparators {

  enum PeriodNaturalOrderComparator implements Comparator<Period> {
    INSTANCE;

    @Override
    public int compare(Period x, Period y) {
      int result = Integer.compare(x.getYears(), y.getYears());
      if (result != 0) return result;

      result = Integer.compare(x.getMonths(), y.getMonths());
      if (result != 0) return result;

      return Integer.compare(x.getDays(), y.getDays());
    }
  }

  enum AgeNaturalOrderComparator implements Comparator<Age> {
    INSTANCE;

    @Override
    public int compare(Age o1, Age o2) {
      return Age.compare(o1, o2);
    }
  }

  enum TemporalRangeNaturalOrderComparator implements Comparator<TemporalRange> {
    INSTANCE;

    @Override
    public int compare(TemporalRange o1, TemporalRange o2) {
      return TemporalRange.compare(o1, o2);
    }
  }

}
