package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.AgeSinceBirth;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a single phenotypic annotation for a disease, i.e. <em>Ectopia lentis</em> for Marfan syndrome.
 */
public interface HpoDiseaseAnnotation extends Identified, Comparable<HpoDiseaseAnnotation> {

  static HpoDiseaseAnnotation of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    float frequency = metadata.stream()
      .map(meta -> meta.frequency().ratio())
      .flatMap(Optional::stream)
      .reduce(Ratio::combine)
      .map(Ratio::frequency)
      .orElse(0F);

    if (frequency > 1.0)
      throw new IllegalArgumentException("Total frequency cannot be greater than 1: " + frequency);

    return HpoDiseaseAnnotationDefault.of(termId, metadata);
  }


  /**
   * @deprecated use {@link #id()}.
   */
  @Deprecated(since = "2.0.0-RC1", forRemoval = true)
  default TermId termId() {
    return id();
  }

  Stream<HpoDiseaseAnnotationMetadata> metadata();

  /**
   * @return ratio representing number of individuals with this {@link HpoDiseaseAnnotation} or an empty optional
   * if no occurrence data is available.
   */
  Optional<Ratio> ratio();

  /**
   * @return list of {@link TemporalInterval}s representing periods when the {@link HpoDiseaseAnnotation} is observable.
   */
  List<TemporalInterval> observationIntervals();

  /**
   * @param target temporal interval
   * @return ratio of patients with {@link HpoDiseaseAnnotation} observable in given <code>target</code> {@link TemporalInterval}
   * or an empty {@link Optional} if no occurrence data is available
   */
  Optional<Ratio> observedInInterval(TemporalInterval target);

  /* **************************************************************************************************************** */

  default Optional<AgeSinceBirth> earliestOnset() {
    return metadata()
      .filter(meta -> meta.frequency().numerator().map(numerator -> numerator != 0).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::start)
      .min(AgeSinceBirth::compare);
  }

  default Optional<AgeSinceBirth> latestOnset() {
    return metadata()
      .filter(meta -> meta.frequency().numerator().map(numerator -> numerator != 0).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::start)
      .max(AgeSinceBirth::compare);
  }

  default Optional<AgeSinceBirth> earliestResolution() {
    return metadata()
      .filter(meta -> meta.frequency().numerator().map(numerator -> numerator != 0).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::end)
      .min(AgeSinceBirth::compare);
  }

  default Optional<AgeSinceBirth> latestResolution() {
    return metadata()
      .filter(meta -> meta.frequency().numerator().map(numerator -> numerator != 0).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::end)
      .max(AgeSinceBirth::compare);
  }

}
