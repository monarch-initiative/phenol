package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.TemporalRange;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

class HpoDiseaseAnnotationMetadataDefault implements HpoDiseaseAnnotationMetadata {

  private final TemporalRange temporalRange;
  private final AnnotationFrequency diseaseAnnotationFrequency;
  private final Collection<TermId> modifiers;
  private final Sex sex;

  HpoDiseaseAnnotationMetadataDefault(TemporalRange temporalRange, AnnotationFrequency diseaseAnnotationFrequency, Collection<TermId> modifiers, Sex sex) {
    this.temporalRange = temporalRange;
    this.diseaseAnnotationFrequency = Objects.requireNonNull(diseaseAnnotationFrequency, "Frequency cannot be null");
    this.modifiers = Objects.requireNonNull(modifiers, "Modifiers collection cannot be null");
    this.sex = sex;
  }

  @Override
  public Optional<TemporalRange> temporalRange() {
    return Optional.ofNullable(temporalRange);
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
    return Objects.equals(temporalRange, that.temporalRange) && Objects.equals(diseaseAnnotationFrequency, that.diseaseAnnotationFrequency) && Objects.equals(modifiers, that.modifiers) && Objects.equals(sex, that.sex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temporalRange, diseaseAnnotationFrequency, modifiers, sex);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationMetadataDefault{" +
      "temporalRange=" + temporalRange +
      ", frequency=" + diseaseAnnotationFrequency +
      ", modifiers=" + modifiers +
      ", sex=" + sex +
      '}';
  }

}
