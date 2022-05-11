package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;
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

  static HpoDisease of(TermId diseaseId,
                       String name,
                       TemporalInterval globalOnset,
                       List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                       List<TermId> modesOfInheritance) {
    return new HpoDiseaseDefault(diseaseId, name, globalOnset, phenotypicAbnormalities, modesOfInheritance);
  }

  /**
   * @deprecated to be removed in v3.0.0, use {@link #id()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default TermId diseaseDatabaseTermId() {
    return id();
  }

  /**
   * @deprecated to be removed in v3.0.0, use {@link #id()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default TermId getDiseaseDatabaseTermId() {
    return id();
  }

  // TODO - add String getName() and getDiseaseName() and deprecate since v3.0.0
  String diseaseName();

  /**
   * @deprecated to be removed in v3.0.0, use {@link #diseaseName()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default String getName() {
    return diseaseName();
  }

  /**
   * @deprecated to be removed in v3.0.0, use {@link #diseaseName()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default String getDiseaseName() {
    return diseaseName();
  }

  /**
   * @return global disease onset
   */
  Optional<TemporalInterval> globalOnset();

  List<TermId> modesOfInheritance();

  /**
   * @deprecated to be removed in v3.0.0, use {@link #modesOfInheritance()} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC2")
  default List<TermId> getModesOfInheritance() {
    return modesOfInheritance();
  }

  /**
   * @return iterator of <em>ALL</em> phenotypic abnormalities of the disease, both present and absent.
   */
  Iterator<HpoDiseaseAnnotation> phenotypicAbnormalities();

  int phenotypicAbnormalitiesCount();

  /**
   * @return stream of <em>ALL</em> phenotypic abnormalities of the disease, both present and absent.
   */
  default Stream<HpoDiseaseAnnotation> phenotypicAbnormalitiesStream() {
    return StreamSupport.stream(Spliterators.spliterator(phenotypicAbnormalities(), phenotypicAbnormalitiesCount(), Spliterator.DISTINCT & Spliterator.SIZED), false);
  }

  /**
   * The method is kept for backward compatibility. However, the method throws {@link PhenolRuntimeException} upon each invocation.
   *
   * @deprecated to be removed in 2.0.0, use {@link #phenotypicAbnormalities()} or {@link #phenotypicAbnormalitiesStream()} instead.
   * @throws PhenolRuntimeException upon each invocation.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC2")
  default List<HpoAnnotation> getPhenotypicAbnormalities() {
    throw new PhenolRuntimeException("The 'getPhenotypicAbnormalities()` method was deprecated. Use `phenotypicAbnormalities()` instead!");
  }

  /**
   * @return stream of absent phenotypic abnormalities of the disease,
   * i.e. ones that were observed in <em>zero</em> out of <em>n</em> affected individuals.
   */
  default Stream<HpoDiseaseAnnotation> absentPhenotypicAbnormalitiesStream() {
    return phenotypicAbnormalitiesStream()
      .filter(a -> a.ratio().map(Ratio::isZero).orElse(false));
  }

  /**
   *
   * @deprecated to be removed in v3.0.0, use {@link #absentPhenotypicAbnormalitiesStream()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> negativeAnnotations() {
    return absentPhenotypicAbnormalitiesStream()
      .map(HpoDiseaseAnnotation::id)
      .collect(Collectors.toList());
  }

  /**
   * @deprecated to be removed in v3.0.0, use {@link #absentPhenotypicAbnormalitiesStream()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> getNegativeAnnotations() {
    return negativeAnnotations();
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

  /**
   * @deprecated use {@link #getPhenotypicAbnormalityTermIds()} and decide if you really need the list.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> getPhenotypicAbnormalityTermIdList() {
    return getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
  }

  /**
   * @deprecated use {@link #phenotypicAbnormalityTermIds()}.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default Stream<TermId> getPhenotypicAbnormalityTermIds() {
    return phenotypicAbnormalityTermIds();
  }


  default Stream<TermId> phenotypicAbnormalityTermIds() {
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
   * Check if {@code termId} is annotated to any of the terms to which this disease is annotated including their
   * ancestors.
   *
   * @param termId a query term.
   * @param hpo HPO ontology.
   * @return true iff this disease is annotated to the term directly or via annotation propagation.
   */
  default boolean isAnnotatedTo(TermId termId, Ontology hpo) {
    List<TermId> directAnnotations = phenotypicAbnormalitiesStream()
      .filter(a -> !a.isAbsent())
      .map(HpoDiseaseAnnotation::id)
      .collect(Collectors.toList());
    Set<TermId> ancestors = hpo.getAllAncestorTermIds(directAnnotations, true);

    return ancestors.contains(termId);
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
