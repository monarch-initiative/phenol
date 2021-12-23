package org.monarchinitiative.phenol.ontology.data;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import com.google.common.collect.ComparisonChain;

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
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof TestTermAnnotation)) {
      throw new PhenolRuntimeException("Cannot compare " + o + " to " + this);
    }
    TestTermAnnotation that = (TestTermAnnotation) o;

    return ComparisonChain.start()
        .compare(this.termId, that.termId)
        .compare(this.label, that.label)
        .result();
  }

  @Override
  public String toString() {
    return "TestTermAnnotation [termId=" + termId + ", label=" + label + "]";
  }
}
