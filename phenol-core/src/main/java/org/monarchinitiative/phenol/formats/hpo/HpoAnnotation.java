package org.monarchinitiative.phenol.formats.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represent an HPO Term together with a Frequency and an Onset and modifiers. This is intended to
 * be used to represent a disease annotation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.3 (2018-03-12)
 */
public class HpoAnnotation {
  /** Note that we still do not have valid freuqency information for all of the annotations; the default
   * is to show "n/a"*/
  private final static String DEFAULT_FREQUENCY_STRING="n/a";
  /** The annotated {@link TermId}. */
  private final TermId termId;
  /** The frequency with which this phenotypic abnormality is seen in patients with this disease */
  private final double frequency;
  /** The frequency represented in String form intended for display */
  private final String frequencyString;
  /** The characteristic age of onset of a feature in a certain disease. */
  private final HpoOnset onset;
  /** List of modifiers of this annotation. List can be empty but cannot be null */
  private final List<TermId> modifiers;
  /** List of citations that support this annotation. */
  private final List<String> citations;

  /**
   * Constructor.
   *
   * @param termId Annotated {@link TermId}.
   * @param f The frequency the term is annotated with.
   */
  public HpoAnnotation(TermId termId, double f, String freqString,HpoOnset onset, List<TermId> modifiers, List<String> cites) {
    this.termId = termId;
    this.frequency = f;
    frequencyString=freqString;
    this.onset = onset;
    this.modifiers = modifiers;
    this.citations=cites;
  }

  public static HpoAnnotation forTerm(TermId t) {
    return new Builder(t).build();
  }

  public static HpoAnnotation parseTerm(String id) {
    TermId tid = TermId.constructWithPrefix(id);
    return forTerm(tid);
  }

  public String getFrequencyString() {
    return frequencyString;
  }

  public List<String> getCitations() {
    return citations;
  }

  /** @return The annotated {@link TermId}. */
  public TermId getTermId() {
    return termId;
  }

  /** @return The annotating {@link HpoFrequency}. */
  public double getFrequency() {
    return frequency;
  }

  public HpoOnset getOnset() {
    return onset;
  }

  public List<TermId> getModifiers() {
    return modifiers;
  }

  @Override
  public String toString() {
    return "HpoTermId [termId=" + termId + ", frequency=" + frequency + ", onset=" + onset + "]";
  }

  /**
   * Return the full term ID including prefix.
   *
   * @return The full HPO, id, e.g., HP:0000123.
   */
  public String getIdWithPrefix() {
    return this.termId.getIdWithPrefix();
  }

  /**
   * Objects are equal if the three components are equal. Note that the constructor guarantees that
   * the the TermId, the Frequency, and the Onset are not null.
   *
   * @param that object to be tested for equality
   * @return true if this is equal to that
   */
  @Override
  public boolean equals(Object that) {
    if (that == null) return false;
    if (!(that instanceof HpoAnnotation)) return false;
    HpoAnnotation otherHpoAnnotation = (HpoAnnotation) that;

    return termId.equals(otherHpoAnnotation.getTermId())
        && frequency == otherHpoAnnotation.getFrequency()
        && onset.equals(otherHpoAnnotation.getOnset())
        && modifiers.equals(otherHpoAnnotation.modifiers);
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + termId.hashCode();
    result = 31 * result + Double.valueOf(frequency).hashCode();
    result = 31 * result + onset.hashCode();
    result = 31 * result + modifiers.hashCode();
    return result;
  }

  /**
   * @param that The other HpoAnnotation that we are comparing with
   * @return sort order
   */
  public int compareTo(HpoAnnotation that) {
    final int BEFORE = -1;
    final int EQUAL = 0;
    final int AFTER = 1;
    if (this==that) return EQUAL;
    if (! this.termId.equals(that.termId)) {
     return this.termId.compareTo(that.termId);
    }
    if (this.frequency<that.frequency) return BEFORE;
    else if (that.frequency<this.frequency) return AFTER;
    if (! this.onset.equals(that.onset)) {
      return this.onset.compareTo(that.onset);
    }
    // for sorting purposes we do not care about the modifier list.
    return EQUAL;
  }

  public static Builder builder(TermId termId) {
    return new Builder(termId);
  }

  public static class Builder {
      /** If no information is available, then assume that the feature is always present! */
      private static final HpoFrequency DEFAULT_HPO_FREQUENCY = HpoFrequency.ALWAYS_PRESENT;
      /**
       * If no onset information is available, use the Onset term "Onset" (HP:0003674), which is the
       * root of the subontology for onset.
       */
      private static final HpoOnset DEFAULT_HPO_ONSET = HpoOnset.ONSET;
      /** The id of the HPO term that this annotation is reporting. */
      private final TermId termId;

      /** The frequency represented as a String and intended for display */
      private String frequencyString=DEFAULT_FREQUENCY_STRING;
      /** List of citations that support this annotation. */
      private List<String> citations=null;

    /** A frequency (proportion) represented as a double and intended for computation {@link HpoFrequency}. */
      private double frequency = DEFAULT_HPO_FREQUENCY.mean();
      /** The characteristic age of onset of a feature in a certain disease. */
      private HpoOnset onset = DEFAULT_HPO_ONSET;
      /** List of modifiers of this annotation. List can be empty but cannot be null */
      private List<TermId> modifierList = ImmutableList.of();

      public Builder(TermId tid) {
        Objects.requireNonNull(tid,"TermId cannot be null");
        this.termId = tid;
      }

      /**
       *
       * @param o HpoOnset term (can be null, in which case we use the default)
       * @return reference to this Builder object
       */
      public Builder onset(HpoOnset o) {
        if (o != null) {
          this.onset = o;
        }
        return this;
      }

      public Builder frequency(double f, String freqString) {
        checkBoundsWithinZeroToOne(f);
        this.frequency = f;
        this.frequencyString=freqString;
        return this;
      }

      private void checkBoundsWithinZeroToOne(double f) {
        try {
          assert f >= 0d && f <= 1d;
        } catch (AssertionError ex) {
          throw new IllegalArgumentException(f + " Frequency must be within range 0-1");
        }
      }

      public Builder modifiers(Collection<TermId> modifiers) {
        Objects.requireNonNull(modifiers,"modifiers cannot be null");
        this.modifierList = ImmutableList.copyOf(modifiers);
        return this;
      }

      public Builder citations(List<String> cites) {
        this.citations=cites;
        return this;
      }

      public HpoAnnotation build() {
        if (citations==null){
          citations=ImmutableList.of();
        }
        return new HpoAnnotation(termId, frequency, frequencyString,onset, modifierList,citations);
      }
  }
}
