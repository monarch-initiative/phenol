package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.annotations.base.AgeRange;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;
import java.util.Optional;

class DiseaseFeatureDefault implements DiseaseFeature {

  private final TermId id;
  private final AgeRange duration;
  private final DiseaseFeatureFrequency frequency;
  private final Sex sex;

  DiseaseFeatureDefault(TermId id, AgeRange duration, DiseaseFeatureFrequency frequency, Sex sex) {
    this.id = id;
    this.duration = duration;
    this.frequency = frequency;
    this.sex = sex;
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public AgeRange duration() {
    return duration;
  }

  @Override
  public DiseaseFeatureFrequency frequency() {
    return frequency;
  }

  @Override
  public Optional<Sex> sex() {
    return Optional.ofNullable(sex);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiseaseFeatureDefault that = (DiseaseFeatureDefault) o;
    return Objects.equals(id, that.id) && Objects.equals(duration, that.duration) && Objects.equals(frequency, that.frequency) && sex == that.sex;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, duration, frequency, sex);
  }

  @Override
  public String toString() {
    return "DiseaseFeatureDefault{" +
      "id=" + id +
      ", duration=" + duration +
      ", frequency=" + frequency +
      ", sex=" + sex +
      '}';
  }
}
