package org.monarchinitiative.phenol.graph.csr.mono;

import org.monarchinitiative.phenol.graph.csr.util.Util;

import java.util.*;

/**
 * A CSR-like data structure for retrieval of nodes {@link T} that are adjacent to a {@link T} under
 * a <code>row</code> index.
 * <p>
 * The array is inspired by CSR format, but it does not need the column indices and stores directly
 * the {@link T} elements that represent the adjacent nodes. Since we are mostly interested in propagating relationships
 * such as <em>is_a</em>, the <em>adjacent</em> corresponds either to parents or children most of the time.
 *
 * @param <T> – the type of elements in this array is generic over (most likely term ID).
 */
class StaticCsrArray<T> {

  private final int[] indptr;
  private final List<T> data;

  StaticCsrArray(int[] indptr, List<T> data) {
    this.indptr = Util.checkSequenceOfNonNegativeInts(indptr);
    this.data = data;
  }

  int[] getIndptr() {
    return indptr;
  }

  List<T> getData() {
    return data;
  }

  Set<T> getOutgoingNodes(int row) {
    if (row < 0 || indptr.length < row)
      throw new NoSuchElementException();

    int start = indptr[row];
    int end = indptr[row + 1];

    if (start == end)
      return Collections.emptySet();

    return new AbstractSet<>() {
      @Override
      public Iterator<T> iterator() {
        return new CsrArrayIterator<>(data, start, end);
      }

      @Override
      public int size() {
        return end - start;
      }
    };
  }

  /**
   * An iterator over items associated with
   * <em>NOT</em> thread safe!
   */
  private static class CsrArrayIterator<T> implements Iterator<T> {

    private final List<T> data;
    private int cursor;
    private final int end;

    private CsrArrayIterator(List<T> data,
                             int start,
                             int end) {
      this.data = data;
      assert start <= end;
      this.cursor = start;
      this.end = end;
    }

    @Override
    public boolean hasNext() {
      return cursor < end;
    }

    @Override
    public T next() {
      try {
        return data.get(cursor++);
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }

  }
}
