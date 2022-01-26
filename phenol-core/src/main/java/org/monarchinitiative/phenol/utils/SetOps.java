package org.monarchinitiative.phenol.utils;

import java.util.Collection;
import java.util.Set;

interface SetOps {

  <T> Set<T> intersection(Collection<T> left, Collection<T> right);

  <T> Set<T> union(Collection<T> left, Collection<T> right);

}
