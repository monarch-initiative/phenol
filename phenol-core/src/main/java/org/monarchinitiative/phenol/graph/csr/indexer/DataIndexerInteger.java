package org.monarchinitiative.phenol.graph.csr.indexer;

import org.monarchinitiative.phenol.graph.csr.DataIndexer;

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
    return (int) (previous | (1 << n));
  }

  @Override
  public boolean isSet(Integer value, int n) {
    return ((1 << n) & value) > 0;
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
