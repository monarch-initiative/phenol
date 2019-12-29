package org.monarchinitiative.phenol.annotations.formats.mpo;

import com.google.common.collect.ImmutableSet;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that encapsulates the annotation of a MGI model with MP terms, including
 * PubMed references modifiers, and negation options.
 */
public final class MpAnnotation {
  /** The annotated {@link TermId}. */
  private final TermId termId;
  /** List of PubMed ids for this phenotype annotation. */
  private final Set<String> pmidList;
  /** List of modifiers for the current phenotype annotation */
  private final Set<MpModifier> modifiers;

  /**
   * Constructor is intended to be used with the Builder, which ensures that the Lists
   * are immutable.
   * @param tid TermId of the MP term in this anotation
   * @param pmids List of pubmed ids supporting this annotation
   * @param mods Modifiers (e.g., sexSpecific- or sexSpecificNormal) for this annotation
   */
  private MpAnnotation(TermId tid, Set<String> pmids, Set<MpModifier> mods) {
    this.termId = tid;
    this.pmidList = pmids;
    modifiers = mods;
  }
  /** @return true if one of more modifiers indicates a male-specific phenotype. */
  public boolean maleSpecificAbnormal() {
    return modifiers.stream().anyMatch(MpModifier::maleSpecific);
  }
  /** @return true if one of more modifiers indicates a female-specific phenotype. */
  public boolean femaleSpecificAbnormal() {
    return modifiers.stream().anyMatch(MpModifier::femaleSpecific);
  }
  /** @return true if one of more modifiers indicates a female- or male-specific phenotype. */
  public boolean sexSpecific() {
    return modifiers.stream().anyMatch(MpModifier::sexSpecific);
  }

  public TermId getTermId() {
    return termId;
  }

  public Set<String> getPmidSet() {
    return pmidList;
  }
  /** @return list of modifiers for this annotation. May be empty but cannot be null. */
  public Set<MpModifier> getModifiers() {
    return modifiers;
  }


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
    // merge PMIDs
    ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
    builder.addAll(annot1.getPmidSet());
    builder.addAll(annot2.getPmidSet());
    // merge modifiers
    ImmutableSet.Builder<MpModifier> modbuilder = new ImmutableSet.Builder<>();
    modbuilder.addAll(annot1.getModifiers());
    modbuilder.addAll(annot2.getModifiers());
    return new MpAnnotation(annot1.termId, builder.build(),modbuilder.build());
  }

  @Override
  public String toString() {
    return termId.getValue() +
       modifiers.stream().map(MpModifier::getType).map(MpModifierType::toString).collect(Collectors.joining("; "));
  }

  public static class Builder {

    private final TermId termId;

    private final Set<String> pmidList;

    private Set<MpModifier> modifers;


    public Builder(TermId tid, Set<String> pmids) {
      Objects.requireNonNull(tid, "TermId cannot be null");
      this.termId = tid;
      this.pmidList = pmids;
      modifers = new HashSet<>();
    }

    public Builder sexSpecific(MpSex sex) {
      if (sex.equals(MpSex.FEMALE)) {
        modifers.add(new MpModifier(MpModifierType.FEMALE_SPECIFIC_ABNORMAL));
      } else if (sex.equals(MpSex.MALE)) {
        modifers.add(new MpModifier(MpModifierType.MALE_SPECIFIC_ABNORMAL));
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

    public Builder modifiers(Set<MpModifier> modset) {
      this.modifers=modset;
      return this;
    }

    public MpAnnotation build() {
      return new MpAnnotation(this.termId,
        ImmutableSet.copyOf(this.pmidList),
        ImmutableSet.copyOf(this.modifers));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MpAnnotation that = (MpAnnotation) o;

    if (!termId.equals(that.termId)) return false;
    if (pmidList != null ? !pmidList.equals(that.pmidList) : that.pmidList != null) return false;
    return modifiers.equals(that.modifiers);
  }

  @Override
  public int hashCode() {
    int result = termId.hashCode();
    result = 31 * result + (pmidList != null ? pmidList.hashCode() : 0);
    result = 31 * result + modifiers.hashCode();
    return result;
  }
}
