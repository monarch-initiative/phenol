package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link HpoDiseaseAnnotationDefault} aggregates {@link KnowsRatioAndMaybeTemporalInterval}s into a global ratio and observation intervals.
 * The global ratio represents the total number of patients presenting the {@link HpoDiseaseAnnotation} at any time,
 * and the observation intervals represent {@link TemporalInterval}s when one or more proband presented
 * the phenotype feature.
 */
class HpoDiseaseAnnotationFactoryDefault implements HpoDiseaseAnnotationFactory {

  private static final HpoDiseaseAnnotationFactoryDefault INSTANCE = new HpoDiseaseAnnotationFactoryDefault();

  static HpoDiseaseAnnotationFactoryDefault instance() {
    return INSTANCE;
  }

  @Override
  public HpoDiseaseAnnotation create(TermId id,
                                     Iterable<KnowsRatioAndMaybeTemporalInterval> ratios,
                                     List<TermId> modifiers,
                                     List<AnnotationReference> annotationReferences) {
    // 1. Initialize
    int numerator = 0, denominator = 0;
    List<TemporalInterval> observationIntervals = new LinkedList<>();

    // 2. Process ratio and observation intervals in a single loop
    for (KnowsRatioAndMaybeTemporalInterval tr : ratios) {
      // Ratio
      Ratio r = tr.ratio();
      numerator += r.numerator();
      denominator += r.denominator();

      if (r.isZero())
        continue;

      // Observation intervals
      if (tr.temporalInterval().isPresent()) {
        TemporalInterval current = tr.temporalInterval().get();

        boolean overlapFound = false;
        for (int j = 0; j < observationIntervals.size(); j++) {
          TemporalInterval other = observationIntervals.get(j);
          if (current.overlapsWith(other)) {
            observationIntervals.set(j, TemporalInterval.of(
              PointInTime.min(current.start(), other.start()),
              PointInTime.max(current.end(), other.end()))
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

    List<TemporalInterval> sorted = observationIntervals.stream()
      .sorted(TemporalInterval::compare)
      .collect(Collectors.toUnmodifiableList());


    return new HpoDiseaseAnnotationDefault(id, ratio, sorted, ratios, modifiers, annotationReferences);
  }

}
