package org.monarchinitiative.phenol.graph.csr.poly.indexer;

import org.monarchinitiative.phenol.graph.csr.poly.DataIndexer;

public class DataIndexerShort implements DataIndexer<Short> {

  public static final DataIndexerShort INSTANCE = new DataIndexerShort();

  private DataIndexerShort(){}

  @Override
  public Short empty() {
    return 0;
  }

  @Override
  public Short set(Short previous, int n) {
    return (short) (previous | (1 << n));
  }

  @Override
  public boolean isSet(Short value, int n) {
    return (value & (1 << n)) > 0;
  }

  @Override
  public int maxIdx() {
    return Short.SIZE - 1;
  }

}
