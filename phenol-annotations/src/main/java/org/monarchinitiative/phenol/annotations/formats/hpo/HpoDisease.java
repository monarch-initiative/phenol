package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Model of a disease from the HPO annotations.
 *
 * <p>The main purpose here is to separate phenotypic abnormalities from mode of inheritance and
 * other annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.2.1 (2017-11-16)
 */
public final class HpoDisease {
  /** Name of the disease from annotation. */
  private final String name;
  /** The disease identifier as a CURIE, e.g., OMIM:600100. */
  private final TermId diseaseDatabaseId;

  /** {@link TermId}s with phenotypic abnormalities and their frequencies. */
  private final List<HpoAnnotation> phenotypicAbnormalities;

  /** {@link TermId}s with mode of inheritance and their frequencies. */
  private final List<TermId> modesOfInheritance;
  /** {@link TermId}s that do NOT characterize this disease. */
  private final List<TermId> negativeAnnotations;
  /** {@link TermId}s for clinical modifiers, including Incomplete penetrance . */
  private final List<TermId> clinicalModifiers;
  /** {@link TermId}s clinical course representing Onset, Mortality, Pace of progression and Temporal pattern . */
  private final List<TermId> clinicalCourseList;


  public TermId getDiseaseDatabaseId() {
    return diseaseDatabaseId;
  }

  public HpoDisease(
    String name,
    TermId databaseId,
    List<HpoAnnotation> phenotypicAbnormalities,
    List<TermId> modesOfInheritance,
    List<TermId> notTerms,
    List<TermId> clinicalModifiers,
    List<TermId> clinicalCourses) {
    this.name = name;
    this.diseaseDatabaseId = databaseId;
    this.phenotypicAbnormalities = ImmutableList.copyOf(phenotypicAbnormalities);
    this.modesOfInheritance = ImmutableList.copyOf(modesOfInheritance);
    this.negativeAnnotations = ImmutableList.copyOf(notTerms);
    this.clinicalModifiers = ImmutableList.copyOf(clinicalModifiers);
    this.clinicalCourseList = ImmutableList.copyOf(clinicalCourses);
  }

  /** @return The name of the disease. */
  public String getName() {
    return name;
  }

  /** @return the count of the non-negated annotations excluding mode of inheritance. */
  public int getNumberOfPhenotypeAnnotations() {
    return this.phenotypicAbnormalities.size();
  }

  /** @return The list of frequency-annotated phenotypic abnormalities. */
  public List<HpoAnnotation> getPhenotypicAbnormalities() {
    return phenotypicAbnormalities;
  }

  /** @return The list of phenotype abnormalities as bare TermIds. */
  public List<TermId> getPhenotypicAbnormalityTermIdList() {
    return phenotypicAbnormalities.stream().map(HpoAnnotation::getTermId).collect(Collectors.toList());
  }

  /** @return The list of frequency-annotated modes of inheritance. */
  public List<TermId> getModesOfInheritance() {
    return modesOfInheritance;
  }

  public List<TermId> getNegativeAnnotations() {
    return this.negativeAnnotations;
  }

  /**
   * @return The list of clinical modifiers for the disease
   */
  public List<TermId> getClinicalModifiers() {
    return clinicalModifiers;
  }

  /**
   * @return The list of clinical course terms for the disease
   */
  public List<TermId> getClinicalCourseList() {
    return clinicalCourseList;
  }

  /**
   * Users can user this function to get the HpoTermId corresponding to a TermId
   *
   * @param id id of the plain {@link TermId} for which we want to have the {@link HpoAnnotation}.
   * @return corresponding {@link HpoAnnotation} or null if not present.
   */
  public HpoAnnotation getAnnotation(TermId id) {
    return phenotypicAbnormalities
        .stream()
        .filter(timd -> timd.getTermId().equals(id))
        .findAny()
        .orElse(null);
  }

  /**
   * @param tid ID of an HPO TermI
   * @return true if there is a direct annotation to tid. Does not include indirect annotations from
   *     annotation propagation rule.
   */
  public boolean isDirectlyAnnotatedTo(TermId tid) {
    for (HpoAnnotation tiwm : phenotypicAbnormalities) {
      if (tiwm.getTermId().equals(tid)) return true;
    }
    return false;
  }
  /**
   * @param tidset Set of ids of HPO Terms
   * @return true if there is a direct annotation to any of the terms in tidset. Does not include
   *     indirect annotations from annotation propagation rule.
   */
  public boolean isDirectlyAnnotatedToAnyOf(Set<TermId> tidset) {
    for (HpoAnnotation tiwm : phenotypicAbnormalities) {
      if (tidset.contains(tiwm.getTermId())) return true;
    }
    return false;
  }

  /**
   * Check if {@code tid} is annotated to any of the terms to which this disease is annotated or their ancestors
   * @param tid An HP query term
   * @param ontology reference to HPO ontology
   * @return true iff this disease is annotated to the term directly or via annotation propagation
   */
  private boolean isAnnotatedTo(TermId tid, Ontology ontology) {
    List<TermId> direct = getPhenotypicAbnormalityTermIdList();
    Set<TermId> ancs = ontology.getAllAncestorTermIds(direct,true);
    return ancs.contains(tid);
  }


  /**
   * Returns the mean frequency of the feature in the disease.
   *
   * @param tid id of an HPO term
   * @return frequency of the phenotypic feature in individuals with the annotated disease
   */
  public double getFrequencyOfTermInDisease(TermId tid) {
    HpoAnnotation tiwm =
        phenotypicAbnormalities
            .stream()
            .filter(twm -> twm.getTermId().equals(tid))
            .findFirst()
            .orElse(null);
    if (tiwm == null) {
      return 0D; // term not annotated to disease so frequency is zero
    } else return tiwm.getFrequency();
  }

  /**
   * Get the frequency of a term in the disease. This includes if any disease term is an ancestor of the
   * query term -- we take the maximum of any ancestor term.
   * @param tid Term ID of an HPO term whose frequency we want to know
   * @param ontology Reference to the HPO ontology
   * @return frequency of the term in the disease (including annotation propagation)
   */
  private double getFrequencyOfTermInDiseaseWithAnnotationPropagation(TermId tid, Ontology ontology) {
    double freq=0.0;
    for (TermId diseaseTermId :  getPhenotypicAbnormalityTermIdList() ) {
      Set<TermId> ancs = ontology.getAncestorTermIds(diseaseTermId,true);
      if (ancs.contains(tid)) {
        double f = getFrequencyOfTermInDisease(diseaseTermId);
        freq = Math.max(f,freq);
      }
    }
    return freq;
  }

  @Override
  public String toString() {
    String abnormalityList = phenotypicAbnormalities
      .stream()
      .map(HpoAnnotation::getIdWithPrefix)
      .collect(Collectors.joining(";"));
    return String.format(
      "HpoDisease [name=%s;%s] phenotypicAbnormalities=\n%s" + ", modesOfInheritance=%s",
      name, diseaseDatabaseId.getValue(), abnormalityList, modesOfInheritance);
  }

  /** @return the {@code DB} field of the annotation, e.g., OMIM, ORPHA, or DECIPHER (prefix of the diseaseId) */
  public String getDatabase() {
    return diseaseDatabaseId.getPrefix();
  }
}
