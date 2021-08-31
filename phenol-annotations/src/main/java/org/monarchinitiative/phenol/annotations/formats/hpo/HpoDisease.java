package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Model of a disease from the HPO annotations.
 *
 * <p>The main purpose here is to separate phenotypic abnormalities from mode of inheritance and
 * other annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1 (2017-11-16)
 */
@InProgress
public interface HpoDisease {

  static HpoDisease of(String name,
                       TermId databaseId,
                       List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                       List<TermId> modesOfInheritance,
                       List<TermId> notTerms) {
    return HpoDiseaseDefault.of(name, databaseId, phenotypicAbnormalities, modesOfInheritance, notTerms);
  }

  TermId diseaseDatabaseTermId();

  String diseaseName();

  List<TermId> modesOfInheritance();

  Stream<HpoDiseaseAnnotation> phenotypicAbnormalities();

  List<TermId> negativeAnnotations();

  /*
  Disease:
  - name
  - id
  - 0..n HpoDiseaseAnnotation
  - 0..n modes of inheritance
  - 0..n negativeAnnotations

  - modifiers? incomplete penetrance?

  HpoDiseaseAnnotation
  - termId (e.g. Hypertension)
  - 0..n DiseaseFeatureMetadata metadata()

  DiseaseFeatureMetadata
  - Optional<TermId> onset()
  - Optional<Frequency> frequency()
  - 0..n TermId modifiers()
  - Optional<TermId> sex()

  Frequency
  - numerator
  - denominator

  | Example
  - childhood onset
  - 2/7
  - (Unilateral, Severe)

   */


  /**
   * Users can user this function to get the HpoTermId corresponding to a TermId
   *
   * @param termId id of the plain {@link TermId} for which we want to have the {@link HpoDiseaseAnnotation}.
   * @return optional with {@link HpoDiseaseAnnotation}
   */
  default Optional<HpoDiseaseAnnotation> getAnnotation(TermId termId) {
    return phenotypicAbnormalities()
      .filter(diseaseAnnotation -> diseaseAnnotation.termId().equals(termId))
      .findAny();
  }

  /**
   * Returns the mean frequency of the feature in the disease.
   *
   * @param termId id of an HPO term
   * @return frequency of the phenotypic feature in individuals with the annotated disease
   */
  default double getFrequencyOfTermInDisease(TermId termId) {
    return phenotypicAbnormalities()
      .filter(diseaseAnnotation -> diseaseAnnotation.termId().equals(termId))
      .findFirst()
      .map(HpoDiseaseAnnotation::frequency)
      .orElse(0D); // term not annotated to disease so frequency is zero
  }

  default Stream<TermId> getPhenotypicAbnormalityTermIds() {
    return phenotypicAbnormalities()
      .map(HpoDiseaseAnnotation::termId);
  }

  /**
   * @return the count of the non-negated annotations excluding mode of inheritance.
   */
  default long getNumberOfPhenotypeAnnotations() {
    return this.phenotypicAbnormalities().count();
  }


  /**
   * @param termId ID of an HPO Term
   * @return true if there is a direct annotation to termId. Does not include indirect annotations from
   * annotation propagation rule.
   */
  default boolean isDirectlyAnnotatedTo(TermId termId) {
    return phenotypicAbnormalities()
      .anyMatch(annotation -> annotation.termId().equals(termId));
  }

  /**
   * @param termIds Set of ids of HPO Terms
   * @return true if there is a direct annotation to any of the terms in termIds. Does not include
   * indirect annotations from annotation propagation rule.
   */
  default boolean isDirectlyAnnotatedToAnyOf(Set<TermId> termIds) {
    return phenotypicAbnormalities().anyMatch(tiwm -> termIds.contains(tiwm.termId()));
  }
}
