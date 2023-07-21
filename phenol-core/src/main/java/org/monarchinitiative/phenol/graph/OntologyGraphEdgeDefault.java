package org.monarchinitiative.phenol.graph;

import java.util.Objects;

class OntologyGraphEdgeDefault<T> implements OntologyGraphEdge<T> {

  private final T subject, object;
  private final RelationType relationshipType;

  OntologyGraphEdgeDefault(T subject,
                           T object,
                           RelationType relationshipType) {
    this.subject = Objects.requireNonNull(subject);
    this.object = Objects.requireNonNull(object);
    this.relationshipType = Objects.requireNonNull(relationshipType);
  }

  @Override
  public T subject() {
    return subject;
  }

  @Override
  public T object() {
    return object;
  }

  @Override
  public RelationType relationType() {
    return relationshipType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OntologyGraphEdgeDefault<?> that = (OntologyGraphEdgeDefault<?>) o;
    return Objects.equals(subject, that.subject) && Objects.equals(object, that.object) && Objects.equals(relationshipType, that.relationshipType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, object, relationshipType);
  }

  @Override
  public String toString() {
    return "OntologyGraphEdgeDefault{" +
      "subject=" + subject +
      ", object=" + object +
      ", relationshipType=" + relationshipType +
      '}';
  }
}
