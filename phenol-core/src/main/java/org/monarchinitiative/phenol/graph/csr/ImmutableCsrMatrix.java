package org.monarchinitiative.phenol.graph.csr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * {@linkplain ImmutableCsrMatrix} stores {@link T} entries and allows obtaining the column indices of items
 * that match a provided predicate.
 *
 * @param <T> entry type.
 */
public class ImmutableCsrMatrix<T> {

  private final int[] indptr;
  private final int[] indices;
  private final T[] data;

  ImmutableCsrMatrix(int[] indptr, int[] indices, T[] data) {
    this.indptr = checkSequenceOfNonNegativeInts(indptr);
    this.indices = checkSequenceOfNonNegativeInts(indices);
    this.data = data;
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

  private static int[] checkSequenceOfNonNegativeInts(int[] a) {
    List<String> offenders = new ArrayList<>();
    for (int i = 0; i < a.length; i++) {
      if (a[i] < 0) {
        offenders.add(String.valueOf(i));
      }
    }
    if (!offenders.isEmpty()) {
      String msg = "Expected array of non-negative integers but the following indices were negative: " +
        String.join(", ", offenders);

      throw new IllegalArgumentException(msg);
    }

    return a;
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
      return current >=0;
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
