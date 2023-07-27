package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.graph.RelationType;
import org.monarchinitiative.phenol.graph.RelationTypes;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RelationCodecDefaultTest {

  private static final RelationType SOME = RelationType.of("some", "some relation type", false);
  private static final RelationType OTHER = RelationType.of("other", "other relation type", false);
  private static final DataIndexer<Byte> INDEXER = DataIndexer.byteIndexer();

  @Nested
  public class BitIndexChecks {
    private RelationCodecDefault<Byte> codec;

    @BeforeEach
    public void setUp() {
      codec = RelationCodecDefault.of(List.of(
        RelationTypes.isA(),
        RelationTypes.partOf(),
        SOME
      ), INDEXER);
    }

    @Test
    public void calculateBitIndex_propagating() {
      assertThat(codec.calculateBitIndex(RelationTypes.isA()), equalTo(0));
      assertThat(codec.calculateBitIndex(RelationTypes.isA(), true), equalTo(1));
      assertThat(codec.calculateBitIndex(RelationTypes.partOf()), equalTo(2));
      assertThat(codec.calculateBitIndex(RelationTypes.partOf(), true), equalTo(3));
    }

    @Test
    public void calculateBitIndex_nonPropagating() {
      assertThat(codec.calculateBitIndex(SOME), equalTo(4));
    }

    @Test
    public void calculateBitIndex_unknown() {
      assertThat(codec.calculateBitIndex(OTHER), equalTo(-1));
    }
  }

  @Nested
  public class MaxIdxChecks {

    @Test
    public void onlyPropagating() {
      RelationCodecDefault<?> codec = RelationCodecDefault.of(List.of(RelationTypes.isA(), RelationTypes.partOf()), INDEXER);
      assertThat(codec.maxIdx(), equalTo(3));
    }

    @Test
    public void onlyNonPropagating() {
      RelationCodecDefault<?> codec = RelationCodecDefault.of(List.of(SOME, OTHER), INDEXER);
      assertThat(codec.maxIdx(), equalTo(1));
    }

    @Test
    public void propagatingAndNonPropagating() {
      RelationCodecDefault<?> codec = RelationCodecDefault.of(List.of(RelationTypes.isA(), SOME, OTHER, RelationTypes.partOf()), INDEXER);
      assertThat(codec.maxIdx(), equalTo(5));
    }

    @Test
    public void empty() {
      RelationCodecDefault<?> codec = RelationCodecDefault.of(List.of(), INDEXER);
      assertThat(codec.maxIdx(), equalTo(-1));
    }
  }

  @Nested
  public class Others {
    private RelationCodecDefault<Byte> codec;

    @BeforeEach
    public void setUp() {
      codec = RelationCodecDefault.of(List.of(
        RelationTypes.isA(),
        SOME
      ), INDEXER);
    }

    @Test
    public void isSet() {
      assertThat(codec.isSet((byte) 0b01, RelationTypes.isA(), false), equalTo(true));
      assertThat(codec.isSet((byte) 0b10, RelationTypes.isA(), true), equalTo(true));
      assertThat(codec.isSet((byte) 0b10, RelationTypes.isA(), false), equalTo(false));
      assertThat(codec.isSet((byte) 0b01, RelationTypes.isA(), true), equalTo(false));

      assertThat(codec.isSet((byte) 0b001, SOME), equalTo(false));
      assertThat(codec.isSet((byte) 0b010, SOME), equalTo(false));
      assertThat(codec.isSet((byte) 0b100, SOME), equalTo(true));
    }
  }



}
