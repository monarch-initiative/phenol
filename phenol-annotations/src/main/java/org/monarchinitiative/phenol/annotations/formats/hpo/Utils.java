package org.monarchinitiative.phenol.annotations.formats.hpo;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

class Utils {

  private Utils() {
  }

  /**
   * Get a new iterable that contains elements from the <code>parent</code> that pass given <code>filter</code>.
   *
   * @param parent parent iterable.
   * @param filter filter to apply for including or excluding elements from the parent.
   * @return iterable containing elements {@link T} that pass the provided <code>filter</code>.
   * @param <T> element type.
   */
  static <T> Iterable<T> filterIterable(Iterable<T> parent, Predicate<T> filter) {
    return new Iterable<>() {
      private final Iterable<T> iterable = Objects.requireNonNull(parent);

      @Override
      public Iterator<T> iterator() {
        return new Iterator<>() {
          private final Iterator<T> iterator = iterable.iterator();
          private T next = nextPassingElement();

          private T nextPassingElement() {
            while (iterator.hasNext()) {
              T t = iterator.next();
              if (filter.test(t))
                return t;
            }
            return null;
          }

          @Override
          public boolean hasNext() {
            return next != null;
          }

          @Override
          public T next() {
            T current = next;
            next = nextPassingElement();
            return current;
          }
        };
      }
    };
  }
}
