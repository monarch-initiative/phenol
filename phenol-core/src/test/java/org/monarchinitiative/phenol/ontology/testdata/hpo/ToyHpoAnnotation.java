package org.monarchinitiative.phenol.ontology.testdata.hpo;

import com.google.common.collect.ComparisonChain;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import javax.annotation.Nonnull;

public class ToyHpoAnnotation implements TermAnnotation {

    private static final long serialVersionUID = 1L;

    private final TermId termId;
    private final TermId label;

  public ToyHpoAnnotation(TermId termId, String label) {
      this.termId = termId;
      this.label = TermId.of("VEG", label);
    }

    @Override
    public TermId getTermId() {
      return termId;
    }

    @Override
    public TermId getLabel() {
      return label;
    }

    @Override
    public int compareTo(@Nonnull TermAnnotation o) {
      if (!(o instanceof ToyHpoAnnotation)) {
        throw new RuntimeException("Cannot compare " + o + " to " + this);
      }
      ToyHpoAnnotation that = (ToyHpoAnnotation) o;

      return ComparisonChain.start()
        .compare(this.termId, that.termId)
        .compare(this.label, that.label)
        .result();
    }

    @Override
    public String toString() {
      return "VegetableTermAnnotation [termId=" + termId + ", label=" + label + "]";
    }
}
