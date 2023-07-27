package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Relationship;

import java.util.Map;
import java.util.Optional;

interface RelationshipContainer {

  Optional<Relationship> relationshipById(int relationshipId);

  Map<Integer, Relationship> getRelationMap();

}
