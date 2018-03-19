package org.monarchinitiative.phenol.formats.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
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
  private static final long serialVersionUID = 2L;

  /** The annotated {@link TermId}. */
  private final TermId termId;

  /** The {@link HpoFrequency}. */
  private final double frequency;
  /** The characteristic age of onset of a feature in a certain disease. */
  private final HpoOnset onset;
  /** List of modifiers of this annotation. List can be empty but cannot be null */
  private final List<TermId> modifiers;

  /** If no information is available, then assume that the feature is always present! */
  private static final HpoFrequency DEFAULT_HPO_FREQUENCY = HpoFrequency.ALWAYS_PRESENT;
  /**
   * If no onset information is available, use the Onset term "Onset" (HP:0003674), which is the
   * root of the subontology for onset.
   */
  private static final HpoOnset DEFAULT_HPO_ONSET = HpoOnset.ONSET;

  /**
   * Constructor.
   *
   * @param termId Annotated {@link TermId}.
   * @param f The frequency the term is annotated with.
   */
  private HpoAnnotation(TermId termId, double f, HpoOnset onset, List<TermId> modifiers) {
    this.termId = termId;
    this.frequency = f;
    this.onset = onset != null ? onset : DEFAULT_HPO_ONSET;
    this.modifiers = modifiers;
  }

  public static HpoAnnotation forTerm(TermId t) {
    return new HpoAnnotation(t,DEFAULT_HPO_FREQUENCY.mean(),DEFAULT_HPO_ONSET,ImmutableList.of());
  }

  public static HpoAnnotation parseTerm(String id) {
    return new HpoAnnotation(ImmutableTermId.constructWithPrefix(id),DEFAULT_HPO_FREQUENCY.mean(),DEFAULT_HPO_ONSET,ImmutableList.of());
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
    private final TermId termId;

    /** The {@link HpoFrequency}. */
    private double frequency = DEFAULT_HPO_FREQUENCY.mean();
    /** The characteristic age of onset of a feature in a certain disease. */
    private HpoOnset onset = DEFAULT_HPO_ONSET;
    /** List of modifiers of this annotation. List can be empty but cannot be null */
    private List<TermId> modifierList = ImmutableList.of();

    public Builder(TermId tid) {
      Objects.requireNonNull(tid,"TermId cannot be null");
      this.termId = tid;
    }

    public Builder onset(HpoOnset o) {
      Objects.requireNonNull(o,"Onset cannot be null");
      this.onset = o;
      return this;
    }

    public Builder frequency(double f) {
      checkBoundsWithinZeroToOne(f);
      this.frequency = f;
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

    public HpoAnnotation build() {
      return new HpoAnnotation(termId, frequency, onset, modifierList);
    }
  }
}
