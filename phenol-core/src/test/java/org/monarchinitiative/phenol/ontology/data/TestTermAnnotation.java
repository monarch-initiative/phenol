package org.monarchinitiative.phenol.ontology.data;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import java.util.Objects;

public class TestTermAnnotation implements TermAnnotation {

  private static final long serialVersionUID = 1L;

  private final TermId termId;
  private final TermId label;

  public TestTermAnnotation(TermId termId, TermId label) {
    this.termId = termId;
    this.label = label;
  }

  @Override
  public TermId getTermId() {
    return termId;
  }

  @Override
  public TermId getItemId() {
    return label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TestTermAnnotation that = (TestTermAnnotation) o;
    return Objects.equals(termId, that.termId) && Objects.equals(label, that.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(termId, label);
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof TestTermAnnotation)) {
      throw new PhenolRuntimeException("Cannot compare " + o + " to " + this);
    }
    TestTermAnnotation that = (TestTermAnnotation) o;

    int result = termId.compareTo(that.termId);
    if (result != 0) return result;
    return label.compareTo(that.label);
  }

  @Override
  public String toString() {
    return "TestTermAnnotation [termId=" + termId + ", label=" + label + "]";
  }
}
