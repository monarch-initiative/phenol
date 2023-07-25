package org.monarchinitiative.phenol.graph.csr.indexer;

import org.monarchinitiative.phenol.graph.csr.DataIndexer;

import java.util.function.IntFunction;

public class DataIndexerShort implements DataIndexer<Short> {

  public static final DataIndexerShort INSTANCE = new DataIndexerShort();

  private DataIndexerShort(){}

  @Override
  public Short empty() {
    return 0;
  }

  @Override
  public Short setNthSlot(Short previous, int n) {
    return (short) (previous | (1 << n));
  }

  @Override
  public boolean isSet(Short value, int n) {
    return (value & (1 << n)) > 0;
  }

  @Override
  public IntFunction<Short[]> createArray() {
    return Short[]::new;
  }

  @Override
  public int maxIdx() {
    return Short.SIZE - 1;
  }

}
