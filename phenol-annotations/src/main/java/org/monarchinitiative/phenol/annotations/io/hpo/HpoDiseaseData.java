package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;

/**
 * {@link HpoAnnotationLine}s corresponding to single disease.
 */
class HpoDiseaseData {

  private final String diseaseName;
  private final List<HpoAnnotation> phenotypes;
  private final List<TermId> modesOfInheritance;
  private final List<TermId> clinicalModifiers;
  private final List<TermId> clinicalCourseTerms;

  HpoDiseaseData(String diseaseName,
                 List<HpoAnnotation> phenotypes,
                 List<TermId> modesOfInheritance,
                 List<TermId> clinicalModifiers,
                 List<TermId> clinicalCourseTerms) {
    this.diseaseName = diseaseName;
    this.phenotypes = phenotypes;
    this.modesOfInheritance = modesOfInheritance;
    this.clinicalModifiers = clinicalModifiers;
    this.clinicalCourseTerms = clinicalCourseTerms;
  }

  public String diseaseName() {
    return diseaseName;
  }

  public List<HpoAnnotation> phenotypes() {
    return phenotypes;
  }

  public List<TermId> modesOfInheritance() {
    return modesOfInheritance;
  }

  public List<TermId> clinicalModifiers() {
    return clinicalModifiers;
  }

  public List<TermId> clinicalCourseTerms() {
    return clinicalCourseTerms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseData that = (HpoDiseaseData) o;
    return Objects.equals(diseaseName, that.diseaseName) && Objects.equals(phenotypes, that.phenotypes) && Objects.equals(modesOfInheritance, that.modesOfInheritance) && Objects.equals(clinicalModifiers, that.clinicalModifiers) && Objects.equals(clinicalCourseTerms, that.clinicalCourseTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diseaseName, phenotypes, modesOfInheritance, clinicalModifiers, clinicalCourseTerms);
  }

  @Override
  public String toString() {
    return "HpoDiseaseData{" +
      "diseaseName='" + diseaseName + '\'' +
      ", phenotypes=" + phenotypes +
      ", modesOfInheritance=" + modesOfInheritance +
      ", clinicalModifiers=" + clinicalModifiers +
      ", clinicalCourseTerms=" + clinicalCourseTerms +
      '}';
  }

}
