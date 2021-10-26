package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class MendelianDiseaseDefault implements MendelianDisease {

  private final TermId id;
  private final String name;
  private final Set<DiseaseFeature> features;
  private final List<TermId> modesOfInheritance;

  private MendelianDiseaseDefault(TermId id,
                                  String name,
                                  Set<DiseaseFeature> features,
                                  List<TermId> modesOfInheritance) {
    this.id = id;
    this.name = name;
    this.features = features;
    this.modesOfInheritance = modesOfInheritance;
  }

  public static MendelianDiseaseDefault of(TermId id,
                                           String name,
                                           Set<DiseaseFeature> features,
                                           List<TermId> modesOfInheritance) {
    Set<DiseaseFeature> diseaseFeatures = Set.copyOf(Objects.requireNonNull(features, "Features must not be null"));
    List<TermId> modes = List.copyOf(Objects.requireNonNull(modesOfInheritance, "Modes of inheritance must not be null"));
    return new MendelianDiseaseDefault(Objects.requireNonNull(id, "Id must not be null"),
      Objects.requireNonNull(name, "Name must not be null"),
      diseaseFeatures,
      modes);
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Stream<DiseaseFeature> diseaseFeatures() {
    return features.stream();
  }

  @Override
  public List<TermId> modesOfInheritance() {
    return modesOfInheritance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MendelianDiseaseDefault that = (MendelianDiseaseDefault) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(features, that.features) && Objects.equals(modesOfInheritance, that.modesOfInheritance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, features, modesOfInheritance);
  }

  @Override
  public String toString() {
    return "MendelianDiseaseDefault{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", features=" + features +
      ", modesOfInheritance=" + modesOfInheritance +
      '}';
  }
}
