package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MpAnnotation {
  /**
   * The annotated {@link TermId}.
   */
  private final TermId termId;

  private final List<String> pmidList;

  private final List<MpModifier> modifers;

  private final boolean negated;


  public MpAnnotation(TermId tid) {
    this(tid, ImmutableList.of(), ImmutableList.of(), false);
  }

  public MpAnnotation(TermId tid, List<String> pmids) {
    this(tid, pmids, ImmutableList.of(), false);
  }

  public MpAnnotation(TermId tid, List<String> pmids, List<MpModifier> mods, boolean neg) {
    this.termId = tid;
    this.pmidList = pmids;
    modifers = mods;
    negated = neg;
  }

  public boolean maleSpecific() {
    return modifers.stream().anyMatch(MpModifier::maleSpecific);
  }

  public boolean femaleSpecific() {
    return modifers.stream().anyMatch(MpModifier::femaleSpecific);
  }

  public boolean sexSpecific() {
    return modifers.stream().anyMatch(MpModifier::sexSpecific);
  }


  public TermId getTermId() {
    return termId;
  }

  public List<String> getPmidList() {
    return pmidList;
  }

  public List<MpModifier> getModifers() {
    return modifers;
  }

  public boolean isNegated() {
    return negated;
  }

  @Override
  public String toString() {
    return negated ? "NOT" : "" +
      termId.getIdWithPrefix() +
      modifers.stream().map(MpModifier::getType).map(ModifierType::toString).collect(Collectors.joining("; "));
  }

  public static class Builder {

    private final TermId termId;

    private final List<String> pmidList;

    private List<MpModifier> modifers;

    private boolean negated = false;

    public Builder(TermId tid, List<String> pmids) {
      Objects.requireNonNull(tid, "TermId cannot be null");
      this.termId = tid;
      this.pmidList = pmids;
      modifers = new ArrayList<>();
    }

    public Builder sex(MpSex sex) {
      if (sex.equals(MpSex.FEMALE)) {
        modifers.add(new MpModifier(ModifierType.FEMALE_SPECIFIC));
      } else if (sex.equals(MpSex.MALE)) {
        modifers.add(new MpModifier(ModifierType.MALE_SPECIFIC));
      }
      return this;
    }

    public Builder negated(boolean neg) {
      this.negated = neg;
      return this;
    }


    public MpAnnotation build() {
      return new MpAnnotation(this.termId,this.pmidList,this.modifers,this.negated);
    }
  }

}
