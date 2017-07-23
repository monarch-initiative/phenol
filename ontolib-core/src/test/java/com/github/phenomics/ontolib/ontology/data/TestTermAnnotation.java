package com.github.phenomics.ontolib.ontology.data;

import com.github.phenomics.ontolib.base.OntoLibRuntimeException;
import com.github.phenomics.ontolib.ontology.data.TermAnnotation;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ComparisonChain;

public class TestTermAnnotation implements TermAnnotation {

  private static final long serialVersionUID = 1L;

  private TermId termId;
  private String label;

  public TestTermAnnotation(TermId termId, String label) {
    this.termId = termId;
    this.label = label;
  }

  @Override
  public TermId getTermId() {
    return termId;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof TestTermAnnotation)) {
      throw new OntoLibRuntimeException("Cannot compare " + o + " to " + this);
    }
    TestTermAnnotation that = (TestTermAnnotation) o;

    return ComparisonChain.start().compare(this.termId, that.termId).compare(this.label, that.label)
        .result();
  }

  @Override
  public String toString() {
    return "TestTermAnnotation [termId=" + termId + ", label=" + label + "]";
  }

}
