package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalPoint;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represent an HPO Term together with a Frequency and an Onset and modifiers. This is intended to
 * be used to represent a disease annotation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class HpoDiseaseAnnotationDefault implements HpoDiseaseAnnotation {

  /** The annotated {@link TermId}. */
  private final TermId termId;

  private final Collection<HpoDiseaseAnnotationMetadata> metadata;

  private final Ratio ratio;

  private final List<TemporalRange> observationIntervals;

  static HpoDiseaseAnnotationDefault of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    // 1. Initialize
    int numerator = 0, denominator = 0;
    List<TemporalRange> observationIntervals = new LinkedList<>();

    // 2. Process ratio and observation intervals in a single loop
    for (HpoDiseaseAnnotationMetadata datum : metadata) {
      // Ratio
      Optional<Ratio> ratio = datum.frequency().flatMap(AnnotationFrequency::ratio);
      if (ratio.isPresent()) {
        Ratio r = ratio.get();
        numerator += r.numerator();
        denominator += r.denominator();
      }

      // Observation intervals
      Optional<TemporalRange> interval = datum.observationInterval();
      if (interval.isPresent()) {
        TemporalRange current = interval.get();

        boolean overlapFound = false;
        for (int j = 0; j < observationIntervals.size(); j++) {
          TemporalRange other = observationIntervals.get(j);
          if (current.overlapsWith(other)) {
            observationIntervals.set(j, TemporalRange.of(
              TemporalPoint.min(current.start(), other.start()),
              TemporalPoint.max(current.end(), other.end()))
            );
            overlapFound = true;
            break;
          }
        }

        if (!overlapFound) observationIntervals.add(current);
      }
    }

    // 3. Finalize
    Ratio ratio = numerator == 0 && denominator == 0
      ? null
      : Ratio.of(numerator, denominator);

    List<TemporalRange> intervals = observationIntervals.stream()
      .sorted(TemporalRange::compare)
      .collect(Collectors.toUnmodifiableList());

    return new HpoDiseaseAnnotationDefault(termId, metadata, ratio, intervals);
  }

  private HpoDiseaseAnnotationDefault(TermId termId,
                                      Collection<HpoDiseaseAnnotationMetadata> metadata,
                                      Ratio ratio,
                                      List<TemporalRange> observationIntervals) {
    this.termId = Objects.requireNonNull(termId, "Term ID must not be null");
    this.metadata = metadata;
    this.ratio = Objects.requireNonNull(ratio, "Ratio must not be null!");
    this.observationIntervals = observationIntervals;
  }

  @Override
  public TermId id() {
    return termId;
  }

  @Override
  public Stream<HpoDiseaseAnnotationMetadata> metadata() {
    return metadata.stream();
  }

  @Override
  public Ratio ratio() {
    return ratio;
  }

  @Override
  public List<TemporalRange> observationIntervals() {
    return observationIntervals;
  }

  @Override
  public Optional<Ratio> observedInInterval(TemporalRange target) {
    return metadata.stream()
      .filter(meta -> meta.observationInterval().map(interval -> interval.overlapsWith(target)).orElse(false))
      .map(meta -> meta.frequency().flatMap(AnnotationFrequency::ratio))
      .flatMap(Optional::stream)
      .reduce(Ratio::combine);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationDefault that = (HpoDiseaseAnnotationDefault) o;
    return Objects.equals(termId, that.termId) && Objects.equals(metadata, that.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(termId, metadata);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationDefault{" +
      "termId=" + termId +
      ", metadata=" + metadata +
      '}';
  }

}
