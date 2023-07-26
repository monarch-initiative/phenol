package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Relationship;

import java.util.Map;
import java.util.Optional;

class MapRelationshipContainer implements RelationshipContainer {

  private final Map<Integer, Relationship> relationshipMap;

  MapRelationshipContainer(Map<Integer, Relationship> relationshipMap) {
    this.relationshipMap = relationshipMap;
  }

  @Override
  public Optional<Relationship> relationshipById(int relationshipId) {
    return Optional.ofNullable(relationshipMap.get(relationshipId));
  }

  @Override
  public Map<Integer, Relationship> getRelationMap() {
    return relationshipMap;
  }
}
