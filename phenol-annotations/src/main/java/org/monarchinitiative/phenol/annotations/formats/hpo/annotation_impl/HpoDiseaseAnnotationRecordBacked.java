package org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotationRecord;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link HpoDiseaseAnnotation} implementation backed by a {@link Collection} of {@link HpoDiseaseAnnotation}.
 */
public class HpoDiseaseAnnotationRecordBacked implements HpoDiseaseAnnotation {

  private final TermId id;
  private final Iterable<HpoDiseaseAnnotationRecord> records;
  private final TemporalRatios temporalRatios;

  public static HpoDiseaseAnnotationRecordBacked of(TermId id,
                                                    Collection<HpoDiseaseAnnotationRecord> records) {
    TemporalRatios ratios = TemporalRatios.of(records);
    return new HpoDiseaseAnnotationRecordBacked(id, ratios, records);
  }

  private HpoDiseaseAnnotationRecordBacked(TermId id,
                                           TemporalRatios ratios,
                                           Collection<HpoDiseaseAnnotationRecord> records) {
    this.id = Objects.requireNonNull(id);
    this.temporalRatios = ratios;
    this.records = Objects.requireNonNull(records);
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public Ratio ratio() {
    return temporalRatios.ratio();
  }

  @Override
  public Optional<PointInTime> earliestOnset() {
    return temporalRatios.start();
  }

  @Override
  public Optional<PointInTime> latestOnset() {
    return findEndpoint(records, TemporalInterval::start, PointInTime::max);
  }

  @Override
  public Optional<PointInTime> earliestResolution() {
    return findEndpoint(records, TemporalInterval::end, PointInTime::min);
  }

  @Override
  public Optional<PointInTime> latestResolution() {
    return temporalRatios.end();
  }

  @Override
  public int observedInInterval(TemporalInterval query) {
    return temporalRatios.observedInInterval(query);
  }

  private Stream<HpoDiseaseAnnotationRecord> records() {
    return StreamSupport.stream(records.spliterator(), false);
  }

  @Override
  public List<TermId> modifiers() {
    return records().flatMap(r -> r.modifiers().stream())
      .collect(Collectors.toList());
  }

  @Override
  public List<AnnotationReference> references() {
    return records().flatMap(r -> r.references().stream())
      .collect(Collectors.toList());
  }

  private static Optional<PointInTime> findEndpoint(Iterable<HpoDiseaseAnnotationRecord> records,
                                                    Function<TemporalInterval, PointInTime> endpointExtractor,
                                                    BiFunction<PointInTime, PointInTime, PointInTime> endpointSelector) {
    PointInTime pit = null;

    for (HpoDiseaseAnnotationRecord record : records) {
      if (record.isPresent()) {
        var onset = record.temporalInterval()
          .map(endpointExtractor)
          .orElse(null);
        pit = selectPointInTime(pit, onset, endpointSelector);
      }
    }

    return Optional.ofNullable(pit);
  }

  private static PointInTime selectPointInTime(PointInTime a,
                                               PointInTime b,
                                               BiFunction<PointInTime, PointInTime, PointInTime> f) {
    if (a == null)
      a = b;
    else
      a = b == null ? b : f.apply(a, b);
    return a;
  }

  @Override
  public String toString() {
    return "HpoDiseaseAnnotationRecordBacked{" +
      "id=" + id +
      ", records=" + records +
      ", temporalRatios=" + temporalRatios +
      '}';
  }
}
