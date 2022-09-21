package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

class HpoDiseaseDefault implements HpoDisease {

  /**
   * The disease identifier as a CURIE, e.g., OMIM:600100.
   */
  private final TermId diseaseDatabaseId;

  /**
   * Name of the disease from annotation.
   */
  private final String diseaseName;

  /**
   * Global disease onset.
   */
  private final TemporalInterval onset;

  /**
   * {@link TermId}s with phenotypic abnormalities and their frequencies.
   */
  private final List<HpoDiseaseAnnotation> phenotypicAbnormalities;

  /**
   * {@link TermId}s with mode of inheritance and their frequencies.
   */
  private final List<TermId> modesOfInheritance;

  HpoDiseaseDefault(TermId diseaseDatabaseId,
                    String diseaseName,
                    TemporalInterval onset,
                    List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                    List<TermId> modesOfInheritance) {
    this.diseaseDatabaseId = Objects.requireNonNull(diseaseDatabaseId, "Disease database ID must not be null");
    this.diseaseName = Objects.requireNonNull(diseaseName, "Name must not be null");
    this.onset = onset; // nullable
    this.phenotypicAbnormalities = Objects.requireNonNull(phenotypicAbnormalities, "Phenotypic abnormalities must not be null");
    this.modesOfInheritance = Objects.requireNonNull(modesOfInheritance, "Modes of inheritance must not be null");
  }

  @Override
  public TermId id() {
    return diseaseDatabaseId;
  }

  @Override
  public String diseaseName() {
    return diseaseName;
  }

  /**
   * @return The list of frequency-annotated modes of inheritance.
   */
  @Override
  public List<TermId> modesOfInheritance() {
    return modesOfInheritance;
  }

  @Override
  public Collection<HpoDiseaseAnnotation> annotations() {
    return phenotypicAbnormalities;
  }

  @Override
  public Optional<TemporalInterval> diseaseOnset() {
    return Optional.ofNullable(onset);
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
   * Get the frequency of a term in the disease. This includes if any disease term is an ancestor of the
   * query term -- we take the maximum of any ancestor term.
   *
   * @param tid      Term ID of an HPO term whose frequency we want to know
   * @param ontology Reference to the HPO ontology
   * @return frequency of the term in the disease (including annotation propagation)
   */
  private double getFrequencyOfTermInDiseaseWithAnnotationPropagation(TermId tid, Ontology ontology) {
//    return getPhenotypicAbnormalityTermIds()
//      .map(term -> {
//        Set<TermId> ancs = ontology.getAncestorTermIds(term, true);
//        if (ancs.contains(tid)) {
//          return Optional.of(getFrequencyOfTermInDisease(term));
//        }
//        return Optional.<Double>empty();
//      }).map(Optional::stream)
//      .max(Double::compareTo)
//      .orElse(0.);
    // TODO - implement
    return Double.NaN;
  }

  @Override
  public String toString() {
    String abnormalityList = phenotypicAbnormalities.stream()
      .map(f -> f.id().getValue())
      .collect(Collectors.joining(";"));
    return String.format("HpoDisease [name=%s;%s] phenotypicAbnormalities=%s, globalOnset=%s, modesOfInheritance=%s",
      diseaseName, diseaseDatabaseId.getValue(), abnormalityList, onset, modesOfInheritance);
  }
}
