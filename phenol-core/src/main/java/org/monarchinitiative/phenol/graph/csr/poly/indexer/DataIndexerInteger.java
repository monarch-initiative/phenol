package org.monarchinitiative.phenol.graph.csr.poly.indexer;

import org.monarchinitiative.phenol.graph.csr.poly.DataIndexer;

import java.util.function.IntFunction;

public class DataIndexerInteger implements DataIndexer<Integer> {

  public static final DataIndexerInteger INSTANCE = new DataIndexerInteger();

  private DataIndexerInteger(){}

  @Override
  public Integer empty() {
    return 0;
  }

  @Override
  public Integer setNthSlot(Integer previous, int n) {
    return previous | (1 << n);
  }

  @Override
  public boolean isSet(Integer value, int n) {
    return (value & (1 << n)) > 0;
  }

  @Override
  public IntFunction<Integer[]> createArray() {
    return Integer[]::new;
  }

  @Override
  public int maxIdx() {
    return Integer.SIZE - 1;
  }

}
