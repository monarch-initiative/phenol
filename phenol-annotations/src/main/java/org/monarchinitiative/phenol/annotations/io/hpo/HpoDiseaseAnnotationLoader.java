package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoModeOfInheritanceTermIds;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

public class HpoDiseaseAnnotationLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseAnnotationLoader.class);

  private static final TermId CLINICAL_MODIFIER_ROOT = TermId.of("HP:0012823");
  private static final TermId CLINICAL_COURSE = TermId.of("HP:0031797");

  private HpoDiseaseAnnotationLoader() {
  }

  public static HpoDiseases loadHpoDiseases(Path annotationPath,
                                            Ontology hpoOntology,
                                            Set<DiseaseDatabase> databases) throws IOException {
    Set<String> databasePrefixes = databases.stream()
      .map(DiseaseDatabase::prefix)
      .collect(Collectors.toUnmodifiableSet());

    // First stage of parsing is to get the lines parsed and sorted according to disease.
    Map<TermId, List<HpoAnnotationLine>> annotationLinesByDiseaseId = groupAnnotationLinesByDiseaseId(annotationPath, databasePrefixes);

    // Then, for each disease, we process the disease annotation lines into a HpoDisease object.
    List<HpoDisease> diseases = new ArrayList<>();
    for (Map.Entry<TermId, List<HpoAnnotationLine>> termIdListEntry : annotationLinesByDiseaseId.entrySet()) {
      HpoDisease hpoDisease = assembleHpoDisease(hpoOntology, termIdListEntry.getKey(), termIdListEntry.getValue());
      diseases.add(hpoDisease);
    }

    return HpoDiseases.of(List.copyOf(diseases));
  }

  private static HpoDisease assembleHpoDisease(Ontology hpoOntology,
                                               TermId diseaseId,
                                               Iterable<HpoAnnotationLine> annotationLines) {
    List<HpoAnnotation> phenoListBuilder = new ArrayList<>();
    List<TermId> inheritanceListBuilder = new ArrayList<>();
    List<TermId> negativeTermListBuilder = new ArrayList<>();
    List<TermId> clinicalModifierListBuilder = new ArrayList<>();
    List<TermId> clinicalCourseListBuilder = new ArrayList<>();
    String diseaseName = null;
    for (HpoAnnotationLine line : annotationLines) {
      try {
        if (isInheritanceTerm(line.getPhenotypeId(), hpoOntology)) {
          inheritanceListBuilder.add(line.getPhenotypeId());
        } else if (isClinicalModifierTerm(line.getPhenotypeId(), hpoOntology)) {
          clinicalModifierListBuilder.add(line.getPhenotypeId());
        } else if (isClinicalCourse(line.getPhenotypeId(), hpoOntology)) {
          clinicalCourseListBuilder.add(line.getPhenotypeId());
        } else if (line.isNOT()) {
          negativeTermListBuilder.add(line.getPhenotypeId());
        } else {
          HpoAnnotation annotation = HpoAnnotationLine.toHpoAnnotation(line, hpoOntology);
          phenoListBuilder.add(annotation);
        }
        if (line.getDbObjectName() != null)
          diseaseName = line.getDbObjectName();
      } catch (Exception e) {
        LOGGER.warn("Could not parse annotation ({}) in line {}", e.getMessage(), line);
      }
    }
    return HpoDisease.of(diseaseId, diseaseName, List.copyOf(phenoListBuilder), List.copyOf(inheritanceListBuilder), List.copyOf(negativeTermListBuilder), List.copyOf(clinicalModifierListBuilder), List.copyOf(clinicalCourseListBuilder));
  }

  private static Map<TermId, List<HpoAnnotationLine>> groupAnnotationLinesByDiseaseId(Path annotationPath, Set<String> databasePrefixes) throws IOException {
    Map<TermId, List<HpoAnnotationLine>> disease2AnnotLineMap = new HashMap<>();

    try (BufferedReader br = Files.newBufferedReader(annotationPath)) {
      String line = br.readLine();
      while (line.startsWith("#")) {
        line = br.readLine();
      } // this skips the comments (including the definition of the header)
      while ((line = br.readLine()) != null) {
        HpoAnnotationLine annotationLine;
        try {
          annotationLine = HpoAnnotationLine.constructFromString(line);
        } catch (PhenolException e) {
          LOGGER.warn("Error {} while parsing line: {}", e.getMessage(), line);
          continue;
        }

        TermId diseaseId = TermId.of(annotationLine.getDiseaseId());
        if (!databasePrefixes.contains(diseaseId.getPrefix())) {
          continue; // skip unless we want to keep this database
        }

        disease2AnnotLineMap.computeIfAbsent(diseaseId, k -> new ArrayList<>()).add(annotationLine);
      }
    }
    return disease2AnnotLineMap;
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
  private static boolean isInheritanceTerm(TermId tid, Ontology ontology) {
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
  private static boolean isClinicalModifierTerm(TermId tid, Ontology ontology) {
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
  private static boolean isClinicalCourse(TermId tid, Ontology ontology) {
    return tid.equals(CLINICAL_COURSE) || existsPath(ontology, tid, CLINICAL_COURSE);
  }
}
