package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.graph.RelationType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class RelationCodecDefault<E> implements RelationCodec<E> {

  public static <E> RelationCodecDefault<E> of(Collection<RelationType> relationTypes,
                                               DataIndexer<E> indexer) {
    List<RelationType> propagating = relationTypes.stream()
      .filter(RelationType::propagates)
      .collect(Collectors.toList());
    int nPropagating = propagating.size();

    // Insert propagating relations.
    Map<RelationType, Integer> relationMap = new HashMap<>();
    for (int i = 0; i < propagating.size(); i++) {
      RelationType relationType = propagating.get(i);
      relationMap.put(relationType, i * 2);
    }

    AtomicInteger np = new AtomicInteger(nPropagating * 2);
    relationTypes.stream()
      .filter(Predicate.not(RelationType::propagates))
      .forEach(rt -> relationMap.put(rt, np.getAndIncrement()));

    // we subtract one to counter the effect of the last `np.getAndIncrement()`
    return new RelationCodecDefault<>(relationMap, np.get() -1, indexer);
  }

  private final Map<RelationType, Integer> relationMap;
  private final int maxIndex;
  private final DataIndexer<E> indexer;

  private RelationCodecDefault(Map<RelationType, Integer> relationMap,
                               int maxIndex,
                               DataIndexer<E> indexer) {
    this.relationMap = new ConcurrentHashMap<>(relationMap);
    this.maxIndex = maxIndex;
    this.indexer = indexer;
  }

  /**
   * @return bit index for a known/managed relation type or {@code -1} for non-managed relation type.
   */
  @Override
  public int calculateBitIndex(RelationType relationType, boolean inverted) {
    Integer idx = relationMap.getOrDefault(relationType, -1);
    if (idx < 0)
      return idx;

    if (relationType.propagates()) {
      return inverted
        ? idx + 1
        : idx;
    } else
      return idx;
  }

  @Override
  public boolean isSet(E value, RelationType relationType, boolean inverted) {
    int idx = calculateBitIndex(relationType, inverted);
    return indexer.isSet(value, idx);
  }

  @Override
  public int maxIdx() {
    return maxIndex;
  }
}
