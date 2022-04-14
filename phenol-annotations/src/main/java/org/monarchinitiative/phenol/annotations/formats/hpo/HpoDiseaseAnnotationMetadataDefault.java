package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

class HpoDiseaseAnnotationMetadataDefault implements HpoDiseaseAnnotationMetadata {

  private final TemporalInterval temporalInterval;
  private final AnnotationFrequency diseaseAnnotationFrequency;
  private final Collection<TermId> modifiers;
  private final Sex sex;

  HpoDiseaseAnnotationMetadataDefault(TemporalInterval temporalInterval, AnnotationFrequency diseaseAnnotationFrequency, Collection<TermId> modifiers, Sex sex) {
    this.temporalInterval = temporalInterval; // nullable
    this.diseaseAnnotationFrequency = Objects.requireNonNull(diseaseAnnotationFrequency, "Frequency cannot be null");
    this.modifiers = Objects.requireNonNull(modifiers, "Modifiers collection cannot be null");
    this.sex = sex; // nullable
  }

  @Override
  public Optional<TemporalInterval> observationInterval() {
    return Optional.ofNullable(temporalInterval);
  }

  @Override
  public AnnotationFrequency frequency() {
    return diseaseAnnotationFrequency;
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
    return Objects.equals(temporalInterval, that.temporalInterval) && Objects.equals(diseaseAnnotationFrequency, that.diseaseAnnotationFrequency) && Objects.equals(modifiers, that.modifiers) && Objects.equals(sex, that.sex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temporalInterval, diseaseAnnotationFrequency, modifiers, sex);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationMetadataDefault{" +
      "temporalInterval=" + temporalInterval +
      ", frequency=" + diseaseAnnotationFrequency +
      ", modifiers=" + modifiers +
      ", sex=" + sex +
      '}';
  }

}
