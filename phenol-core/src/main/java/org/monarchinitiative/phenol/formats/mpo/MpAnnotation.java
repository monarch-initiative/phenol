package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class that encapsulates the annotation of a MGI model with MP terms, including
 * PubMed references modifiers, and negation options.
 */
public final class MpAnnotation {
  /** The annotated {@link TermId}. */
  private final TermId termId;
  /** List of PubMed ids for this phenotype annotation. */
  private final List<String> pmidList;
  /** List of modifiers for the current phenotype annotation */
  private final List<MpModifier> modifers;

  /**
   * Constructor is intended to be used with the Builder, which ensures that the Lists
   * are immutable.
   * @param tid TermId of the MP term in this anotation
   * @param pmids List of pubmed ids supporting this annotation
   * @param mods Modifiers (e.g., sexSpecific- or sexSpecificNormal) for this annotation
   */
  private MpAnnotation(TermId tid, List<String> pmids, List<MpModifier> mods) {
    this.termId = tid;
    this.pmidList = pmids;
    modifers = mods;
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
  /** @return true if this phenotype was excluded in all models for males. */
  public boolean maleSpecificNormal() {return modifers.stream().allMatch(MpModifier::maleSpecificNormal); }
  /** @return true if this phenotype was excluded in all models for females. */
  public boolean femaleSpecificNormal() {return modifers.stream().allMatch(MpModifier::femaleSpecificNormal); }
  /** @return true if this phenotype was excluded in all models for males or females or both. */
  public boolean sexSpecificNormal() { return ( maleSpecificNormal() || femaleSpecificNormal() ); }


  public TermId getTermId() {
    return termId;
  }

  public List<String> getPmidList() {
    return pmidList;
  }
  /** @return list of modifiers for this annotation. May be empty but cannot be null. */
  public List<MpModifier> getModifers() {
    return modifers;
  }

//  public boolean isNegated() {
//    return negated;
//  }

  /**
   * Merge the contents of two annotation to the same MP term with different metadata
   * @param annot1 The first MP annotation
   * @param annot2 The second MP annotation
   * @return The merged MP annotation
   * @throws PhenolException if annot1 and annot2 are incompatible
   */
  public static MpAnnotation merge(MpAnnotation annot1, MpAnnotation annot2) throws PhenolException {
    if (! annot1.termId.equals(annot2.termId)) {
      // should never happen!
      throw new PhenolException(String.format("Attempt to merge annotations with distinct MP ids [%s/%s]",
        annot1.termId.getValue(), annot2.termId.getValue()));
    }
       // merge list of modifiers
    List<MpModifier> modlist = new ArrayList<>(annot1.getModifers());
    for (MpModifier mod: annot2.getModifers()) {
      if (!modlist.contains(mod)) {
        modlist.add(mod);
      }
    }
    // merge PMIDs
    List<String> pmidList=new ArrayList<>(annot1.getPmidList());
    for (String pmid: annot2.getPmidList()) {
      if (! pmidList.contains(pmid)) {
        pmidList.add(pmid);
      }
    }
    return new MpAnnotation(annot1.termId,ImmutableList.copyOf(pmidList),ImmutableList.copyOf(modlist));
  }

  @Override
  public String toString() {
    return termId.getValue() +
       modifers.stream().map(MpModifier::getType).map(MpModifierType::toString).collect(Collectors.joining("; "));
  }

  public static class Builder {

    private final TermId termId;

    private final List<String> pmidList;

    private List<MpModifier> modifers;


    public Builder(TermId tid, List<String> pmids) {
      Objects.requireNonNull(tid, "TermId cannot be null");
      this.termId = tid;
      this.pmidList = pmids;
      modifers = new ArrayList<>();
    }

    public Builder sexSpecific(MpSex sex) {
      if (sex.equals(MpSex.FEMALE)) {
        modifers.add(new MpModifier(MpModifierType.FEMALE_SPECIFIC));
      } else if (sex.equals(MpSex.MALE)) {
        modifers.add(new MpModifier(MpModifierType.MALE_SPECIFIC));
      }
      return this;
    }

    public Builder sexSpecificNormal(MpSex sex) {
      if (sex.equals(MpSex.FEMALE)) {
        modifers.add(new MpModifier(MpModifierType.FEMALE_SPECIFIC_NORMAL));
      } else if (sex.equals(MpSex.MALE)) {
        modifers.add(new MpModifier(MpModifierType.MALE_SPECIFIC_NORMAL));
      }
      return this;
    }

    public MpAnnotation build() {
      return new MpAnnotation(this.termId,
        ImmutableList.copyOf(this.pmidList),
        ImmutableList.copyOf(this.modifers));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MpAnnotation that = (MpAnnotation) o;

    if (!termId.equals(that.termId)) return false;
    if (pmidList != null ? !pmidList.equals(that.pmidList) : that.pmidList != null) return false;
    return modifers.equals(that.modifers);
  }

  @Override
  public int hashCode() {
    int result = termId.hashCode();
    result = 31 * result + (pmidList != null ? pmidList.hashCode() : 0);
    result = 31 * result + modifers.hashCode();
    return result;
  }
}
