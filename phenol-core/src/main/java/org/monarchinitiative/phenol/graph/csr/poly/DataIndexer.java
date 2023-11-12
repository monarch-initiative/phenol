package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.graph.csr.poly.indexer.DataIndexerByte;
import org.monarchinitiative.phenol.graph.csr.poly.indexer.DataIndexerInteger;
import org.monarchinitiative.phenol.graph.csr.poly.indexer.DataIndexerLong;
import org.monarchinitiative.phenol.graph.csr.poly.indexer.DataIndexerShort;

/**
 * @param <T> the type to use for storing the relations.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public interface DataIndexer<T> {

  static DataIndexer<Byte> byteIndexer() {
    return DataIndexerByte.INSTANCE;
  }
  static DataIndexer<Short> shortIndexer() {
    return DataIndexerShort.INSTANCE;
  }
  static DataIndexer<Integer> integerIndexer() {
    return DataIndexerInteger.INSTANCE;
  }
  static DataIndexer<Long> longIndexer() {
    return DataIndexerLong.INSTANCE;
  }

  /**
   * @return an empty value of {@link T}.
   */
  T empty();

  /**
   * Store the presence of the relation in the {@code n}-th slot of the {@code previous} value.
   *
   * @param previous the previous value.
   * @param n slot position.
   * @return the value presence of the relation encoded in the {@code n}-th slot.
   * @throws IllegalArgumentException if {@code n} is negative
   */
  T set(T previous, int n);

  /**
   * Unset the relation in the {@code n}-th slot of the {@code previous} value.
   *
   * @param previous the previous value.
   * @param n slot position.
   * @return the value presence of the relation encoded in the {@code n}-th slot.
   * @throws IllegalArgumentException if {@code n} is negative
   */
  T unset(T previous, int n);

  /**
   * Return {@code true} if the {@code n}-th slot is set or {@code false} otherwise.
   *
   * @param value value to be checked
   * @param n the slot number
   * @return {@code true} or {@code false} as described above.
   * @throws IllegalArgumentException if {@code n} is negative.
   */
  boolean isSet(T value, int n);

  /**
   * Get the number of slots available in {@link T}.
   *
   * @return the number of slots available in {@link T}.
   */
  int maxIdx();

}
