package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

class HpoDiseaseAnnotationMetadataDefault implements HpoDiseaseAnnotationMetadata {

  private final AnnotationReference reference;
  private final TemporalInterval temporalInterval;
  private final AnnotationFrequency frequency;
  private final Collection<TermId> modifiers;
  private final Sex sex;

  HpoDiseaseAnnotationMetadataDefault(AnnotationReference reference,
                                      TemporalInterval temporalInterval,
                                      AnnotationFrequency frequency,
                                      Collection<TermId> modifiers,
                                      Sex sex) {
    this.reference = Objects.requireNonNull(reference, "Reference must not be null");
    this.temporalInterval = temporalInterval; // nullable
    this.frequency = frequency; // nullable
    this.modifiers = Objects.requireNonNull(modifiers, "Modifiers collection must not be null");
    this.sex = sex; // nullable
  }

  @Override
  public AnnotationReference reference() {
    return reference;
  }

  @Override
  public Optional<TemporalInterval> observationInterval() {
    return Optional.ofNullable(temporalInterval);
  }

  @Override
  public Optional<AnnotationFrequency> frequency() {
    return Optional.ofNullable(frequency);
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationMetadataDefault that = (HpoDiseaseAnnotationMetadataDefault) o;
    return Objects.equals(reference, that.reference) && Objects.equals(temporalInterval, that.temporalInterval) && Objects.equals(frequency, that.frequency) && Objects.equals(modifiers, that.modifiers) && Objects.equals(sex, that.sex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reference, temporalInterval, frequency, modifiers, sex);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationMetadataDefault{" +
      "reference=" + reference +
      ", temporalInterval=" + temporalInterval +
      ", frequency=" + frequency +
      ", modifiers=" + modifiers +
      ", sex=" + sex +
      '}';
  }

}
