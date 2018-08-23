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
  /** If this is true, then the model is NOT characterized by this phenotype. */
  private final boolean negated;


  /**
   * Constructor is intended to be used with the Builder, which ensures that the Lists
   * are immutable.
   * @param tid TermId of the MP term in this anotation
   * @param pmids List of pubmed ids supporting this annotation
   * @param mods Modifiers (e.g., sex-specific) for this annotation
   * @param neg this is true is the annotated is negated (e.g., NOT inflammed).
   */
  private MpAnnotation(TermId tid, List<String> pmids, List<MpModifier> mods, boolean neg) {
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

  /**
   * Merge the contents of two annotation to the same MP term with differnet metadata
   * @param annot1 The first MP annotation
   * @param annot2 The second MP annotation
   * @return The merged MP annotation
   * @throws PhenolException if annot1 and annot2 are incompatible
   */
  public static MpAnnotation merge(MpAnnotation annot1, MpAnnotation annot2) throws PhenolException {
    if (! annot1.termId.equals(annot2.termId))
      throw new PhenolException(String.format("Attempt to merge annotations with distinct MP ids [%s/%s]",
        annot1.termId.getIdWithPrefix(),annot2.termId.getIdWithPrefix()));
    if (! annot1.negated == annot2.negated)
      throw new PhenolException("Attempt to merge annotations only one of which is negated: "+annot1.termId.getIdWithPrefix());
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
    return new MpAnnotation(annot1.termId,ImmutableList.copyOf(pmidList),ImmutableList.copyOf(modlist),annot1.negated);
  }

  @Override
  public String toString() {
    return negated ? "NOT " : "" +
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
      return new MpAnnotation(this.termId,
        ImmutableList.copyOf(this.pmidList),
        ImmutableList.copyOf(this.modifers),
        this.negated);
    }
  }

}
