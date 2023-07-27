package org.monarchinitiative.phenol.graph.csr.indexer;

import org.monarchinitiative.phenol.graph.csr.DataIndexer;

import java.util.function.IntFunction;

public class DataIndexerByte implements DataIndexer<Byte> {

  public static final DataIndexerByte INSTANCE = new DataIndexerByte();

  private DataIndexerByte(){}

  @Override
  public Byte empty() {
    return 0;
  }

  @Override
  public Byte setNthSlot(Byte previous, int n) {
    return (byte) (previous | (1 << n));
  }

  @Override
  public boolean isSet(Byte value, int n) {
    return (value & (1 << n)) > 0;
  }

  @Override
  public IntFunction<Byte[]> createArray() {
    return Byte[]::new;
  }

  @Override
  public int maxIdx() {
    return Byte.SIZE - 1;
  }

}
