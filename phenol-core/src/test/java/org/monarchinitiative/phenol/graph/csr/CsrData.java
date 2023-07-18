package org.monarchinitiative.phenol.graph.csr;

/**
 * Some test data for testing the CSR matrix logic.
 * @param <T>
 */
class CsrData<T> {

  /**
   * <pre>
   * [[100 102 104 106]
   *  [108 110 112 114]
   *  [116 118 120 122]
   *  [124 126 128 130]]
   *  </pre>
   */
  static CsrData<Integer> full() {
    return new CsrData<>(
      new int[]{0, 4, 8, 12, 16},
      new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3},
      new Integer[]{100, 102, 104, 106, 108, 110, 112, 114, 116, 118, 120, 122, 124, 126, 128, 130}
    );
  }

  /**
   * <pre>
   * [[1 0 0 2]
   *  [0 3 0 0]
   *  [0 0 4 0]
   *  [5 0 0 6]]
   *  </pre>
   */
  static CsrData<Integer> allEdges() {
    return new CsrData<>(
      new int[]{0, 2, 3, 4, 6},
      new int[]{0, 3, 1, 2, 0, 3},
      new Integer[]{1, 2, 3, 4, 5, 6}
    );
  }

  /**
   * <pre>
   * [[0 0 0]
   *  [0 0 0]
   *  [0 0 0]]
   *  </pre>
   */
  static CsrData<Integer> zeroes() {
    return new CsrData<>(
      new int[]{0, 0, 0, 0},
      new int[]{},
      new Integer[]{}
    );
  }

  /**
   * <pre>
   * [[0 1 0 2]
   *  [3 0 4 5]]
   *  </pre>
   */
  static CsrData<Integer> rect() {
    return new CsrData<>(
      new int[]{0, 2, 5},
      new int[]{1, 3, 0, 2, 3},
      new Integer[]{1, 2, 3, 4, 5}
    );
  }

  private final int[] indptr;
  private final int[] indices;
  private final T[] data;

  private CsrData(int[] indptr, int[] indices, T[] data) {
    this.indptr = indptr;
    this.indices = indices;
    this.data = data;
  }

  public int[] getIndptr() {
    return indptr;
  }

  public int[] getIndices() {
    return indices;
  }

  public T[] getData() {
    return data;
  }
}
