package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolException;
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

  public MpAnnotation merge(MpAnnotation other) throws PhenolException {
    if (! this.termId.equals(other.termId))
      throw new PhenolException(String.format("Attempt to merge annotations with distinct MP ids [%s/%s]",
        this.termId.getIdWithPrefix(),other.termId.getIdWithPrefix()));
    if (! this.negated == other.negated)
      throw new PhenolException("Attempt to merge annotations only one of which is negated: "+termId.getIdWithPrefix());
    // merge list of modifiers
    List<MpModifier> modlist = new ArrayList<>();
    modlist.addAll(getModifers());
    for (MpModifier mod: other.getModifers()) {
      if (!modlist.contains(mod)) {
        modlist.add(mod);
      }
    }
    // merge PMIDs
    List<String> pmidList=new ArrayList<>();
    pmidList.addAll(getPmidList());
    for (String pmid: other.getPmidList()) {
      if (! pmidList.contains(pmid)) {
        pmidList.add(pmid);
      }
    }
    return new MpAnnotation(this.termId,ImmutableList.copyOf(pmidList),ImmutableList.copyOf(modlist),negated);
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
