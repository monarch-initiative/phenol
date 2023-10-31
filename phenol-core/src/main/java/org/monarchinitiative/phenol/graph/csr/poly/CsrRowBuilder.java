package org.monarchinitiative.phenol.graph.csr.poly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * {@linkplain CsrRowBuilder} builds a row of a specialized CSR matrix that stores integral values where we encode flags
 * (presence) into individual bits.
 * <p>
 * The builder allows to set n-th bit of a column via {@link #setNthBitInCol(int, int)} and keeps track of
 * the columns where at least one bit was set. The builder maintains the columns in the sorted order.
 *
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
class CsrRowBuilder<T> {

  private final DataIndexer<T> indexer;
  private final List<Integer> cols = new ArrayList<>();
  private final List<T> values = new ArrayList<>();

  CsrRowBuilder(DataIndexer<T> indexer) {
    this.indexer = Objects.requireNonNull(indexer);
  }

  void setNthBitInCol(int colIdx, int n) {
    if (colIdx < 0 || n < 0)
      throw new IllegalArgumentException(String.format("colIdx %d and n %d must not be negative", colIdx, n));

    int idx = Collections.binarySearch(cols, colIdx);
    int workingIndex;
    if (idx < 0) { // not there yet
      if (cols.isEmpty()) {
        workingIndex = 0;
        cols.add(colIdx);
        values.add(indexer.empty());
      } else {
        workingIndex = (-idx)-1;
        cols.add(workingIndex, colIdx);
        values.add(workingIndex, indexer.empty());
      }
    } else {
      workingIndex = idx;
    }

    T val = values.remove(workingIndex);
    T updated = indexer.setNthSlot(val, n);

    // store
    if (values.isEmpty())
      values.add(updated);
    else
      values.add(workingIndex, updated);
  }

  List<Integer> getColIndices() {
    return cols;
  }

  List<T> getValues() {
    return values;
  }

  void clear() {
    cols.clear();
    values.clear();
  }
}
