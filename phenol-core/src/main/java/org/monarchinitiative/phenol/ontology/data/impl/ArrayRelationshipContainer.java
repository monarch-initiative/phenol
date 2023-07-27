package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Relationship;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class ArrayRelationshipContainer implements RelationshipContainer {

  private volatile Map<Integer, Relationship> relationshipMap;
  private final Relationship[] relationships;
  private final int min;

  static ArrayRelationshipContainer of(Collection<Relationship> relationships) {
    if (relationships.isEmpty())
      throw new IllegalArgumentException("Relationships must not empty!");
    List<Relationship> sorted = relationships.stream()
      .sorted(Comparator.comparingInt(Relationship::getId))
      .collect(Collectors.toList());
    Set<Integer> offenders = new HashSet<>();
    int min = Integer.MAX_VALUE;
    int previous = -1;
    for (Relationship relationship : sorted) {
      // Check that the relationship IDs are unique. We use `==` instead of `<=`
      // since the relationships were just sorted.
      int current = relationship.getId();
      if (previous == current)
        offenders.add(previous);
      min = Math.min(current, min);
      previous = current;
    }
    if (!offenders.isEmpty()) {
      throw new IllegalArgumentException(
        String.format("Relationships do not have unique IDs. The following IDs occurred >1 times: %s",
          offenders.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]")))
      );
    }

    int capacity = previous - min;
    Relationship[] a = new Relationship[capacity + 1]; // +1 because of 0-based indexing.
    for (Relationship relationship : sorted) {
      a[relationship.getId() - min] = relationship;
    }

    return new ArrayRelationshipContainer(a, min);
  }

  ArrayRelationshipContainer(Relationship[] relationships, int min) {
    this.relationships = relationships;
    this.min = min;
  }

  @Override
  public Optional<Relationship> relationshipById(int relationshipId) {
    if (relationshipId < 0)
      throw new IllegalArgumentException(String.format("Relationship ID (%d) must not be negative!", relationshipId));
    int query = relationshipId - min;
    return 0 > query || query >= relationships.length // bound check
      ? Optional.empty()
      : Optional.ofNullable(relationships[query]);
  }

  @Override
  public Map<Integer, Relationship> getRelationMap() {
    // The function is deprecated in MinimalOntology, so we implement it in an expensive fashion until it is removed in
    // v3.0.0.
    if (relationshipMap == null) {
      synchronized (this) {
        if (relationshipMap == null)
          relationshipMap = Arrays.stream(relationships)
            .collect(Collectors.toMap(Relationship::getId, Function.identity()));
      }
    }

    return relationshipMap;
  }
}
