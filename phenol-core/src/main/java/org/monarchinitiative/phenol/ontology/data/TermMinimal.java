package org.monarchinitiative.phenol.ontology.data;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Minimal {@linkplain Term} representation.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class TermMinimal implements Term {

  private final TermId id;
  private final String name;

  TermMinimal(TermId id, String name) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public List<TermId> getAltTermIds() {
    return List.of();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDefinition() {
    return "";
  }

  @Override
  public List<SimpleXref> getDatabaseXrefs() {
    return List.of();
  }

  @Override
  public List<SimpleXref> getPmidXrefs() {
    return List.of();
  }

  @Override
  public String getComment() {
    return "";
  }

  @Override
  public List<String> getSubsets() {
    return List.of();
  }

  @Override
  public List<TermSynonym> getSynonyms() {
    return List.of();
  }

  @Override
  public boolean isObsolete() {
    return false;
  }

  @Override
  public String getCreatedBy() {
    return "";
  }

  @Override
  public Optional<Date> getCreationDate() {
    return Optional.empty();
  }

  @Override
  public List<Dbxref> getXrefs() {
    return List.of();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TermMinimal that = (TermMinimal) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "TermMinimal{" +
      "id=" + id +
      ", name='" + name + '\'' +
      '}';
  }
}
