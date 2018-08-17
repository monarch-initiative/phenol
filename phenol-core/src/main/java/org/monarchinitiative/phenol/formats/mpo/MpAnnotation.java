package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;

public class MpAnnotation {
  /** The annotated {@link TermId}. */
  private final TermId termId;

  private final List<String> pmidList;


  public MpAnnotation(TermId tid) {
    this.termId=tid;
    pmidList=ImmutableList.of();
  }

  public MpAnnotation(TermId tid, List<String> pmids){
    this.termId=tid;
    this.pmidList=pmids;
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
