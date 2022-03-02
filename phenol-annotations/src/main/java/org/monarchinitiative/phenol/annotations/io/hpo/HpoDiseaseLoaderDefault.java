package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.TemporalRange;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

class HpoDiseaseLoaderDefault implements HpoDiseaseLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseLoaderDefault.class);
  private static final Pattern HPO_PATTERN = Pattern.compile("HP:\\d{7}");
  private static final Pattern RATIO_PATTERN = Pattern.compile("(?<numerator>\\d+)/(?<denominator>\\d+)");
  private static final TermId CLINICAL_MODIFIER_ROOT = TermId.of("HP:0012823");
  private static final TermId CLINICAL_COURSE = TermId.of("HP:0031797");

  private final Ontology hpo;
  private final int cohortSize;
  private final boolean salvageNegatedFrequencies;
  private final Set<String> databasePrefixes;

  HpoDiseaseLoaderDefault(Ontology hpo, Options options) {
    this.hpo = Objects.requireNonNull(hpo, "Hpo ontology must not be null");
    this.cohortSize = options.cohortSize();
    this.salvageNegatedFrequencies = options.salvageNegatedFrequencies();
    this.databasePrefixes = options.includedDatabases();
  }

  @Override
  public HpoDiseases load(InputStream is) throws IOException {
    Map<TermId, List<HpoAnnotationLine>> disease2AnnotLineMap = groupLinesByDiseaseId(is);

    // Then, we assemble the annotation lines into a HpoDisease objects.
    List<HpoDisease> diseases = disease2AnnotLineMap.entrySet().stream()
      .map(entry -> assembleHpoDisease(entry.getKey(), entry.getValue()))
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    return HpoDiseases.of(diseases);
  }

  private Map<TermId, List<HpoAnnotationLine>> groupLinesByDiseaseId(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

    Map<TermId, List<HpoAnnotationLine>> annotationLinesByDiseaseId = new HashMap<>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      if (line.startsWith("#"))
        continue;

      HpoAnnotationLine annotationLine;
      try {
        annotationLine = HpoAnnotationLine.constructFromString(line);
      } catch (PhenolException e) {
        LOGGER.warn("Error {} while parsing line: {}", e.getMessage(), line);
        continue;
      }

      TermId diseaseId = TermId.of(annotationLine.getDiseaseId());
      if (!databasePrefixes.contains(diseaseId.getPrefix()))
        continue; // skip unless we want to keep this database

      annotationLinesByDiseaseId.computeIfAbsent(diseaseId, k -> new ArrayList<>()).add(annotationLine);
    }

    return annotationLinesByDiseaseId;
  }

  private Optional<HpoDisease> assembleHpoDisease(TermId diseaseId,
                                                  Iterable<HpoAnnotationLine> annotationLines) {

    Optional<HpoDiseaseData> diseaseDataOptional = parseDiseaseData(hpo, annotationLines);
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

    return Optional.of(HpoDisease.of(diseaseData.diseaseName(),
      diseaseId,
      onset,
      diseaseAnnotations,
      Collections.unmodifiableList(diseaseData.modesOfInheritance())));
  }

  private Optional<HpoDiseaseData> parseDiseaseData(Ontology hpo, Iterable<HpoAnnotationLine> annotationLines) {
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
        LOGGER.warn("Non-parsable phenotype term `" + line.getPhenotypeId() + "`: " + e.getMessage());
        continue;
      }


      if (isInheritanceTerm(hpo, phenotypeId)) {
        modesOfInheritance.add(phenotypeId);
      } else if (isClinicalModifierTerm(hpo, phenotypeId)) {
        clinicalModifierListBuilder.add(phenotypeId);
      } else if (isClinicalCourse(hpo, phenotypeId)) {
        clinicalCourse.add(phenotypeId);
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
    AnnotationFrequency annotationFrequency = parseFrequency(line.isNOT(), line.getFrequency());

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
      annotationFrequency,
      onset,
      modifiers,
      citations,
      evidenceCode,
      sex);
  }

  private AnnotationFrequency parseFrequency(boolean isNegated, String frequency) throws IllegalArgumentException {
    int numerator = -1, denominator = -1;

    if ("".equals(frequency)) {
      // The empty string is assumed to represent a case study
      numerator = (isNegated) ? 0 : 1;
      denominator = 1;
      return AnnotationFrequency.of(Ratio.of(numerator, denominator));
    }

    if (HPO_PATTERN.matcher(frequency).matches()) {
      // HPO
      HpoFrequency hpoFrequency = HpoFrequency.fromTermId(TermId.of(frequency));
      int hpoApproximate = Math.round(hpoFrequency.frequency() * cohortSize);
      numerator = isNegated
        ? cohortSize - hpoApproximate
        : hpoApproximate;
      denominator = cohortSize;
    } else {
      // Ratio
      Matcher ratioMatcher = RATIO_PATTERN.matcher(frequency);
      if (ratioMatcher.matches()) {
        denominator = Integer.parseInt(ratioMatcher.group("denominator"));
        int i = Integer.parseInt(ratioMatcher.group("numerator"));
        if (isNegated) {
          if (i == 0 && salvageNegatedFrequencies) {
            numerator = 0;
          } else {
            numerator = denominator - i;
          }
        } else {
          numerator = i;
        }
      }
    }

    if (numerator >= 0 && denominator > 0) {
      return AnnotationFrequency.of(Ratio.of(numerator, denominator));
    }

    throw new IllegalArgumentException();
  }

  /**
   * Check whether a term is a member of the inheritance subontology.
   * We check whether there is a path between the term and the root of the inheritance ontology.
   * We also check whether the term is itself the root of the inheritance ontology because
   * there cannot be a path between a term and itself.
   *
   * @param tid A term to be checked
   * @return true if tid is an inheritance term
   */
  private static boolean isInheritanceTerm(Ontology ontology, TermId tid) {
    return tid.equals(HpoModeOfInheritanceTermIds.INHERITANCE_ROOT) || existsPath(ontology, tid, HpoModeOfInheritanceTermIds.INHERITANCE_ROOT);
  }

  /**
   * Check whether a term is a member of the clinical modifier subontology.
   * We check whether there is a path between the term and the root of the clinical modifier ontology.
   * We also check whether the term is itself the root of the clinical modifier ontology because
   * there cannot be a path between a term and itself.
   *
   * @param tid A term to be checked
   * @return true if tid is a clinical modifier term
   */
  private static boolean isClinicalModifierTerm(Ontology ontology, TermId tid) {
    return tid.equals(CLINICAL_MODIFIER_ROOT) || existsPath(ontology, tid, CLINICAL_MODIFIER_ROOT);
  }

  /**
   * Check whether a term is a member of the clinical course subontology.
   * We check whether there is a path between the term and the root of the clinical course ontology.
   * We also check whether the term is itself the root of the clinical course ontology because
   * there cannot be a path between a term and itself.
   *
   * @param tid A term to be checked
   * @return true if tid is a clinical course term
   */
  private static boolean isClinicalCourse(Ontology ontology, TermId tid) {
    return tid.equals(CLINICAL_COURSE) || existsPath(ontology, tid, CLINICAL_COURSE);
  }

  private static HpoOnset parseGlobalDiseaseOnset(List<TermId> termIds, List<HpoAnnotation> phenotypes) {
    // First, let's use the onset term IDs provided by HPOA lines where `aspect==C`.
    Optional<HpoOnset> earliest = termIds.stream()
      .map(HpoOnset::fromTermId)
      .flatMap(Optional::stream)
      .min(Comparator.comparing(HpoOnset::temporalRange, TemporalRange::compare));

    if (earliest.isPresent())
      return earliest.get();

    // If there is no such term, lets use the earliest onset of the phenotype terms.
    HpoOnset onset = null;
    for (HpoAnnotation phenotype : phenotypes) {
      Optional<HpoOnset> onsetOptional = phenotype.onset();
      if (onsetOptional.isPresent()) {
        HpoOnset candidate = onsetOptional.get();
        if (onset == null)
          onset = candidate;
        else {
          int result = TemporalRange.compare(candidate.temporalRange(), onset.temporalRange());
          if (result < 0)
            onset = candidate;
        }
      }
    }

    return onset;
  }

  private static HpoDiseaseAnnotation toDiseaseAnnotation(TermId phenotypeId, List<HpoAnnotation> annotations) {
    List<HpoDiseaseAnnotationMetadata> meta = annotations.stream()
      .map(HpoDiseaseLoaderDefault::toHpoDiseaseAnnotationMetadata)
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    return HpoDiseaseAnnotation.of(phenotypeId, meta);
  }

  private static Optional<HpoDiseaseAnnotationMetadata> toHpoDiseaseAnnotationMetadata(HpoAnnotation annotation) {
    return Optional.of(
      HpoDiseaseAnnotationMetadata.of(annotation.onset()
          .map(HpoOnset::temporalRange)
          .orElse(null),
        annotation.annotationFrequency(),
        annotation.modifiers(),
        annotation.sex())
    );
  }

}
