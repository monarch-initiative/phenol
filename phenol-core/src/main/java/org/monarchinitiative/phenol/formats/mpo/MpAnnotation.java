package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;

public class MpAnnotation {
  /** The annotated {@link TermId}. */
  private final TermId termId;


  private MpAnnotation(TermId tid) {
    this.termId=tid;
  }




  public static class Builder {

    private final TermId termId;

    public Builder(TermId tid) {
      Objects.requireNonNull(tid, "TermId cannot be null");
      this.termId = tid;
    }


    public MpAnnotation build() {
      return new MpAnnotation(this.termId);
    }


  }


  }
