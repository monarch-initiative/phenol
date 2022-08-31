package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl.RatioAndTemporalIntervalAware;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.annotations.constants.hpo.HpoSubOntologyRootTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link HpoDiseaseLoaderDefault} first maps the phenotype terms into the deprecated {@link HpoAnnotation}
 * and then maps it back to {@link HpoDiseaseAnnotation}.
 * <p>
 * The loader implements the parsing from the previous phenol versions.
 */
class HpoDiseaseLoaderDefault extends BaseHpoDiseaseLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseLoaderDefault.class);

  HpoDiseaseLoaderDefault(Ontology hpo, HpoDiseaseLoaderOptions options) {
    super(hpo, options);
  }

  @Override
  protected Optional<HpoDisease> assembleHpoDisease(TermId diseaseId,
                                                  Iterable<HpoAnnotationLine> annotationLines) {
    Optional<HpoDiseaseData> diseaseDataOptional = parseDiseaseData(annotationLines);
    if (diseaseDataOptional.isEmpty())
      return Optional.empty();
    HpoDiseaseData diseaseData = diseaseDataOptional.get();

    List<HpoAnnotation> phenotypes = diseaseData.phenotypes();
    HpoOnset onset = parseGlobalDiseaseOnset(diseaseData.clinicalCourseTerms(), phenotypes);

    Map<TermId, List<HpoAnnotation>> phenotypeById = phenotypes.stream()
      .collect(Collectors.groupingBy(HpoAnnotation::id));

    List<HpoDiseaseAnnotation> diseaseAnnotations = phenotypeById.entrySet().stream()
      .map(entry -> toDiseaseAnnotation(entry.getKey(), entry.getValue()))
      .collect(Collectors.toUnmodifiableList());

    return Optional.of(HpoDisease.of(diseaseId, diseaseData.diseaseName(),
      onset,
      diseaseAnnotations,
      Collections.unmodifiableList(diseaseData.modesOfInheritance())));
  }

  private Optional<HpoDiseaseData> parseDiseaseData(Iterable<HpoAnnotationLine> annotationLines) {
    List<HpoAnnotation> phenotypes = new ArrayList<>();
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
          phenotypes.add(createHpoAnnotation(phenotypeId, line));
        } catch (IllegalArgumentException e) {
          LOGGER.warn("Non-parsable phenotype entry `{}`: {}", line, e.getMessage());
        }
      }
    }

    return Optional.of(
      new HpoDiseaseData(diseaseName,
        phenotypes,
        modesOfInheritance,
        clinicalModifierListBuilder,
        clinicalCourse)
    );
  }

  private HpoAnnotation createHpoAnnotation(TermId phenotypeId, HpoAnnotationLine line) throws IllegalArgumentException {
    // frequency
    Ratio ratio = parseFrequency(line.isNOT(), line.getFrequency());

    // onset
    HpoOnset onset = HpoOnset.fromHpoIdString(line.getOnsetId())
      .orElse(null);

    // modifiers
    List<TermId> modifiers = Arrays.stream(line.modifiers().split(";"))
      .filter(token -> !token.isBlank())
      .map(TermId::of)
      .collect(Collectors.toList());

    // citations/publications & evidence
    List<String> citations = line.getPublication();
    EvidenceCode evidenceCode = EvidenceCode.parse(line.getEvidence());

    Sex sex = Sex.parse(line.getSex()).orElse(null);

    return HpoAnnotation.of(phenotypeId,
      AnnotationFrequency.of(ratio),
      onset,
      modifiers,
      citations,
      evidenceCode,
      sex);
  }

  private static HpoOnset parseGlobalDiseaseOnset(List<TermId> termIds, List<HpoAnnotation> phenotypes) {
    // First, let's use the onset term IDs provided by HPOA lines where `aspect==C`.
    Optional<HpoOnset> earliest = termIds.stream()
      .map(HpoOnset::fromTermId)
      .flatMap(Optional::stream)
      .min(Comparator.comparing(a -> a, TemporalInterval::compare));

    if (earliest.isPresent())
      return earliest.get();

    // If there is no such term, lets use the earliest onset of the phenotype terms. Otherwise, the onset is `null`.
    HpoOnset onset = null;
    for (HpoAnnotation phenotype : phenotypes) {
      Optional<HpoOnset> onsetOptional = phenotype.onset();
      if (onsetOptional.isPresent()) {
        HpoOnset candidate = onsetOptional.get();
        if (onset == null)
          onset = candidate;
        else {
          int result = TemporalInterval.compare(candidate, onset);
          if (result < 0)
            onset = candidate;
        }
      }
    }

    return onset;
  }

  private HpoDiseaseAnnotation toDiseaseAnnotation(TermId phenotypeId, List<HpoAnnotation> annotations) {
    List<HpoDiseaseAnnotationRecord> records = new ArrayList<>(annotations.size());
    // Assemble ratios and references in a single pass.
    for (HpoAnnotation annotation : annotations) {

      // 1. TemporalRatio consists of 2 bits: ratio and reference.
      Optional<Ratio> ratioOptional = annotation.annotationFrequency().ratio();
      if (ratioOptional.isEmpty()) // ratio is mandatory attribute
        continue;

      Ratio ratio = ratioOptional.get();
      TemporalInterval temporalInterval = annotation.onset()
        .map(onset -> TemporalInterval.openEnd(onset.start()))
        .orElse(null);

      // 2. AnnotationReference
      EvidenceCode evidence = annotation.evidence();
      List<AnnotationReference> references = new ArrayList<>(annotation.citations().size());
      for (String citation : annotation.citations()) {
        try {
          references.add(AnnotationReference.of(TermId.of(citation), evidence));
        } catch (PhenolRuntimeException e) {
          LOGGER.warn("Skipping invalid citation {} in {}", citation, annotation.id());
        }
      }

      records.add(HpoDiseaseAnnotationRecord.of(ratio, temporalInterval, references, null, annotation.modifiers()));
    }

    return HpoDiseaseAnnotation.of(phenotypeId, records);
  }

  /**
   * {@link HpoAnnotationLine}s corresponding to one disease.
   */
  private static class HpoDiseaseData {

    private final String diseaseName;
    private final List<HpoAnnotation> phenotypes;
    private final List<TermId> modesOfInheritance;
    private final List<TermId> clinicalModifiers;
    private final List<TermId> clinicalCourseTerms;

    private HpoDiseaseData(String diseaseName,
                   List<HpoAnnotation> phenotypes,
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

    private List<HpoAnnotation> phenotypes() {
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
