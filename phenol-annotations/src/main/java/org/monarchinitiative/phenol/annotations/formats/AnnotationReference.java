package org.monarchinitiative.phenol.annotations.formats;

import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;

public class AnnotationReference implements Identified {

  private final TermId id;
  private final EvidenceCode evidenceCode;

  public static AnnotationReference of(TermId id, EvidenceCode evidenceCode) {
    return new AnnotationReference(id, evidenceCode);
  }

  private AnnotationReference(TermId id, EvidenceCode evidenceCode) {
    this.id = Objects.requireNonNull(id);
    this.evidenceCode = Objects.requireNonNull(evidenceCode);
  }

  @Override
  public TermId id() {
    return id;
  }

  public EvidenceCode evidenceCode() {
    return evidenceCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnnotationReference that = (AnnotationReference) o;
    return Objects.equals(id, that.id) && evidenceCode == that.evidenceCode;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, evidenceCode);
  }

  @Override
  public String toString() {
    return "AnnotationReference{" +
      "id=" + id +
      ", evidenceCode=" + evidenceCode +
      '}';
  }
}
