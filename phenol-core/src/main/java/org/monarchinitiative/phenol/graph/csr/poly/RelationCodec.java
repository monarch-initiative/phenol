package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.graph.RelationType;

import java.util.Collection;

interface RelationCodec<E> {

  static <E> RelationCodec<E> of(Collection<RelationType> relationTypes,
                                DataIndexer<E> indexer) {
    return RelationCodecDefault.of(relationTypes, indexer);
  }

  int calculateBitIndex(RelationType relationType, boolean inverted);

  default int calculateBitIndex(RelationType relationType) {
    return calculateBitIndex(relationType, false);
  }

  boolean isSet(E value, RelationType relationType, boolean inverted);

  default boolean isSet(E value, RelationType relationType) {
    return isSet(value, relationType, false);
  }

  int maxIdx();

}
