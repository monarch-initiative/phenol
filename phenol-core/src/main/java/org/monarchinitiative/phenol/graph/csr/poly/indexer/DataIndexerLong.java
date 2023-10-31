package org.monarchinitiative.phenol.graph.csr.poly.indexer;

import org.monarchinitiative.phenol.graph.csr.poly.DataIndexer;

import java.util.function.IntFunction;

public class DataIndexerLong implements DataIndexer<Long> {

  public static final DataIndexerLong INSTANCE = new DataIndexerLong();

  private DataIndexerLong(){}

  @Override
  public Long empty() {
    return 0L;
  }

  @Override
  public Long setNthSlot(Long previous, int n) {
    return (previous | (1L << n));
  }

  @Override
  public boolean isSet(Long value, int n) {
    return (value & (1L << n)) > 0;
  }

  @Override
  public IntFunction<Long[]> createArray() {
    return Long[]::new;
  }

  @Override
  public int maxIdx() {
    return Long.SIZE - 1;
  }

}
