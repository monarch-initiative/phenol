package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.Sex;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Objects;

class HpoDiseaseAnnotationMetadataDefault implements HpoDiseaseAnnotationMetadata {

  private final HpoOnset onset;
  private final FrequencyBin frequency;
  private final Collection<TermId> modifiers;
  private final Sex sex;

  static HpoDiseaseAnnotationMetadataDefault of(HpoOnset onset, FrequencyBin frequency, Collection<TermId> modifiers, Sex sex) {
    return new HpoDiseaseAnnotationMetadataDefault(onset, frequency, modifiers, sex);
  }

  private HpoDiseaseAnnotationMetadataDefault(HpoOnset onset, FrequencyBin frequency, Collection<TermId> modifiers, Sex sex) {
    this.onset = Objects.requireNonNull(onset, "Onset cannot be null");
    this.frequency = Objects.requireNonNull(frequency, "Frequency cannot be null");
    this.modifiers = Objects.requireNonNull(modifiers, "Modifiers collection cannot be null");
    this.sex = Objects.requireNonNull(sex, "Sex cannot be null");
  }

  @Override
  public HpoOnset onset() {
    return onset;
  }

  @Override
  public FrequencyBin frequency() {
    return frequency;
  }

  @Override
  public Collection<TermId> modifiers() {
    return modifiers;
  }

  @Override
  public Sex sex() {
    return sex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationMetadataDefault that = (HpoDiseaseAnnotationMetadataDefault) o;
    return Objects.equals(onset, that.onset) && Objects.equals(frequency, that.frequency) && Objects.equals(modifiers, that.modifiers) && Objects.equals(sex, that.sex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(onset, frequency, modifiers, sex);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationMetadataDefault{" +
      "onset=" + onset +
      ", frequency=" + frequency +
      ", modifiers=" + modifiers +
      ", sex=" + sex +
      '}';
  }
}
