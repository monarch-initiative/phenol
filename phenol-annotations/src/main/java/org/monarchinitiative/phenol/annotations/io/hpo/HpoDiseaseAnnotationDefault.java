package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalPoint;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class HpoDiseaseAnnotationDefault implements HpoDiseaseAnnotation {

  private final TermId id;
  private final Ratio ratio;
  private final List<TemporalRange> observationIntervals;
  private final Iterable<KnowsRatioAndMaybeTemporalRange> ratios;
  private final List<TermId> modifiers;
  private final List<AnnotationReference> annotationReferences;

  HpoDiseaseAnnotationDefault(TermId id,
                              Ratio ratio,
                              List<TemporalRange> observationIntervals,
                              Iterable<KnowsRatioAndMaybeTemporalRange> ratios,
                              List<TermId> modifiers,
                              List<AnnotationReference> annotationReferences) {
    this.id = Objects.requireNonNull(id);
    this.ratio = Objects.requireNonNull(ratio);
    this.observationIntervals = Objects.requireNonNull(observationIntervals);
    this.ratios = Objects.requireNonNull(ratios);
    this.modifiers = Objects.requireNonNull(modifiers);
    this.annotationReferences = Objects.requireNonNull(annotationReferences);
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public Ratio ratio() {
    return ratio;
  }

  @Override
  public Stream<TemporalRange> observationIntervals() {
    return observationIntervals.stream();
  }

  @Override
  public Ratio observedInInterval(TemporalRange interval) {
    return ratioStream()
      .filter(tr -> tr.temporalRange().map(tra -> tra.overlapsWith(interval)).orElse(false))
      .map(KnowsRatioAndMaybeTemporalRange::ratio)
      .reduce(Ratio::sum)
      .orElse(Ratio.of(0, ratio.denominator()));
  }

  @Override
  public List<TermId> modifiers() {
    return modifiers;
  }

  @Override
  public Optional<TemporalPoint> earliestOnset() {
    return ratioStream()
      .map(KnowsRatioAndMaybeTemporalRange::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalRange::start)
      .min(TemporalPoint::compare);
  }

  @Override
  public Optional<TemporalPoint> latestOnset() {
    return ratioStream()
      .map(KnowsRatioAndMaybeTemporalRange::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalRange::start)
      .max(TemporalPoint::compare);
  }

  @Override
  public Optional<TemporalPoint> earliestResolution() {
    return ratioStream()
      .map(KnowsRatioAndMaybeTemporalRange::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalRange::end)
      .min(TemporalPoint::compare);
  }

  @Override
  public Optional<TemporalPoint> latestResolution() {
    return ratioStream()
      .map(KnowsRatioAndMaybeTemporalRange::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalRange::end)
      .max(TemporalPoint::compare);
  }

  @Override
  public List<AnnotationReference> references() {
    return annotationReferences;
  }

  private Stream<KnowsRatioAndMaybeTemporalRange> ratioStream() {
    return StreamSupport.stream(ratios.spliterator(), false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoDiseaseAnnotationDefault that = (HpoDiseaseAnnotationDefault) o;
    return Objects.equals(id, that.id) && Objects.equals(ratio, that.ratio) && Objects.equals(observationIntervals, that.observationIntervals) && Objects.equals(ratios, that.ratios) && Objects.equals(modifiers, that.modifiers) && Objects.equals(annotationReferences, that.annotationReferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ratio, observationIntervals, ratios, modifiers, annotationReferences);
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationDefault{" +
      "id=" + id +
      ", ratio=" + ratio +
      ", observationIntervals=" + observationIntervals +
      ", ratios=" + ratios +
      ", modifiers=" + modifiers +
      ", annotationReferences=" + annotationReferences +
      '}';
  }
}
