package org.monarchinitiative.phenol.ontology.testdata.hpo;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

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
    public TermId getItemId() {
      return label;
    }

    @Override
    public int compareTo(TermAnnotation o) {
      if (!(o instanceof ToyHpoAnnotation)) {
        throw new RuntimeException("Cannot compare " + o + " to " + this);
      }
      ToyHpoAnnotation that = (ToyHpoAnnotation) o;

      int result = termId.compareTo(that.termId);
      if (result != 0) return result;
      return label.compareTo(that.label);
    }

    @Override
    public String toString() {
      return "VegetableTermAnnotation [termId=" + termId + ", label=" + label + "]";
    }
}
