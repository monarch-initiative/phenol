package org.monarchinitiative.phenol.graph.csr.mono;

import java.util.*;
import java.util.function.Function;

/**
 * An iterator that implements breadth-first search over starting from a <code>source</code> node.
 *
 * @param <T> – the type of elements in producing {@link org.monarchinitiative.phenol.graph.OntologyGraph}
 *           is generic over (most likely term ID).
 */
class TraversingIterator<T> implements Iterator<T> {

  private final Function<T, Iterable<T>> neighbors;
  private final Set<T> seen = new HashSet<>();
  private final Deque<T> buffer = new ArrayDeque<>();

  TraversingIterator(T source,
                     Function<T, Iterable<T>> neighbors) {
    this.neighbors = neighbors;
    for (T item : neighbors.apply(source)) {
      seen.add(item);
      buffer.add(item);
    }
  }

  @Override
  public boolean hasNext() {
    return !buffer.isEmpty();
  }

  @Override
  public T next() {
    T current = buffer.pop();
    for (T item : neighbors.apply(current)) {
      if (!seen.contains(item)) {
        seen.add(item);
        buffer.add(item);
      }
    }
    return current;
  }
}
