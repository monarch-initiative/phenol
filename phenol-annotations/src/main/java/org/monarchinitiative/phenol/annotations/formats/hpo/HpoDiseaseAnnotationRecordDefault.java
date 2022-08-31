package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Default {@link HpoDiseaseAnnotationRecord} implementation.
 */
class HpoDiseaseAnnotationRecordDefault implements HpoDiseaseAnnotationRecord {

  private final Ratio ratio;
  private final TemporalInterval temporalInterval;
  private final Collection<AnnotationReference> references;
  private final Sex sex;
  private final Collection<TermId> modifiers;

  HpoDiseaseAnnotationRecordDefault(Ratio ratio,
                                    TemporalInterval temporalInterval,
                                    Collection<AnnotationReference> references,
                                    Sex sex,
                                    Collection<TermId> modifiers) {
    this.ratio = Objects.requireNonNull(ratio);
    this.temporalInterval = temporalInterval; // nullable
    this.references = Objects.requireNonNull(references);
    this.sex = sex; // nullable
    this.modifiers = Objects.requireNonNull(modifiers);
  }

  @Override
  public Ratio ratio() {
    return ratio;
  }

  @Override
  public Optional<TemporalInterval> temporalInterval() {
    return Optional.ofNullable(temporalInterval);
  }

  @Override
  public Collection<AnnotationReference> references() {
    return references;
  }

  @Override
  public Collection<TermId> modifiers() {
    return modifiers;
  }

  @Override
  public Optional<Sex> sex() {
    return Optional.ofNullable(sex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratio, temporalInterval, references, sex, modifiers);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationRecordDefault that = (HpoDiseaseAnnotationRecordDefault) o;
    return Objects.equals(ratio, that.ratio) && Objects.equals(temporalInterval, that.temporalInterval) && Objects.equals(references, that.references) && sex == that.sex && Objects.equals(modifiers, that.modifiers);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationRecordDefault{" +
      "ratio=" + ratio +
      ", temporalInterval=" + temporalInterval +
      ", reference=" + references +
      ", sex=" + sex +
      ", modifiers=" + modifiers +
      '}';
  }
}
