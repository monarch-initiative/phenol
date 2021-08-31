package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class HpoDiseaseDefault implements HpoDisease {

  /**
   * Name of the disease from annotation.
   */
  private final String name;

  /**
   * The disease identifier as a CURIE, e.g., OMIM:600100.
   */
  private final TermId diseaseDatabaseId;

  /**
   * {@link TermId}s with phenotypic abnormalities and their frequencies.
   */
  private final Collection<HpoDiseaseAnnotation> phenotypicAbnormalities;

  /**
   * {@link TermId}s with mode of inheritance and their frequencies.
   */
  private final List<TermId> modesOfInheritance;

  /**
   * {@link TermId}s that do NOT characterize this disease.
   */
  private final List<TermId> negativeAnnotations;

  private HpoDiseaseDefault(String name,
                           TermId databaseId,
                           List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                           List<TermId> modesOfInheritance,
                           List<TermId> notTerms) {
    this.name = name;
    this.diseaseDatabaseId = databaseId;
    this.phenotypicAbnormalities = phenotypicAbnormalities;
    this.modesOfInheritance = modesOfInheritance;
    this.negativeAnnotations = notTerms;
  }

  static HpoDiseaseDefault of(String name,
                              TermId databaseId,
                              List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                              List<TermId> modesOfInheritance,
                              List<TermId> notTerms) {
    return new HpoDiseaseDefault(name, databaseId, phenotypicAbnormalities, modesOfInheritance, notTerms);
  }


  @Override
  public TermId diseaseDatabaseTermId() {
    return diseaseDatabaseId;
  }

  @Override
  public String diseaseName() {
    return name;
  }

  /**
   * @return The list of frequency-annotated phenotypic abnormalities.
   */
  @Override
  public Stream<HpoDiseaseAnnotation> phenotypicAbnormalities() {
    return phenotypicAbnormalities.stream();
  }

  /**
   * @return The list of frequency-annotated modes of inheritance.
   */
  @Override
  public List<TermId> modesOfInheritance() {
    return modesOfInheritance;
  }

  @Override
  public List<TermId> negativeAnnotations() {
    return negativeAnnotations;
  }

//  /**
//   * @return The list of clinical modifiers for the disease
//   */
//  @Override
//  public List<TermId> clinicalModifiers() {
//    return clinicalModifiers;
//  }

//  /**
//   * @return The list of clinical course terms for the disease
//   */
//  private List<TermId> getClinicalCourseList() {
//    return clinicalCourseList;
//  }

  //  /**
//   * Naive implementation.
//   * ToDo -- parse actual frequencies from phenotype.hpoa, normalize
//   * @return Map with distribution of HpoOnsets (assumed to sum to 1.0).
//   */
//  @Override
//  public Map<HpoOnset, Double> onsetDistribution() {
//    if (clinicalCourseList.isEmpty()) {
//      return Collections.emptyMap();
//    }
//    double freq = 1.0 / clinicalCourseList.size();
//
//    return clinicalCourseList.stream()
//      .collect(Collectors.toMap(HpoOnset::fromTermId, termId -> freq));
//  }

  /**
   * Check if {@code tid} is annotated to any of the terms to which this disease is annotated or their ancestors
   *
   * @param tid      An HP query term
   * @param ontology reference to HPO ontology
   * @return true iff this disease is annotated to the term directly or via annotation propagation
   */
  private boolean isAnnotatedTo(TermId tid, Ontology ontology) {
    List<TermId> direct = getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
    Set<TermId> ancs = ontology.getAllAncestorTermIds(direct, true);
    return ancs.contains(tid);
  }


  /**
   * Get the frequency of a term in the disease. This includes if any disease term is an ancestor of the
   * query term -- we take the maximum of any ancestor term.
   *
   * @param tid      Term ID of an HPO term whose frequency we want to know
   * @param ontology Reference to the HPO ontology
   * @return frequency of the term in the disease (including annotation propagation)
   */
  private double getFrequencyOfTermInDiseaseWithAnnotationPropagation(TermId tid, Ontology ontology) {
    return getPhenotypicAbnormalityTermIds().map(term -> {
        Set<TermId> ancs = ontology.getAncestorTermIds(term, true);
        if (ancs.contains(tid)) {
          return Optional.of(getFrequencyOfTermInDisease(term));
        }
        return Optional.<Double>empty();
      }).filter(Optional::isPresent)
      .map(Optional::get)
      .max(Double::compareTo)
      .orElse(0.);
  }

  @Override
  public String toString() {
    String abnormalityList = phenotypicAbnormalities.stream()
      .map(f -> f.termId().getValue())
      .collect(Collectors.joining(";"));
    return String.format(
      "HpoDisease [name=%s;%s] phenotypicAbnormalities=\n%s" + ", modesOfInheritance=%s",
      name, diseaseDatabaseId.getValue(), abnormalityList, modesOfInheritance);
  }

  /**
   * @return the {@code DB} field of the annotation, e.g., OMIM, ORPHA, or DECIPHER (prefix of the diseaseId)
   */
  public String getDatabase() {
    return diseaseDatabaseId.getPrefix();
  }
}
