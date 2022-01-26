package org.monarchinitiative.phenol.utils;

import java.util.Collection;
import java.util.Set;

public class Sets {

  private static final SetOps SET_OPS = new EagerSetOps();

  private Sets() {
  }

  public static <T> Set<T> intersection(Collection<T> left, Collection<T> right) {
    return SET_OPS.intersection(left, right);
  }

  public static <T> Set<T> union(Collection<T> left, Collection<T> right) {
    return SET_OPS.union(left, right);
  }
}
