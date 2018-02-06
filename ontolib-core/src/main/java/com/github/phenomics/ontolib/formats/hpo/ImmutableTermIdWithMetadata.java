package com.github.phenomics.ontolib.formats.hpo;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermPrefix;
import com.google.common.collect.ComparisonChain;

/**
 * Represent an HPO Term together with a Frequency and an Onset. This is intended to be used to represent a disease
 * annotation.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.0.2 (2017-11-24)
 */
public class ImmutableTermIdWithMetadata implements TermIdWithMetadata {
    /** The annotated {@link TermId}. */
    private final TermId termId;

    /** The {@link HpoFrequency}. */
    private final HpoFrequency frequency;
    /** The characteristic age of onset of a feature in a certain disease. */
    private final HpoOnset onset;
    /** If no information is available, then assume that the feature is always present! */
    private static final HpoFrequency DEFAULT_HPO_FREQUENCY= HpoFrequency.ALWAYS_PRESENT;
    /** If no onset information is available, use the Onset term "Onset" (HP:0003674), which is the root of the subontology for onset. */
    private static final HpoOnset DEFAULT_HPO_ONSET=HpoOnset.ONSET;


    /**
     * Constructor.
     *
     * @param termId Annotated {@link TermId}.
     * @param frequency That the term is annotated with.
     */
    public ImmutableTermIdWithMetadata(TermId termId, HpoFrequency frequency, HpoOnset onset) {
        this.termId = termId;
        this.frequency = frequency!=null?frequency:DEFAULT_HPO_FREQUENCY;
        this.onset=onset!=null?onset:DEFAULT_HPO_ONSET;
    }


    public ImmutableTermIdWithMetadata(TermId t) {
        this.termId=t;
        this.frequency = DEFAULT_HPO_FREQUENCY;
        this.onset=DEFAULT_HPO_ONSET;
    }

    public ImmutableTermIdWithMetadata(TermPrefix prefix, String id) {
        this.termId=new ImmutableTermId(prefix,id);
        this.frequency = DEFAULT_HPO_FREQUENCY;
        this.onset=DEFAULT_HPO_ONSET;
    }



    /**
     * @return The annotated {@link TermId}.
     */
    public TermId getTermId() {
        return termId;
    }

    /**
     * @return The annotating {@link HpoFrequency}.
     */
    public HpoFrequency getFrequency() {
        return frequency;
    }


    public HpoOnset getOnset() { return onset;    }

    @Override
    public String toString() {
        return "TermIdWithMetadata [termId=" + termId + ", frequency=" + frequency + ", onset="+ onset + "]";
    }

    /**
     * Return the full term ID including prefix.
     *
     * @return The full Id.
     */
    @Override
    public String getIdWithPrefix() {
        return this.termId.getIdWithPrefix();
    }

    @Override
    public TermPrefix getPrefix() { return this.termId.getPrefix();}


    @Override
    public String getId() { return this.termId.getId();}

    /** Objects are equal if the three components are equal.
     * Note that the constructor guarantees that the the TermId, the Frequency, and the Onset are not null.
     * @param that object to be tested for equality
     * @return true if this is equal to that
     */
    @Override
    public boolean equals(Object that) {
        if (that==null) return false;
        if (! (that instanceof TermIdWithMetadata)) return false;
        TermIdWithMetadata otherTIDM = (TermIdWithMetadata) that;

        return termId.getId().equals(otherTIDM.getId()) &&
                frequency.equals(otherTIDM.getFrequency()) &&
                onset.equals(otherTIDM.getOnset());

    }

    @Override
    public int hashCode() {
      int result = 17;
      result = 31 * result + termId.hashCode();
      result = 31 * result + frequency.hashCode();
      result = 31 * result + onset.hashCode();
      return result;
    }

  /** There are issues with compareTo in inheritance hierarchies. We think that the
   * attributes of the TermId are the only essential attributes for sorting. In case
   * of equality, we can show the ImmutableTermWithMetadata first, otherwise don't
   * care (return 0).
   * @param that The other TermId that we are comparing with
   * @return sort order
   */
  @Override
  public int compareTo(TermId that) {
    int c = ComparisonChain.start().compare(this.getPrefix(), that.getPrefix())
      .compare(this.getId(), that.getId()).result();
    if (c!=0) return c;
    else if (this.frequency!=null || this.onset!=null) {
      return 1;
    } else {
      return 0;
    }
  }



}
