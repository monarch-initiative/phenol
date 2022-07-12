package org.monarchinitiative.phenol.annotations.io.hpo.annodef;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.io.hpo.KnowsRatioAndMaybeTemporalInterval;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An {@link HpoDiseaseAnnotation} implementation that overrides earliest/latest onset/resolution methods.
 */
class HpoDiseaseAnnotationDefault implements HpoDiseaseAnnotation {

  private final TermId id;
  private final Ratio ratio;
  private final List<TemporalInterval> observationIntervals;
  private final Iterable<KnowsRatioAndMaybeTemporalInterval> ratios;
  private final List<TermId> modifiers;
  private final List<AnnotationReference> annotationReferences;

  HpoDiseaseAnnotationDefault(TermId id,
                              Ratio ratio,
                              List<TemporalInterval> observationIntervals,
                              Iterable<KnowsRatioAndMaybeTemporalInterval> ratios,
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
  public Iterable<TemporalInterval> observationIntervals() {
    return observationIntervals;
  }

  @Override
  public Ratio observedInInterval(TemporalInterval interval) {
    return ratioStream()
      .filter(tr -> tr.temporalInterval().map(tra -> tra.overlapsWith(interval)).orElse(false))
      .map(KnowsRatioAndMaybeTemporalInterval::ratio)
      .reduce(Ratio::sum)
      .orElse(Ratio.of(0, ratio.denominator()));
  }

  @Override
  public List<TermId> modifiers() {
    return modifiers;
  }

  @Override
  public Optional<PointInTime> earliestOnset() {
    return temporalIntervals()
      .map(TemporalInterval::start)
      .min(PointInTime::compare);
  }

  @Override
  public Optional<PointInTime> latestOnset() {
    return temporalIntervals()
      .map(TemporalInterval::start)
      .max(PointInTime::compare);
  }

  @Override
  public Optional<PointInTime> earliestResolution() {
    return temporalIntervals()
      .map(TemporalInterval::end)
      .min(PointInTime::compare);
  }

  @Override
  public Optional<PointInTime> latestResolution() {
    return temporalIntervals()
      .map(TemporalInterval::end)
      .max(PointInTime::compare);
  }

  @Override
  public List<AnnotationReference> references() {
    return annotationReferences;
  }

  private Stream<KnowsRatioAndMaybeTemporalInterval> ratioStream() {
    return StreamSupport.stream(ratios.spliterator(), false);
  }

  private Stream<TemporalInterval> temporalIntervals() {
    return ratioStream()
      .filter(k -> k.ratio().isPositive())
      .map(KnowsRatioAndMaybeTemporalInterval::temporalInterval)
      .flatMap(Optional::stream);
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
