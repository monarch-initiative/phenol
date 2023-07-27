package org.monarchinitiative.phenol.utils;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A simple wrapper around an existing {@linkplain Iterator} supplier to provide {@linkplain Iterable}.
 * @param <T> type of the item we iterate over.
 */
public class IterableIteratorWrapper<T> implements Iterable<T> {

  private final Supplier<Iterator<T>> supplier;

  public IterableIteratorWrapper(Supplier<Iterator<T>> supplier) {
    this.supplier = Objects.requireNonNull(supplier);
  }

  @Override
  public Iterator<T> iterator() {
    return supplier.get();
  }
}
