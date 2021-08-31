package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represent an HPO Term together with a Frequency and an Onset and modifiers. This is intended to
 * be used to represent a disease annotation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.1.3 (2018-03-12)
 */
class HpoDiseaseAnnotationDefault implements HpoDiseaseAnnotation {

  // TODO - implement real comparator
  private static final Comparator<HpoDiseaseAnnotation> COMPARATOR = Comparator.comparing(HpoDiseaseAnnotation::termId);

  /** The annotated {@link TermId}. */
  private final TermId termId;

  private final Collection<HpoDiseaseAnnotationMetadata> onsets;

  static HpoDiseaseAnnotationDefault of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> onsets) {
    return new HpoDiseaseAnnotationDefault(termId, onsets);
  }

  private HpoDiseaseAnnotationDefault(TermId termId, Collection<HpoDiseaseAnnotationMetadata> onsets) {
    this.termId = termId;
    this.onsets = onsets;
  }

  /** @return The annotated {@link TermId}. */
  @Override
  public TermId termId() {
    return termId;
  }

  @Override
  public Collection<HpoDiseaseAnnotationMetadata> metadata() {
    return onsets;
  }

  @Override
  public int compareTo(HpoDiseaseAnnotation other) {
    return COMPARATOR.compare(this, other);
  }

  // TODO - there was some funky equality test here that omitted several fields. Was it for special purpose?
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationDefault that = (HpoDiseaseAnnotationDefault) o;
    return Objects.equals(termId, that.termId) && Objects.equals(onsets, that.onsets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(termId, onsets);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationDefault{" +
      "termId=" + termId +
      ", onsets=" + onsets +
      '}';
  }

  //  /**
//   * @param that The other HpoAnnotation that we are comparing with
//   * @return sort order
//   */
//  @Override
//  public int compareTo(HpoDiseaseFeatureImpl that) {
//    final int BEFORE = -1;
//    final int EQUAL = 0;
//    final int AFTER = 1;
//    if (this==that) return EQUAL;
//    if (! this.termId.equals(that.termId)) {
//     return this.termId.compareTo(that.termId);
//    }
//    if (this.frequency<that.frequency) return BEFORE;
//    else if (that.frequency<this.frequency) return AFTER;
//    if (! this.onset.equals(that.onset)) {
//      return this.onset.compareTo(that.onset);
//    }
//    // for sorting purposes we do not care about the modifier list.
//    return EQUAL;
//  }

//  public static Builder builder(TermId termId) {
//    return new Builder(termId);
//  }

//
//  public static class Builder {
//      /** If no information is available, then assume that the feature is always present! */
//      private static final HpoFrequency DEFAULT_HPO_FREQUENCY = HpoFrequency.ALWAYS_PRESENT;
//      /**
//       * If no onset information is available, use the Onset term "Onset" (HP:0003674), which is the
//       * root of the subontology for onset.
//       */
//      private static final HpoOnset DEFAULT_HPO_ONSET = HpoOnset.ONSET;
//
//      private static final EvidenceCode DEFAULT_EVIDENCE_CODE = EvidenceCode.IEA;
//      /** The id of the HPO term that this annotation is reporting. */
//      private final TermId termId;
//
//      /** The frequency represented as a String and intended for display */
//      private String frequencyString=DEFAULT_FREQUENCY_STRING;
//      /** List of citations that support this annotation. */
//      private List<String> citations=null;
//
//    /** A frequency (proportion) represented as a double and intended for computation {@link HpoFrequency}. */
//      private double frequency = DEFAULT_HPO_FREQUENCY.mean();
//      /** The characteristic age of onset of a feature in a certain disease. */
//      private HpoOnset onset = DEFAULT_HPO_ONSET;
//      /** Evidence for this annotation. */
//      private EvidenceCode evidence = DEFAULT_EVIDENCE_CODE;
//      /** List of modifiers of this annotation. List can be empty but cannot be null */
//      private List<TermId> modifierList = ImmutableList.of();
//
//      public Builder(TermId tid) {
//        Objects.requireNonNull(tid,"TermId cannot be null");
//        this.termId = tid;
//      }
//
//      /**
//       *
//       * @param o HpoOnset term (can be null, in which case we use the default)
//       * @return reference to this Builder object
//       */
//      public Builder onset(HpoOnset o) {
//        if (o != null) {
//          this.onset = o;
//        }
//        return this;
//      }
//
//      public Builder frequency(double f, String freqString) {
//        checkBoundsWithinZeroToOne(f);
//        this.frequency = f;
//        this.frequencyString=freqString;
//        return this;
//      }
//
//      private void checkBoundsWithinZeroToOne(double f) {
//        try {
//          assert f >= 0d && f <= 1d;
//        } catch (AssertionError ex) {
//          throw new IllegalArgumentException(f + " Frequency must be within range 0-1");
//        }
//      }
//
//      public Builder modifiers(Collection<TermId> modifiers) {
//        Objects.requireNonNull(modifiers,"modifiers cannot be null");
//        this.modifierList = ImmutableList.copyOf(modifiers);
//        return this;
//      }
//
//      public Builder citations(List<String> cites) {
//        this.citations=cites;
//        return this;
//      }
//      public Builder evidence(String evid) {
//        this.evidence = EvidenceCode.fromString(evid);
//        return this;
//      }
//
//      public HpoDiseaseFeature build() {
//        if (citations==null){
//          citations=ImmutableList.of();
//        }
//        return new HpoDiseaseFeatureImpl(termId, frequency, frequencyString,onset, modifierList,citations, evidence);
//      }
//  }
}
