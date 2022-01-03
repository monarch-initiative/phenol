package org.monarchinitiative.phenol.annotations.formats.hpo;

import java.util.*;
import java.util.stream.Stream;

class DiseaseToGeneAssociationsDefault implements DiseaseToGeneAssociations {

  private final List<DiseaseToGeneAssociation> diseaseToGeneAssociations;

  static DiseaseToGeneAssociations of(List<DiseaseToGeneAssociation> diseaseToGeneAssociations) {
    return new DiseaseToGeneAssociationsDefault(diseaseToGeneAssociations);
  }

  private DiseaseToGeneAssociationsDefault(List<DiseaseToGeneAssociation> diseaseToGeneAssociations) {
    this.diseaseToGeneAssociations = Objects.requireNonNull(diseaseToGeneAssociations, "The association list must not be null");
  }

  @Override
  public Stream<DiseaseToGeneAssociation> diseaseToGeneAssociations() {
    return diseaseToGeneAssociations.stream();
  }

  @Override
  public Iterator<DiseaseToGeneAssociation> iterator() {
    return diseaseToGeneAssociations.iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiseaseToGeneAssociationsDefault that = (DiseaseToGeneAssociationsDefault) o;
    return Objects.equals(diseaseToGeneAssociations, that.diseaseToGeneAssociations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diseaseToGeneAssociations);
  }

  @Override
  public String toString() {
    return "DiseaseToGeneAssociations{" +
      "diseaseToGeneAssociations=" + diseaseToGeneAssociations +
      '}';
  }
}
