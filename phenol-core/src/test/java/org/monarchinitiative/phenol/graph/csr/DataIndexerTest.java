package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DataIndexerTest {

  @Test
  public void byteIndexer() {
    DataIndexer<Byte> indexer = DataIndexer.byteIndexer();
    assertThat(indexer.empty(), equalTo((byte) 0));
    assertThat(indexer.setNthSlot((byte) 0b0000, 0), equalTo((byte) 0b0001));
    assertThat(indexer.setNthSlot((byte) 0b0000, 1), equalTo((byte) 0b0010));
    assertThat(indexer.setNthSlot((byte) 0b0000, 2), equalTo((byte) 0b0100));
    assertThat(indexer.setNthSlot((byte) 0b0000, 3), equalTo((byte) 0b1000));
    assertThat(indexer.isSet((byte) 0b1001, 0), equalTo(true));
    assertThat(indexer.isSet((byte) 0b1001, 1), equalTo(false));
    assertThat(indexer.isSet((byte) 0b1001, 2), equalTo(false));
    assertThat(indexer.isSet((byte) 0b1001, 3), equalTo(true));
    assertThat(indexer.isSet((byte) 0b1001, 4), equalTo(false));
    assertThat(indexer.isSet((byte) 0b1000_1001, 7), equalTo(true));
    assertThat(indexer.createArray().apply(5), equalTo(new Byte[5]));
    assertThat(indexer.maxIdx(), equalTo(7));
  }

  @Test
  public void shortIndexer() {
    DataIndexer<Short> indexer = DataIndexer.shortIndexer();
    assertThat(indexer.empty(), equalTo((short) 0));
    assertThat(indexer.setNthSlot((short) 0b1001, 5), equalTo((short) 0b10_1001));
    assertThat(indexer.setNthSlot((short) 0b1001, 15), equalTo((short) 0b10000000_00001001));
    assertThat(indexer.isSet((short) 0b1001, 0), equalTo(true));
    assertThat(indexer.isSet((short) 0b1001, 3), equalTo(true));
    assertThat(indexer.isSet((short) 0b10000000_00001001, 14), equalTo(false));
    assertThat(indexer.isSet((short) 0b10000000_00001001, 15), equalTo(true));
    assertThat(indexer.createArray().apply(5), equalTo(new Short[5]));
    assertThat(indexer.maxIdx(), equalTo(15));
  }

  @Test
  public void intIndexer() {
    DataIndexer<Integer> indexer = DataIndexer.integerIndexer();
    assertThat(indexer.empty(), equalTo(0));
    assertThat(indexer.setNthSlot(0b1001, 9), equalTo( 0b10_0000_1001));
    assertThat(indexer.setNthSlot(0b1001, 31), equalTo( 0b10000000_00000000_00000000_00001001));
    assertThat(indexer.createArray().apply(5), equalTo(new Integer[5]));
    assertThat(indexer.maxIdx(), equalTo(31));
  }

  @Test
  public void longIndexer() {
    DataIndexer<Long> indexer = DataIndexer.longIndexer();
    assertThat(indexer.empty(), equalTo( 0L));
    assertThat(indexer.setNthSlot(0b1001L, 63), equalTo(0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00001001L));
    assertThat(indexer.createArray().apply(5), equalTo(new Long[5]));
    assertThat(indexer.maxIdx(), equalTo(63));
  }
}
