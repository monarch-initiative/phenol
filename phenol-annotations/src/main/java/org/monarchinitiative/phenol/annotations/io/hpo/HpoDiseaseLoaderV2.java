package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
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
import java.util.stream.Collectors;

class HpoDiseaseLoaderV2 extends BaseHpoDiseaseLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseLoaderV2.class);

  HpoDiseaseLoaderV2(Ontology hpo, HpoDiseaseLoaderOptions options) {
    super(hpo, options);
  }

  @Override
  protected Optional<HpoDisease> assembleHpoDisease(TermId diseaseId,
                                                    Iterable<HpoAnnotationLine> annotationLines) {
    return partitionDiseaseAnnotationLines(annotationLines)
      .flatMap(diseaseData -> assembleIntoDisease(diseaseId, diseaseData));
  }

  private Optional<HpoDiseaseData> partitionDiseaseAnnotationLines(Iterable<HpoAnnotationLine> annotationLines) {
    List<HpoAnnotationLine> phenotypes = new ArrayList<>();
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
        // Must be a phenotype feature.
        phenotypes.add(line);
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

  private Optional<? extends HpoDisease> assembleIntoDisease(TermId diseaseId, HpoDiseaseData diseaseData) {
    Map<String, List<HpoAnnotationLine>> phenotypeById = diseaseData.phenotypes.stream()
      .collect(Collectors.groupingBy(HpoAnnotationLine::getPhenotypeId));

    List<HpoDiseaseAnnotation> annotations = phenotypeById.entrySet().stream()
      .map(entry -> toDiseaseAnnotation(entry.getKey(), entry.getValue()))
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    TemporalRange globalOnset = null; // TODO - address
    return Optional.of(
      HpoDisease.of(
        diseaseId,
        diseaseData.diseaseName(),
        globalOnset,
        annotations,
        diseaseData.modesOfInheritance()));
  }

  private Optional<HpoDiseaseAnnotation> toDiseaseAnnotation(String phenotypeFeature,
                                                             List<HpoAnnotationLine> annotationLines) {
    // 1. parse phenotype feature ID.
    TermId phenotypeFeatureId;
    try {
      phenotypeFeatureId = TermId.of(phenotypeFeature);
    } catch (PhenolRuntimeException e) {
      LOGGER.warn("Non-parsable phenotype feature ID `{}`: {}", phenotypeFeature, e.getMessage());
      return Optional.empty();
    }

    List<AnnotationReference> references = new LinkedList<>();
    List<KnowsRatioAndMaybeTemporalRange> ratios = new LinkedList<>();
    for (HpoAnnotationLine line : annotationLines) {
      // 1. Combine `ratio` and `onset` into `TemporalRatio`.
      Ratio ratio = parseFrequency(line.isNOT(), line.getFrequency());
      TemporalRange temporalRange = HpoOnset.fromHpoIdString(line.getOnsetId())
        .map(onset -> TemporalRange.openEnd(onset.start()))
        .orElse(null);
      ratios.add(KnowsRatioAndMaybeTemporalRange.of(ratio, temporalRange));

      // 2. Assemble `AnnotationReference`.
      EvidenceCode evidence = EvidenceCode.parse(line.getEvidence());
      for (String citation : line.getPublication()) {
        try {
          references.add(AnnotationReference.of(TermId.of(citation), evidence));
        } catch (PhenolRuntimeException e) {
          LOGGER.warn("Skipping invalid citation {} in {}", citation, phenotypeFeature);
        }
      }

      // TODO - do we need these?
      // modifiers
//      List<TermId> modifiers = Arrays.stream(line.modifiers().split(";"))
//        .filter(token -> !token.isBlank())
//        .map(TermId::of)
//        .collect(Collectors.toList());
//
//      Sex sex = Sex.parse(line.getSex()).orElse(null);
    }


    return Optional.of(factory.create(phenotypeFeatureId, ratios, references));
  }

  /**
   * {@link HpoAnnotationLine}s corresponding to one disease.
   */
  private static class HpoDiseaseData {

    private final String diseaseName;
    private final List<HpoAnnotationLine> phenotypes;
    private final List<TermId> modesOfInheritance;
    private final List<TermId> clinicalModifiers;
    private final List<TermId> clinicalCourseTerms;

    private HpoDiseaseData(String diseaseName,
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

    private String diseaseName() {
      return diseaseName;
    }

    private List<HpoAnnotationLine> phenotypes() {
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
