package org.monarchinitiative.phenol.graph.csr.poly.indexer;

import org.monarchinitiative.phenol.graph.csr.poly.DataIndexer;

public class DataIndexerByte implements DataIndexer<Byte> {

  public static final DataIndexerByte INSTANCE = new DataIndexerByte();

  private DataIndexerByte(){}

  @Override
  public Byte empty() {
    return 0;
  }

  @Override
  public Byte set(Byte previous, int n) {
    return (byte) (previous | (1 << n));
  }

  @Override
  public Byte unset(Byte previous, int n) {
    return (byte) (previous & (byte) ~((1 << n)));
  }

  @Override
  public boolean isSet(Byte value, int n) {
    return (value & (1 << n)) > 0;
  }

  @Override
  public int maxIdx() {
    return Byte.SIZE - 1;
  }

}
