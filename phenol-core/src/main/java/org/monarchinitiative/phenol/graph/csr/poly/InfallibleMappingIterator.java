package org.monarchinitiative.phenol.graph.csr.poly;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@linkplain InfallibleMappingIterator} applies a {@code mapper} function to each item from the {@code base} iterator.
 * The {@code mapper} function is infallible.
 *
 * @param <T> type of elements yielded by the base iterator.
 * @param <U> type of elements yielded by this iterator.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
class InfallibleMappingIterator<T, U> implements Iterator<U> {

  private final Iterator<T> base;
  private final Function<T, U> mapper;

  InfallibleMappingIterator(Supplier<Iterator<T>> base, Function<T, U> mapper) {
    this.base = base.get();
    this.mapper = mapper;
  }

  @Override
  public boolean hasNext() {
    return base.hasNext();
  }

  @Override
  public U next() {
    return mapper.apply(base.next());
  }
}
