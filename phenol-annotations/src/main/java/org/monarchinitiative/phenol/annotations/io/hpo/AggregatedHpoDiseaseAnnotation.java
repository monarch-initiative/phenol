package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotationMetadata;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class AggregatedHpoDiseaseAnnotation implements HpoDiseaseAnnotation {

  private final TermId id;
  private final Ratio ratio; // nullable
  private final List<TemporalInterval> observationIntervals;
  private final Age earliestOnset, latestOnset; // nullable
  private final List<AnnotationReference> references;

  AggregatedHpoDiseaseAnnotation(TermId id,
                                 Ratio ratio,
                                 Age earliestOnset,
                                 Age latestOnset,
                                 List<TemporalInterval> observationIntervals,
                                 List<AnnotationReference> references) {
    this.id = Objects.requireNonNull(id);
    this.ratio = ratio;
    this.observationIntervals = Objects.requireNonNull(observationIntervals);
    this.earliestOnset = earliestOnset;
    this.latestOnset = latestOnset;
    this.references = Objects.requireNonNull(references);
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public Stream<HpoDiseaseAnnotationMetadata> metadata() {
    return Stream.empty();
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
    return Optional.empty();
  }

  @Override
  public Optional<Age> earliestOnset() {
    return Optional.ofNullable(earliestOnset);
  }

  @Override
  public Optional<Age> latestOnset() {
    return Optional.ofNullable(latestOnset);
  }

  // we do not have the resolution data in HPOA file
  @Override
  public Optional<Age> earliestResolution() {
    return Optional.empty();
  }

  // we do not have the resolution data in HPOA file
  @Override
  public Optional<Age> latestResolution() {
    return Optional.empty();
  }

  @Override
  public List<AnnotationReference> references() {
    return references;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AggregatedHpoDiseaseAnnotation that = (AggregatedHpoDiseaseAnnotation) o;
    return Objects.equals(id, that.id) && Objects.equals(ratio, that.ratio) && Objects.equals(observationIntervals, that.observationIntervals) && Objects.equals(earliestOnset, that.earliestOnset) && Objects.equals(latestOnset, that.latestOnset) && Objects.equals(references, that.references);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ratio, observationIntervals, earliestOnset, latestOnset, references);
  }

  @Override
  public String toString() {
    return "AggregatedHpoDiseaseAnnotation{" +
      "id=" + id +
      ", ratio=" + ratio +
      ", observationIntervals=" + observationIntervals +
      ", earliestOnset=" + earliestOnset +
      ", latestOnset=" + latestOnset +
      ", references=" + references +
      '}';
  }
}
