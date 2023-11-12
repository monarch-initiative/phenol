package org.monarchinitiative.phenol.graph.csr.poly.indexer;

import org.monarchinitiative.phenol.graph.csr.poly.DataIndexer;

public class DataIndexerInteger implements DataIndexer<Integer> {

  public static final DataIndexerInteger INSTANCE = new DataIndexerInteger();

  private DataIndexerInteger(){}

  @Override
  public Integer empty() {
    return 0;
  }

  @Override
  public Integer set(Integer previous, int n) {
    return previous | (1 << n);
  }

  @Override
  public Integer unset(Integer previous, int n) {
    return previous & ~(1 << n);
  }

  @Override
  public boolean isSet(Integer value, int n) {
    return (value & (1 << n)) > 0;
  }

  @Override
  public int maxIdx() {
    return Integer.SIZE - 1;
  }

}
