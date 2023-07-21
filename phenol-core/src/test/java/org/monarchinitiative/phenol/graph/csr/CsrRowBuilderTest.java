package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsrRowBuilderTest {

  private CsrRowBuilder<Byte> instance;

  @BeforeEach
  public void setUp() {
    instance = new CsrRowBuilder<>(DataIndexer.byteIndexer());
  }

  @Test
  public void empty() {
    assertThat(instance.getColIndices(), is(emptyCollectionOf(Integer.class)));
    assertThat(instance.getValues(), is(emptyCollectionOf(Byte.class)));
  }

  @Test
  public void setNthBitInCol() {
    instance.setNthBitInCol(3, 2);
    instance.setNthBitInCol(2, 1);
    instance.setNthBitInCol(0, 4);
    instance.setNthBitInCol(2, 5);
    instance.setNthBitInCol(4, 6);
    instance.setNthBitInCol(0, 0);
    instance.setNthBitInCol(3, 7);

    assertThat(instance.getColIndices(), equalTo(List.of(0, 2, 3, 4)));
    assertThat(instance.getValues(), equalTo(List.of((byte) 0b1_0001, (byte) 0b10_0010, (byte) 0b1000_0100, (byte) 0b100_0000)));
  }

  @Test
  public void useNegativeOffset() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> instance.setNthBitInCol(-1, 2));
    assertThat(e.getMessage(), equalTo("colIdx -1 and n 2 must not be negative"));
    IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> instance.setNthBitInCol(1, -2));
    assertThat(e2.getMessage(), equalTo("colIdx 1 and n -2 must not be negative"));
  }
}
