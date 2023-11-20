package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.graph.csr.util.Util;

import java.util.*;
import java.util.function.Predicate;

/**
 * {@linkplain StaticCsrArray} stores {@link T} entries and allows obtaining the column indices of items
 * that match a provided predicate.
 *
 * @param <T> entry type.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class StaticCsrArray<T> {

  // TODO - implement byte matrix.

  private final int[] indptr;
  private final int[] indices;
  private final T[] data;

  public StaticCsrArray(int[] indptr, int[] indices, T[] data) {
    this.indptr = Util.checkSequenceOfNonNegativeInts(indptr);
    this.indices = Util.checkSequenceOfNonNegativeInts(indices);
    this.data = data;
  }

  int[] indptr() {
    return indptr;
  }

  int[] indices() {
    return indices;
  }

  T[] data() {
    return data;
  }

  /**
   * Get an iterator over the column indices of entries match the {@code predicate}.
   */
  public Iterator<Integer> colIndicesOfVal(int row, Predicate<T> predicate) {
    // bound check
    if (row < 0 || row >= indptr.length - 1) // there are nRows+1 items in the `indptr` array
      throw new IllegalArgumentException(String.format("Row index must be in range [0, %d] but got %d", indptr.length - 2, row));

    int start = indptr[row];
    int end = indptr[row + 1];

    if (start == end)
      // An empty row, no need to instantiate our iterator.
      return Collections.emptyIterator();

    return new ColIterator(start, end, predicate);
  }

  /**
   * <em>NOT</em> thread safe!
   */
  private class ColIterator implements Iterator<Integer> {

    private int start;
    private final int end;
    private final Predicate<T> predicate;
    private int current;

    private ColIterator(int start,
                        int end,
                        Predicate<T> predicate) {
      assert start <= end;
      this.start = start;
      this.end = end;
      this.predicate = predicate;
      this.current = advance();
    }

    @Override
    public boolean hasNext() {
      return current >= 0;
    }

    @Override
    public Integer next() {
      int current = this.current;
      this.current = advance();
      return current;
    }

    private int advance() {
      while (start < end) {
        int colIdx = indices[start];
        // We can increment `start` because this is its last use in the loop.
        if (predicate.test(data[start++]))
          return colIdx;
      }

      return -1;
    }
  }

}
