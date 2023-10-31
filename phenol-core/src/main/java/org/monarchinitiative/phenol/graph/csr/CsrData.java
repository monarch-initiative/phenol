package org.monarchinitiative.phenol.graph.csr;

/**
 * A POJO for the data structures that back a CSR matrix.
 * @param <T> data type
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrData<T> {

  private final int[] indptr;
  private final int[] indices;
  private final T[] data;

  public CsrData(int[] indptr, int[] indices, T[] data) {
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
