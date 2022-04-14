package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
public interface HpoDisease extends Identified {

  static HpoDisease of(String name,
                       TermId databaseId,
                       TemporalInterval globalOnset,
                       List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                       List<TermId> modesOfInheritance) {
    return new HpoDiseaseDefault(databaseId, name, globalOnset, phenotypicAbnormalities, modesOfInheritance);
  }

  /**
   * Use {@link #id()}
   */
  @Deprecated
  default TermId diseaseDatabaseTermId() {
    return id();
  }

  String diseaseName();

  /**
   * @return global disease onset
   */
  Optional<TemporalInterval> globalOnset();

  List<TermId> modesOfInheritance();

  Iterator<HpoDiseaseAnnotation> phenotypicAbnormalities();

  int phenotypicAbnormalitiesCount();

  default Stream<HpoDiseaseAnnotation> phenotypicAbnormalitiesStream() {
    return StreamSupport.stream(Spliterators.spliterator(phenotypicAbnormalities(), phenotypicAbnormalitiesCount(), Spliterator.DISTINCT & Spliterator.SIZED), false);
  }

  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> negativeAnnotations() {
    return List.of();
  }

  /**
   * Users can user this function to get the HpoTermId corresponding to a TermId
   *
   * @param termId id of the plain {@link TermId} for which we want to have the {@link HpoDiseaseAnnotation}.
   * @return optional with {@link HpoDiseaseAnnotation}
   */
  default Optional<HpoDiseaseAnnotation> getAnnotation(TermId termId) {
    return phenotypicAbnormalitiesStream()
      .filter(diseaseAnnotation -> diseaseAnnotation.id().equals(termId))
      .findAny();
  }

  /**
   * Returns the mean frequency of the feature in the disease.
   *
   * @param termId id of an HPO term
   * @return frequency of the phenotypic feature in individuals with the annotated disease
   */
  default Optional<Ratio> getFrequencyOfTermInDisease(TermId termId) {
    return phenotypicAbnormalitiesStream()
      .filter(diseaseAnnotation -> diseaseAnnotation.id().equals(termId))
      .findFirst()
      .flatMap(HpoDiseaseAnnotation::ratio);
  }

  default Stream<TermId> getPhenotypicAbnormalityTermIds() {
    return phenotypicAbnormalitiesStream()
      .map(HpoDiseaseAnnotation::id);
  }

  /**
   * @return the count of the non-negated annotations excluding mode of inheritance
   * @deprecated use {@link #phenotypicAbnormalitiesCount()}
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default long getNumberOfPhenotypeAnnotations() {
    return phenotypicAbnormalitiesCount();
  }


  /**
   * @param termId ID of an HPO Term
   * @return true if there is a direct annotation to termId. Does not include indirect annotations from
   * annotation propagation rule.
   */
  default boolean isDirectlyAnnotatedTo(TermId termId) {
    return phenotypicAbnormalitiesStream()
      .anyMatch(annotation -> annotation.id().equals(termId));
  }

  /**
   * @param termIds Set of ids of HPO Terms
   * @return true if there is a direct annotation to any of the terms in termIds. Does not include
   * indirect annotations from annotation propagation rule.
   */
  default boolean isDirectlyAnnotatedToAnyOf(Set<TermId> termIds) {
    return phenotypicAbnormalitiesStream().anyMatch(tiwm -> termIds.contains(tiwm.id()));
  }
}
