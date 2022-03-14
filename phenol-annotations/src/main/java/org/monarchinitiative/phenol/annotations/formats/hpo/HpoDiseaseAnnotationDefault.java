package org.monarchinitiative.phenol.annotations.formats.hpo;

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

  // TODO - implement real comparator
  private static final Comparator<HpoDiseaseAnnotation> COMPARATOR = Comparator.comparing(HpoDiseaseAnnotation::id);

  /** The annotated {@link TermId}. */
  private final TermId termId;

  private final Collection<HpoDiseaseAnnotationMetadata> metadata;

  private final Ratio ratio;

  private final List<TemporalInterval> observationIntervals;

  static HpoDiseaseAnnotationDefault of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    // 1. Initialize
    int numerator = 0, denominator = 0;
    List<TemporalInterval> observationIntervals = new LinkedList<>();

    // 2. Process ratio and observation intervals in a single loop
    for (HpoDiseaseAnnotationMetadata datum : metadata) {
      // Ratio
      Optional<Ratio> ratio = datum.frequency().ratio();
      if (ratio.isPresent()) {
        Ratio r = ratio.get();
        numerator += r.numerator();
        denominator += r.denominator();
      }

      // Observation intervals
      Optional<TemporalInterval> interval = datum.observationInterval();
      if (interval.isPresent()) {
        TemporalInterval current = interval.get();

        boolean overlapFound = false;
        for (int j = 0; j < observationIntervals.size(); j++) {
          TemporalInterval other = observationIntervals.get(j);
          if (current.overlapsWith(other)) {
            observationIntervals.set(j, TemporalInterval.of(
              Timestamp.min(current.start(), other.start()),
              Timestamp.max(current.end(), other.end()))
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

    List<TemporalInterval> intervals = observationIntervals.stream()
      .sorted(TemporalInterval::compare)
      .collect(Collectors.toUnmodifiableList());

    return new HpoDiseaseAnnotationDefault(termId, metadata, ratio, intervals);
  }

  private HpoDiseaseAnnotationDefault(TermId termId,
                                      Collection<HpoDiseaseAnnotationMetadata> metadata,
                                      Ratio ratio,
                                      List<TemporalInterval> observationIntervals) {
    this.termId = Objects.requireNonNull(termId, "Term ID must not be null");
    this.metadata = metadata;
    this.ratio = ratio; // nullable
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
  public Optional<Ratio> ratio() {
    return Optional.ofNullable(ratio);
  }

  @Override
  public List<TemporalInterval> observationIntervals() {
    return observationIntervals;
  }

  @Override
  public Optional<Ratio> observedInInterval(TemporalInterval target) {
    return metadata.stream()
      .filter(meta -> meta.observationInterval().map(interval -> interval.overlapsWith(target)).orElse(false))
      .map(meta -> meta.frequency().ratio())
      .flatMap(Optional::stream)
      .reduce(Ratio::combine);
  }

  @Override
  public int compareTo(HpoDiseaseAnnotation other) {
    return COMPARATOR.compare(this, other);
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
