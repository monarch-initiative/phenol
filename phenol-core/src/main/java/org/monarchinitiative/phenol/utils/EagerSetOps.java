package org.monarchinitiative.phenol.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EagerSetOps implements SetOps {
  @Override
  public <T> Set<T> intersection(Collection<T> left, Collection<T> right) {
    Set<T> result = new HashSet<>(left);
    result.retainAll(right);
    return result;
  }

  @Override
  public <T> Set<T> union(Collection<T> left, Collection<T> right) {
    HashSet<T> result = new HashSet<>(left);
    result.addAll(right);
    return result;
  }
}
