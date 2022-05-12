package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotationFrequency;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An implementation of {@link HpoDiseaseLoader} assumes each line of the HPO annotation (HPOA) file represents a single
 * phenotypic feature.
 */
class AggregatedHpoDiseaseLoader extends BaseHpoDiseaseLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(AggregatedHpoDiseaseLoader.class);

  AggregatedHpoDiseaseLoader(Ontology hpo, HpoDiseaseLoaderOptions options) {
    super(hpo, options);
  }

  @Override
  protected Optional<HpoDisease> assembleHpoDisease(TermId diseaseId, Iterable<HpoAnnotationLine> annotationLines) {
    Optional<HpoDiseaseData> diseaseDataOptional = parseDiseaseData(annotationLines);
    if (diseaseDataOptional.isEmpty())
      return Optional.empty();
    HpoDiseaseData diseaseData = diseaseDataOptional.get();

    List<HpoDiseaseAnnotation> phenotypes = diseaseData.phenotypes();
    TemporalInterval onset = parseGlobalDiseaseOnset(diseaseData.clinicalCourseTerms(), phenotypes);

    return Optional.of(
      HpoDisease.of(diseaseId,
        diseaseData.diseaseName(),
        onset,
        phenotypes,
        Collections.unmodifiableList(diseaseData.modesOfInheritance())
      ));
  }

  private Optional<HpoDiseaseData> parseDiseaseData(Iterable<HpoAnnotationLine> annotationLines) {
    List<HpoDiseaseAnnotation> phenotypes = new ArrayList<>();
    List<TermId> modesOfInheritance = new LinkedList<>();
    List<TermId> clinicalModifierListBuilder = new ArrayList<>();
    List<TermId> clinicalCourse = new ArrayList<>();
    String diseaseName = null;
    for (HpoAnnotationLine line : annotationLines) {
      // disease name
      if (line.getDatabaseObjectName() != null)
        diseaseName = line.getDatabaseObjectName();

      // phenotype term
      TermId phenotypeId;
      try {
        phenotypeId = TermId.of(line.getPhenotypeId());
      } catch (PhenolRuntimeException e) {
        LOGGER.warn("Non-parsable phenotype term `{}`: {}", line.getPhenotypeId(), e.getMessage());
        continue;
      }


      if (inheritanceSubHierarchy.contains(phenotypeId)) {
        modesOfInheritance.add(phenotypeId);
      } else if (clinicalCourseSubHierarchy.contains(phenotypeId)) {
        clinicalCourse.add(phenotypeId);
      } else if (phenotypeId.equals(HpoSubOntologyRootTermIds.CLINICAL_MODIFIER)) {
        // The term can only be clinical modifier only if it is NOT clinical course (checked above) and it is equal to
        // clinical modifier.
        clinicalModifierListBuilder.add(phenotypeId);
      } else {
        try {
          phenotypes.add(createDiseaseAnnotation(phenotypeId, line));
        } catch (IllegalArgumentException e) {
          LOGGER.warn("Non-parsable phenotype entry `{}`: {}", line, e.getMessage());
        }
      }
    }

    if (diseaseName == null)
      return Optional.empty();

    return Optional.of(
      new HpoDiseaseData(diseaseName,
        phenotypes,
        modesOfInheritance,
        clinicalModifierListBuilder,
        clinicalCourse)
    );
  }

  private HpoDiseaseAnnotation createDiseaseAnnotation(TermId phenotypeId, HpoAnnotationLine line) throws IllegalArgumentException {
    // frequency
    AnnotationFrequency annotationFrequency = parseFrequency(line.isNOT(), line.getFrequency());

    // onset
    //    HpoOnset onset = onsetOptional.orElse(null);
    Optional<HpoOnset> onsetOptional = HpoOnset.fromHpoIdString(line.getOnsetId());
    Age earliestOnset = onsetOptional.map(HpoOnset::start).orElse(null);
    Age latestOnset = onsetOptional.map(HpoOnset::end).orElse(null);

    List<TemporalInterval> observationIntervals;
    if (onsetOptional.isPresent()) {
      HpoOnset onset = onsetOptional.get();
      observationIntervals = List.of(TemporalInterval.openEnd(onset.start()));
    } else {
      observationIntervals = List.of();
    }

    // citations/publications & evidence
    EvidenceCode evidenceCode = EvidenceCode.parse(line.getEvidence());

    List<AnnotationReference> references = new ArrayList<>(line.getPublication().size());
    for (String ref : line.getPublication()) {
      try {
        TermId referenceId = TermId.of(ref);
        references.add(AnnotationReference.of(referenceId, evidenceCode));
      } catch (PhenolRuntimeException e) {
        LOGGER.warn("Skipping non-parsable reference id {}", ref);
      }
    }

    if (annotationFrequency.ratio().isEmpty()) {
      throw new IllegalStateException("Annotation frequency must be present!");
    }

    return new AggregatedHpoDiseaseAnnotation(phenotypeId,
      annotationFrequency.ratio().get(),
      earliestOnset,
      latestOnset,
      observationIntervals,
      references);
  }

  private static TemporalInterval parseGlobalDiseaseOnset(List<TermId> termIds, List<HpoDiseaseAnnotation> phenotypes) {
    {
      // First, let's use the onset term IDs provided by HPOA lines where `aspect==C`.
      Optional<HpoOnset> earliest = termIds.stream()
        .map(HpoOnset::fromTermId)
        .flatMap(Optional::stream)
        .min(Comparator.comparing(a -> a, TemporalInterval::compare));

      if (earliest.isPresent())
        return earliest.get();
    }

    // If there is no such term, lets use the earliest onset of the phenotype terms. Otherwise, the onset is `null`.
    Age earliestOnset = null, latestOnset = null;
    for (HpoDiseaseAnnotation phenotype : phenotypes) {
      Optional<Age> earliest = phenotype.earliestOnset();
      if (earliest.isPresent()) {
        if (earliestOnset == null)
          earliestOnset = earliest.get();
        else
          earliestOnset = Age.min(earliestOnset, earliest.get());
      }
      Optional<Age> latest = phenotype.latestOnset();
      if (latest.isPresent()) {
        if (latestOnset == null)
          latestOnset = latest.get();
        else
          latestOnset = Age.max(latestOnset, latest.get());
      }
    }

    return (earliestOnset == null || latestOnset == null)
      ? null
      : TemporalInterval.of(earliestOnset, latestOnset);
  }

  /**
   * {@link HpoAnnotationLine}s corresponding to one disease.
   */
  private static class HpoDiseaseData {

    private final String diseaseName;
    private final List<HpoDiseaseAnnotation> phenotypes;
    private final List<TermId> modesOfInheritance;
    private final List<TermId> clinicalModifiers;
    private final List<TermId> clinicalCourseTerms;

    private HpoDiseaseData(String diseaseName,
                           List<HpoDiseaseAnnotation> phenotypes,
                           List<TermId> modesOfInheritance,
                           List<TermId> clinicalModifiers,
                           List<TermId> clinicalCourseTerms) {
      this.diseaseName = diseaseName;
      this.phenotypes = phenotypes;
      this.modesOfInheritance = modesOfInheritance;
      this.clinicalModifiers = clinicalModifiers;
      this.clinicalCourseTerms = clinicalCourseTerms;
    }

    private String diseaseName() {
      return diseaseName;
    }

    private List<HpoDiseaseAnnotation> phenotypes() {
      return phenotypes;
    }

    private List<TermId> modesOfInheritance() {
      return modesOfInheritance;
    }

    private List<TermId> clinicalModifiers() {
      return clinicalModifiers;
    }

    private List<TermId> clinicalCourseTerms() {
      return clinicalCourseTerms;
    }
  }

}
