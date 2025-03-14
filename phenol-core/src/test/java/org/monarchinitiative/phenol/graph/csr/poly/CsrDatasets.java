package org.monarchinitiative.phenol.graph.csr.poly;

import java.util.List;

/**
 * Some test data for testing the CSR matrix logic.
 */
class CsrDatasets {

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
      List.of(100, 102, 104, 106, 108, 110, 112, 114, 116, 118, 120, 122, 124, 126, 128, 130)
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
      List.of(1, 2, 3, 4, 5, 6)
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
      List.of()
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
      List.of(1, 2, 3, 4, 5)
    );
  }

}
