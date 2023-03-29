package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.constants.hpo.HpoClinicalModifierTermIds;
import org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.constants.hpo.HpoSubOntologyRootTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link HpoDiseaseLoader}. The loader uses {@link HpoaDiseaseDataLoader} to parse
 * HPOA file into {@link HpoaDiseaseData} and then maps the {@link HpoaDiseaseData} into {@link HpoDisease}.
 */
class HpoDiseaseLoaderDefault implements HpoDiseaseLoader  {

  private static final Pattern HPO_PATTERN = Pattern.compile("HP:\\d{7}");
  private static final Pattern RATIO_PATTERN = Pattern.compile("(?<numerator>\\d+)/(?<denominator>\\d+)");
  private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("(?<value>\\d+\\.?(\\d+)?)%");

  private final HpoaDiseaseDataLoader loader;
  private final int cohortSize;
  private final boolean salvageNegatedFrequencies;
  protected final Set<DiseaseDatabase> databasePrefixes;
  protected final Set<TermId> clinicalCourseSubHierarchy;
  protected final Set<TermId> inheritanceSubHierarchy;

  HpoDiseaseLoaderDefault(Ontology hpo, HpoDiseaseLoaderOptions options) {
    Objects.requireNonNull(hpo, "HPO ontology must not be null.");
    Objects.requireNonNull(options, "Options must not be null.");
    this.cohortSize = options.cohortSize();
    this.salvageNegatedFrequencies = options.salvageNegatedFrequencies();
    this.databasePrefixes = options.includedDatabases();
    this.loader = HpoaDiseaseDataLoader.of(databasePrefixes);

    this.clinicalCourseSubHierarchy = hpo.containsTerm(HpoClinicalModifierTermIds.CLINICAL_COURSE)
      ? hpo.subOntology(HpoClinicalModifierTermIds.CLINICAL_COURSE).getNonObsoleteTermIds()
      : Set.of();
    this.inheritanceSubHierarchy = hpo.containsTerm(org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT)
      ? hpo.subOntology(HpoModeOfInheritanceTermIds.INHERITANCE_ROOT).getNonObsoleteTermIds()
      : Set.of();
  }

  @Override
  public HpoDiseases load(InputStream is) throws IOException {
    // First, we load the disease data container.
    HpoaDiseaseDataContainer container = loader.loadDiseaseData(is);

    // Then, we assemble the annotation lines into a HpoDisease objects.
    List<HpoDisease> diseases = container.stream()
      .map(this::assembleIntoDisease)
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    return HpoDiseases.of(container.version().orElse(null), diseases);
  }

  /**
   * Calculate global onset based on {@code clinicalCourseTerms} (with help of {@link HpoOnset}) or from disease
   * {@code annotations}. The annotations are used if onset cannot be found based on {@code clinicalCourseTerms}.
   * {@code null} is returned if both methods fail.
   */
  private static TemporalInterval calculateGlobalOnset(Iterable<TermId> clinicalCourseTerms,
                                                Iterable<HpoDiseaseAnnotation> annotations) {
    PointInTime start = null, end = null;

    for (TermId clinicalCourse : clinicalCourseTerms) {
      Optional<HpoOnset> onset = HpoOnset.fromTermId(clinicalCourse);
      if (onset.isPresent()) {
        HpoOnset current = onset.get();
        start = start == null
          ? current.start()
          : PointInTime.min(current.start(), start);
        end = end == null
          ? current.end()
          : PointInTime.max(current.end(), end);
      }
    }

    if (start != null && end != null)
      // We're done, let's use the onset based on the clinical course terms.
      return TemporalInterval.of(start, end);

    // Derive the global onset from the phenotype features
    for (HpoDiseaseAnnotation annotation : annotations) {
      if (annotation.isPresent()) {
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
    }

    return start == null || end == null
      ? null
      : TemporalInterval.of(start, end);
  }

  private Ratio parseFrequency(boolean isNegated, String frequency) throws IllegalArgumentException {
    boolean notDone = true;
    int numerator = -1, denominator = -1;

    if (frequency == null || "".equals(frequency)) {
      // The empty string is assumed to represent a case study
      numerator = (isNegated) ? 0 : 1;
      denominator = 1;
      return Ratio.of(numerator, denominator);
    }

    // HPO term, e.g. HP:0040280 (Obligate)
    if (HPO_PATTERN.matcher(frequency).matches()) {
      HpoFrequency hpoFrequency = HpoFrequency.fromTermId(TermId.of(frequency));
      numerator = isNegated
        ? 0
        : Math.round(hpoFrequency.frequency() * cohortSize);
      denominator = cohortSize;
      notDone = false;
    }

    // Ratio, e.g. 1/2
    if (notDone) {
      Matcher matcher = RATIO_PATTERN.matcher(frequency);
      if (matcher.matches()) {
        denominator = Integer.parseInt(matcher.group("denominator"));
        int i = Integer.parseInt(matcher.group("numerator"));
        if (isNegated) {
          if (denominator == 0)
            // fix denominator in cases like `0/0`
            denominator = cohortSize;
          if (i == 0 && salvageNegatedFrequencies) {
            numerator = 0;
          } else {
            numerator = denominator - i;
          }
        } else {
          numerator = i;
        }
        notDone = false;
      }
    }

    // Percentage, e.g. 20%
    if (notDone) {
      Matcher matcher = PERCENTAGE_PATTERN.matcher(frequency);
      if (matcher.matches()) {
        float percentage = Float.parseFloat(matcher.group("value"));
        numerator = Math.round(percentage * cohortSize / 100F);
        denominator = cohortSize;
        notDone = false;
      }
    }

    if (notDone)
      // we should be done at this point
      throw new IllegalArgumentException();

    return Ratio.of(numerator, denominator);
  }

  private PartitionedHpoDiseaseLines partitionDiseaseAnnotationLines(Iterable<HpoAnnotationLine> annotationLines) {
    List<HpoAnnotationLine> phenotypes = new LinkedList<>();
    List<TermId> modesOfInheritance = new LinkedList<>();
    List<TermId> clinicalModifierListBuilder = new LinkedList<>();
    List<TermId> clinicalCourse = new LinkedList<>();
    String diseaseName = null;
    for (HpoAnnotationLine line : annotationLines) {
      // disease name
      if (diseaseName == null)
        diseaseName = line.diseaseName();

      // phenotype term
      TermId phenotypeId = line.phenotypeTermId();

      if (inheritanceSubHierarchy.contains(phenotypeId)) {
        modesOfInheritance.add(phenotypeId);
      } else if (clinicalCourseSubHierarchy.contains(phenotypeId)) {
        clinicalCourse.add(phenotypeId);
      } else if (phenotypeId.equals(HpoSubOntologyRootTermIds.CLINICAL_MODIFIER)) {
        // The term can only be clinical modifier only if it is NOT clinical course (checked above) and it is equal to
        // clinical modifier.
        clinicalModifierListBuilder.add(phenotypeId);
      } else {
        // Must be a phenotype feature.
        phenotypes.add(line);
      }
    }

    return new PartitionedHpoDiseaseLines(diseaseName,
      phenotypes,
      modesOfInheritance,
      clinicalModifierListBuilder,
      clinicalCourse);
  }

  private Optional<? extends HpoDisease> assembleIntoDisease(HpoaDiseaseData diseaseData) {
    PartitionedHpoDiseaseLines partitioned = partitionDiseaseAnnotationLines(diseaseData.annotationLines());
    Map<TermId, List<HpoAnnotationLine>> phenotypeById = partitioned.phenotypes.stream()
      .collect(Collectors.groupingBy(HpoAnnotationLine::phenotypeTermId));

    List<HpoDiseaseAnnotation> annotations = phenotypeById.entrySet().stream()
      .map(entry -> toDiseaseAnnotation(entry.getKey(), entry.getValue()))
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    TemporalInterval globalOnset = calculateGlobalOnset(partitioned.clinicalCourseTerms, annotations);

    return Optional.of(
      HpoDisease.of(
        diseaseData.id(),
        partitioned.diseaseName,
        globalOnset,
        annotations,
        partitioned.modesOfInheritance
      )
    );
  }

  /**
   * Map several {@link HpoAnnotationLine}s that describe phenotypic feature into one {@link HpoDiseaseAnnotation}.
   * <p>
   * In case the mapping fails, the reason of failure is logged and {@link Optional#empty()} is returned.
   *
   * @param phenotypeFeature ID of the phenotype feature, e.g. <code>HP:1234567</code>.
   * @param annotationLines list of {@link HpoAnnotationLine}s that correspond to the <code>phenotypeFeature</code>.
   * @return the new {@link HpoDiseaseAnnotation} or empty optional if the mapping fails.
   */
  private Optional<HpoDiseaseAnnotation> toDiseaseAnnotation(TermId phenotypeFeature,
                                                             Iterable<HpoAnnotationLine> annotationLines) {
    // (*) Parse the annotation lines.
    List<HpoDiseaseAnnotationRecord> records = new ArrayList<>();
    for (HpoAnnotationLine line : annotationLines) {
      // 1) Ratio
      Ratio ratio = parseFrequency(line.isNegated(), line.frequency());

      // 2) Onset
      //    We take the earliest, most conservative, start point and assume each feature is permanent.
      //    This is a wild, wild assumption, but it is the best we can do at the current stage.
      TemporalInterval onset = line.onset()
        .map(interval -> TemporalInterval.openEnd(interval.start()))
        .orElse(null);

      records.add(HpoDiseaseAnnotationRecord.of(ratio, onset, List.copyOf(line.annotationReferences()), line.sex(), List.copyOf(line.modifiers())));
    }

    // (*) Last, assemble the disease annotation.
    return Optional.of(HpoDiseaseAnnotation.of(phenotypeFeature, records));
  }

  /**
   * {@link HpoAnnotationLine}s corresponding to one disease.
   */
  private static class PartitionedHpoDiseaseLines {

    private final String diseaseName;
    private final List<HpoAnnotationLine> phenotypes;
    private final List<TermId> modesOfInheritance;
    private final List<TermId> clinicalModifiers;
    private final List<TermId> clinicalCourseTerms;

    private PartitionedHpoDiseaseLines(String diseaseName,
                                       List<HpoAnnotationLine> phenotypes,
                                       List<TermId> modesOfInheritance,
                                       List<TermId> clinicalModifiers,
                                       List<TermId> clinicalCourseTerms) {
      this.diseaseName = diseaseName;
      this.phenotypes = phenotypes;
      this.modesOfInheritance = modesOfInheritance;
      this.clinicalModifiers = clinicalModifiers;
      this.clinicalCourseTerms = clinicalCourseTerms;
    }
  }
}
