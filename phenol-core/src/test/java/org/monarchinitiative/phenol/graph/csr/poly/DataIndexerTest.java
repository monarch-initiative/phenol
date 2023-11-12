package org.monarchinitiative.phenol.graph.csr.poly;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataIndexerTest {

  @Test
  public void byteIndexer() {
    DataIndexer<Byte> indexer = DataIndexer.byteIndexer();
    assertThat(indexer.empty(), equalTo((byte) 0));
    assertThat(indexer.set((byte) 0b0000, 0), equalTo((byte) 0b0001));
    assertThat(indexer.set((byte) 0b0000, 1), equalTo((byte) 0b0010));
    assertThat(indexer.set((byte) 0b0000, 2), equalTo((byte) 0b0100));
    assertThat(indexer.set((byte) 0b0000, 3), equalTo((byte) 0b1000));

    assertThat(indexer.isSet((byte) 0b1001, 0), equalTo(true));
    assertThat(indexer.isSet((byte) 0b1001, 1), equalTo(false));
    assertThat(indexer.isSet((byte) 0b1001, 2), equalTo(false));
    assertThat(indexer.isSet((byte) 0b1001, 3), equalTo(true));
    assertThat(indexer.isSet((byte) 0b1001, 4), equalTo(false));
    assertThat(indexer.isSet((byte) 0b1000_1001, 7), equalTo(true));

    assertThat(indexer.unset((byte) 0b0000, 0), equalTo((byte) 0b0));
    assertThat(indexer.unset((byte) 0b0001, 0), equalTo((byte) 0b0));
    assertThat(indexer.unset((byte) 0b0000_0010, 1), equalTo((byte) 0b0));
    assertThat(indexer.unset((byte) 0b0000_0011, 1), equalTo((byte) 0b1));
    assertThat(indexer.unset((byte) 0b0000_0000, 7), equalTo((byte) 0b0));
    assertThat(indexer.unset((byte) 0b1000_0001, 7), equalTo((byte) 0b1));

    assertThat(indexer.maxIdx(), equalTo(7));
  }

  @Test
  public void shortIndexer() {
    DataIndexer<Short> indexer = DataIndexer.shortIndexer();
    assertThat(indexer.empty(), equalTo((short) 0));
    assertThat(indexer.set((short) 0b1001, 5), equalTo((short) 0b10_1001));
    assertThat(indexer.set((short) 0b1001, 15), equalTo((short) 0b10000000_00001001));
    assertThat(indexer.isSet((short) 0b1001, 0), equalTo(true));
    assertThat(indexer.isSet((short) 0b1001, 3), equalTo(true));
    assertThat(indexer.isSet((short) 0b10000000_00001001, 14), equalTo(false));
    assertThat(indexer.isSet((short) 0b10000000_00001001, 15), equalTo(true));

    assertThat(indexer.unset((short) 0b0000, 0), equalTo((short) 0b0));
    assertThat(indexer.unset((short) 0b0001, 0), equalTo((short) 0b0));
    assertThat(indexer.unset((short) 0b0000_0010, 1), equalTo((short) 0b0));
    assertThat(indexer.unset((short) 0b0000_0011, 1), equalTo((short) 0b1));
    assertThat(indexer.unset((short) 0b10000000_00001001, 14), equalTo((short) 0b10000000_00001001));
    assertThat(indexer.unset((short) 0b10000000_00001001, 15), equalTo((short) 0b1001));

    assertThat(indexer.maxIdx(), equalTo(15));
  }

  @Test
  public void intIndexer() {
    DataIndexer<Integer> indexer = DataIndexer.integerIndexer();
    assertThat(indexer.empty(), equalTo(0));
    assertThat(indexer.set(0b1001, 9), equalTo( 0b10_0000_1001));
    assertThat(indexer.set(0b1001, 31), equalTo( 0b10000000_00000000_00000000_00001001));

    assertThat(indexer.unset(0b0000, 0), equalTo(0b0));
    assertThat(indexer.unset(0b0001, 0), equalTo(0b0));
    assertThat(indexer.unset(0b0000_0010, 1), equalTo(0b0));
    assertThat(indexer.unset(0b0000_0011, 1), equalTo(0b1));
    assertThat(indexer.unset(0b10000000_00000000_00000000_00001001, 30), equalTo(0b10000000_00000000_00000000_00001001));
    assertThat(indexer.unset(0b10000000_00000000_00000000_00001001, 31), equalTo(0b1001));

    assertThat(indexer.maxIdx(), equalTo(31));
  }

  @Test
  public void longIndexer() {
    DataIndexer<Long> indexer = DataIndexer.longIndexer();
    assertThat(indexer.empty(), equalTo( 0L));
    assertThat(indexer.set(0b1001L, 63), equalTo(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00001001L));

    assertThat(indexer.unset(0b0000L, 0), equalTo(0b0L));
    assertThat(indexer.unset(0b0001L, 0), equalTo(0b0L));
    assertThat(indexer.unset(0b0000_0011L, 0), equalTo(0b10L));
    assertThat(indexer.unset(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00001001L, 62), equalTo(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00001001L));
    assertThat(indexer.unset(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00001001L, 63), equalTo(0b1001L));

    assertThat(indexer.maxIdx(), equalTo(63));
  }
}
