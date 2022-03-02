package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Age;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.TemporalRange;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.time.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a single phenotypic annotation for a disease, i.e. <em>Ectopia lentis</em> for Marfan syndrome.
 */
public interface HpoDiseaseAnnotation extends Identified, Comparable<HpoDiseaseAnnotation> {

  static HpoDiseaseAnnotation of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    double totalFrequency = metadata.stream()
      .mapToDouble(meta -> meta.frequency().frequency())
      .sum();
    if (totalFrequency > 1.0)
      throw new IllegalArgumentException("Total frequency cannot be greater than 1: " + totalFrequency);

    return HpoDiseaseAnnotationDefault.of(termId, metadata);
  }


  /**
   * @deprecated use {@link #id()}.
   */
  @Deprecated
  default TermId termId() {
    return id();
  }

  // TODO - do we really have to to expose this? Probably not.
  //  Let's make several methods for data that we need to get out of the annotation, i.e. frequencies, onsets, etc.

  Stream<HpoDiseaseAnnotationMetadata> metadata();

  default double observedInPeriod(Period period) {
    // TODO - implement
    return Double.NaN;
  }

  // TODO - implement aggregator methods for onset, etc.

  default float frequency() { // TODO - test
    List<Ratio> ratios = metadata()
      .map(HpoDiseaseAnnotationMetadata::frequency)
      .map(AnnotationFrequency::ratio)
      .flatMap(Optional::stream)
      .collect(Collectors.toList());

    int n = 0, m = 0;
    for (Ratio ratio : ratios) {
      n += ratio.numerator();
      m += ratio.denominator();
    }

    return ((float) n) / m;
  }

  default Optional<Age> earliestOnset() { // TODO - test
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalRange::start)
      .min(Age::compare);
  }

  default Optional<Age> latestOnset() { // TODO - test
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalRange::end)
      .max(Age::compare);
  }

}
