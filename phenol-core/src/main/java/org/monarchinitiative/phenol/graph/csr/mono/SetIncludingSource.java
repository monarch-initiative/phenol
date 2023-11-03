package org.monarchinitiative.phenol.graph.csr.mono;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A utility implementation of a {@link Set} that contains
 * an <code>item</code>, a singular instance of {@link T}, and the {@link T} elements in the <code>other</code> set.
 *
 * @param <T> – the type of elements in this set
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
class SetIncludingSource<T> extends AbstractSet<T> {

  private final T item;
  private final Set<T> other;

  SetIncludingSource(T item, Set<T> other) {
    this.item = item;
    this.other = other;
  }

  @Override
  public Iterator<T> iterator() {
    return new IncludingIterator<>(item, other.iterator());
  }

  @Override
  public int size() {
    return other.size() + 1;
  }

  /**
   * An {@link Iterator} that first yields the <code>first</code> item and then the items from the <code>remaining</code> iterator.
   * <p>
   * NOT THREAD SAFE, of course!
   *
   * @param <T> – the type of elements in this iterator
   */
  private static class IncludingIterator<T> implements Iterator<T> {

    private final T first;
    private final Iterator<T> remaining;
    private boolean yieldedFirst = false;

    private IncludingIterator(T first, Iterator<T> remaining) {
      this.first = first;
      this.remaining = remaining;
    }

    @Override
    public boolean hasNext() {
      return !yieldedFirst || remaining.hasNext();
    }

    @Override
    public T next() {
      if (!yieldedFirst) {
        yieldedFirst = true;
        return first;
      }

      return remaining.next();
    }
  }

}
