package org.monarchinitiative.phenol.graph;

import java.util.Objects;

class RelationTypeDefault implements RelationType {

  private final String id, label;
  private final boolean propagates;

  RelationTypeDefault(String id, String label, boolean propagates) {
    this.id = id;
    this.label = label;
    this.propagates = propagates;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String label() {
    return label;
  }

  @Override
  public boolean propagates() {
    return propagates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RelationTypeDefault that = (RelationTypeDefault) o;
    return propagates == that.propagates && Objects.equals(id, that.id) && Objects.equals(label, that.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, propagates);
  }

  @Override
  public String toString() {
    return "RelationTypeDefault{" +
      "id='" + id + '\'' +
      ", label='" + label + '\'' +
      ", propagates=" + propagates +
      '}';
  }
}
