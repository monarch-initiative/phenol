package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoAnnotationLine;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
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
public interface HpoDisease extends AnnotatedItem {

  static HpoDisease of(TermId diseaseId,
                       String diseaseName,
                       TemporalInterval globalOnset,
                       List<HpoDiseaseAnnotation> phenotypicAbnormalities,
                       List<TermId> modesOfInheritance) {
    return new HpoDiseaseDefault(diseaseId, diseaseName, globalOnset, phenotypicAbnormalities, modesOfInheritance);
  }

  /**
   * @return disease name, e.g. <em>Marfan syndrome</em>.
   */
  String diseaseName();

  /**
   * @return iterable over <em>ALL</em> disease annotations, both present and absent.
   */
  Collection<HpoDiseaseAnnotation> annotations();

  /**
   * @return number of annotations present in the disease.
   * @deprecated use {@link #annotations()} size.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC5")
  default int annotationCount() {
    return annotations().size();
  }

  List<TermId> modesOfInheritance();

  /*\
   *  ****************************************** Default/derived members ********************************************* *
  \*/

  /**
   * @return iterable over <em>PRESENT</em> disease annotations. These are the annotations that were observed in at least
   * one individual of a cohort.
   */
  default Iterable<HpoDiseaseAnnotation> presentAnnotations() {
    return Utils.filterIterable(annotations(), HpoDiseaseAnnotation::isPresent);
  }

  /**
   * @return iterable over <em>ABSENT</em> disease annotations. These are the annotations that were <em>NOT</em>
   * observed in any individuals of a cohort.
   */
  default Iterable<HpoDiseaseAnnotation> absentAnnotations() {
    return Utils.filterIterable(annotations(), HpoDiseaseAnnotation::isAbsent);
  }

  /**
   * @return stream of <em>ALL</em> disease annotations, both present and absent.
   */
  default Stream<HpoDiseaseAnnotation> annotationStream() {
    return StreamSupport.stream(annotations().spliterator(), false);
  }

  /**
   * @return stream of <em>PRESENT</em> disease annotations. These are the annotations that were observed in at least
   *  one individual of a cohort.
   */
  default Stream<HpoDiseaseAnnotation> presentAnnotationsStream() {
    return annotationStream()
      .filter(HpoDiseaseAnnotation::isPresent);
  }

  /**
   * @return stream of <em>ABSENT</em> disease annotations. These are the annotations that were <em>NOT</em>
   * observed in any individuals of a cohort.
   */
  default Stream<HpoDiseaseAnnotation> absentAnnotationsStream() {
    return annotationStream()
      .filter(HpoDiseaseAnnotation::isAbsent);
  }

  /**
   * @return temporal interval representing earliest and latest onset of {@link HpoDiseaseAnnotation}s of the disease.
   */
  default Optional<TemporalInterval> diseaseOnset() {
    PointInTime start = null, end = null;

    for (HpoDiseaseAnnotation annotation : presentAnnotations()) {
      Optional<PointInTime> onset = annotation.earliestOnset();
      if (onset.isPresent()) {
        start = start == null
          ? onset.get()
          : PointInTime.min(onset.get(), start);
        end = end == null
          ? onset.get()
          : PointInTime.max(onset.get(), end);
      }
    }

    return start != null && end != null
      ? Optional.of(TemporalInterval.of(start, end))
      : Optional.empty();
  }

  /**
   * Users can user this function to get the HpoTermId corresponding to a TermId
   *
   * @param termId id of the plain {@link TermId} for which we want to have the {@link HpoDiseaseAnnotation}.
   * @return optional with {@link HpoDiseaseAnnotation}
   */
  default Optional<HpoDiseaseAnnotation> getAnnotation(TermId termId) {
    return annotationStream()
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
    return getAnnotation(termId)
      .map(HpoDiseaseAnnotation::ratio);
  }

  /**
   * @return stream of disease annotation IDs.
   */
  default Stream<TermId> annotationTermIds() {
    return annotationStream()
      .map(Identified::id);
  }

  /**
   * @return a list of disease annotation IDs.
   */
  default List<TermId> annotationTermIdList() {
    return annotationTermIds()
      .collect(Collectors.toList());
  }

  /**
   * Check if {@code termId} is annotated to any of the terms to which this disease is annotated including their
   * ancestors.
   *
   * @param termId a query term.
   * @param hpo HPO ontology.
   * @return true iff this disease is annotated to the term directly or via annotation propagation.
   */
  default boolean isAnnotatedTo(TermId termId, MinimalOntology hpo) {
    return presentAnnotationsStream()
      .flatMap(a -> hpo.graph().getAncestorsStream(a.id(), true))
      .anyMatch(termId::equals);
  }

  /**
   * @param termId ID of an HPO Term
   * @return true if there is a direct annotation to termId. Does not include indirect annotations from
   * annotation propagation rule.
   */
  default boolean isDirectlyAnnotatedTo(TermId termId) {
    return presentAnnotationsStream()
      .anyMatch(annotation -> annotation.id().equals(termId));
  }

  /**
   * @param termIds Set of ids of HPO Terms
   * @return true if there is a direct annotation to any of the terms in termIds. Does not include
   * indirect annotations from annotation propagation rule.
   */
  default boolean isDirectlyAnnotatedToAnyOf(Set<TermId> termIds) {
    return presentAnnotationsStream()
      .anyMatch(annotation -> termIds.contains(annotation.id()));
  }

  /*\
   *  ********************************************** Deprecated members ********************************************** *
  \*/

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
   * @deprecated to be removed in v3.0.0, use {@link #modesOfInheritance()} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC2")
  default List<TermId> getModesOfInheritance() {
    return modesOfInheritance();
  }


  /**
   * @return iterator of <em>ALL</em> phenotypic abnormalities of the disease, both present and absent.
   * @deprecated to be removed in v2.0.0, use {@link #annotations()} instead.
   */
  @Deprecated(forRemoval = true)
  default Iterator<HpoDiseaseAnnotation> phenotypicAbnormalities() {
    return annotations().iterator();
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #annotationCount()} instead.
   */
  @Deprecated(forRemoval = true)
  default int phenotypicAbnormalitiesCount() {
    return annotationCount();
  }

  /**
   * @return stream of <em>ALL</em> phenotypic abnormalities of the disease, both present and absent.
   * @deprecated to be removed in 2.0.0, use {@link #annotationTermIds()}
   */
  @Deprecated(forRemoval = true)
  default Stream<HpoDiseaseAnnotation> phenotypicAbnormalitiesStream() {
    return StreamSupport.stream(Spliterators.spliterator(phenotypicAbnormalities(), phenotypicAbnormalitiesCount(), Spliterator.DISTINCT & Spliterator.SIZED), false);
  }

  /**
   * The method is kept for backward compatibility. However, the method throws {@link PhenolRuntimeException} upon each invocation.
   *
   * @deprecated to be removed in 2.0.0, use {@link #annotations()} or {@link #annotationStream()} instead.
   * @throws PhenolRuntimeException upon each invocation.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC2")
  default List<HpoAnnotationLine> getPhenotypicAbnormalities() {
    throw new PhenolRuntimeException("The 'getPhenotypicAbnormalities()` method was deprecated. Use `phenotypicAbnormalities()` instead!");
  }

  /**
   *
   * @deprecated to be removed in v3.0.0, use {@link #absentAnnotationsStream()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> negativeAnnotations() {
    return absentAnnotationsStream()
      .map(Identified::id)
      .collect(Collectors.toList());
  }

  /**
   * @deprecated to be removed in v3.0.0, use {@link #absentAnnotationsStream()} instead.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> getNegativeAnnotations() {
    return negativeAnnotations();
  }

  /**
   * @deprecated use {@link #getPhenotypicAbnormalityTermIds()} and decide if you really need the list.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default List<TermId> getPhenotypicAbnormalityTermIdList() {
    return getPhenotypicAbnormalityTermIds()
      .collect(Collectors.toList());
  }

  /**
   * @deprecated use {@link #annotationTermIds()}.
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default Stream<TermId> getPhenotypicAbnormalityTermIds() {
    return annotationTermIds();
  }

  /**
   * @return the count of the non-negated annotations excluding mode of inheritance
   * @deprecated use {@link #annotationCount()}
   */
  @Deprecated(since = "2.0.0-RC2", forRemoval = true)
  default long getNumberOfPhenotypeAnnotations() {
    return annotationCount();
  }

}
