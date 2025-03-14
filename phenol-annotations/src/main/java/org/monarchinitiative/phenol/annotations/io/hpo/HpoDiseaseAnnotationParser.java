package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoModeOfInheritanceTermIds;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase.*;
import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * This class parses the phenotype.hpoa file into a collection of HpoDisease objects.
 * If errors are enountered, the line is skipped and the error is added to the list
 * {@link #errors}. Client code can ask if the parsing was error-free with the method
 * {@link #validParse()} and can retrieve the errors (if any) with {@link #getErrors()}.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:michael.gargano@jax.org">Michael Gargano</a>
 *
 * @deprecated as of <em>2.0.0</em>. Use an instance of {@link HpoDiseaseLoader} instead.
 */
@Deprecated // in favor of HpoDiseaseAnnotationLoader
public class HpoDiseaseAnnotationParser {
  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseAnnotationParser.class);

  /**
   * We will, by default, parse in disease models from the following databases.
   */
  private static final Set<DiseaseDatabase> DEFAULT_DATABASE_PREFIXES = Set.of(OMIM, ORPHANET, DECIPHER);

  /**
   * Path to the phenotype.hpoa annotation file.
   */
  private final Path annotationFilePath;
  /**
   * Reference to the HPO Ontology object.
   */
  private final Ontology ontology;
  /**
   * Key: disease CURIE, e.g., OMIM:600100; Value: corresponding {@link HpoDisease} object.
   */
  private final Map<TermId, HpoDisease> diseaseMap;
  /**
   * Key: HpoPhenotypeId; Value: corresponding {@link HpoDisease} object.
   */
  private Map<TermId, List<TermId>> phenotypeToDiseaseMap;
  /**
   * List of errors encountered during parsing of the annotation file.
   */
  private List<String> errors;
  /**
   * We will parse in disease models from the following databases.
   */
  private final Set<DiseaseDatabase> databasePrefixes;


  public static Map<TermId, HpoDisease> loadDiseaseMap(Path annotationFile, Ontology ontology) {
    return loadDiseaseMap(annotationFile, ontology, DEFAULT_DATABASE_PREFIXES);
  }

  public static Map<TermId, HpoDisease> loadDiseaseMap(Path annotationFile, Ontology ontology, Set<DiseaseDatabase> databases) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology, databases);
    try {
      return parser.parse();
    } catch (PhenolException e) {
      LOGGER.error("Could not load HPO annotations at {}: {}", annotationFile, e.getMessage());
    }
    throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
  }

  /**
   * Get a map from HPO terms to diseases. This function includes OMIM, ORPHA, and DECIPHER references.
   * @param annotationFile path to the the {@code phenotype.hpoa} file
   * @param ontology reference to HPO Ontology object
   * @return map with key being an HPO TermId object, and value being a list of TermIds representing diseases.
   */
  public static Map<TermId, List<TermId>> loadTermToDiseaseMap(Path annotationFile, Ontology ontology, Set<DiseaseDatabase> databasePrefixes) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology, databasePrefixes);
    try {
      parser.parse(); // ignore return value for this
      return parser.getTermToDiseaseMap();
    } catch (PhenolException e) {
      System.err.println("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
    }
    throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
  }

  /**
   * Create {@link HpoDisease} objects from the {@code phenotype.hpoa} file
   *
   * @param annotationFile path to the the {@code phenotype.hpoa} file
   * @param ontology       reference to HPO Ontology object
   * @param databases      list of databases we will keep for parsing (from OMIM, ORPHA, DECIPHER)
   */
  private HpoDiseaseAnnotationParser(Path annotationFile,
                                     Ontology ontology,
                                     Set<DiseaseDatabase> databases) {
    this.annotationFilePath = annotationFile;
    this.ontology = ontology;
    this.databasePrefixes = databases;
    this.diseaseMap = new HashMap<>();
  }

  /**
   * @return true if the entire parse was error-free
   */
  public boolean validParse() {
    return errors.isEmpty();
  }

  /**
   * @return a list of Strings each of which represents one parse error.
   */
  public List<String> getErrors() {
    return errors;
  }

  private Map<TermId, List<TermId>> getTermToDiseaseMap() {
    return this.phenotypeToDiseaseMap;
  }

  /**
   * Parse the {@code phenotype.hpoa} file and return a map of diseases.
   *
   * @return a map with key: disease id, e.g., OMIM:600123, and value the corresponding HpoDisease object.
   */
  public Map<TermId, HpoDisease> parse() throws PhenolException {
    // First stage of parsing is to get the lines parsed and sorted according to disease.
    Map<TermId, List<HpoAnnotationLineOld>> disease2AnnotLineMap = new HashMap<>();
    Map<TermId, List<TermId>> termToDisease = new HashMap<>();
    this.errors = new ArrayList<>();
    try (BufferedReader br = Files.newBufferedReader(annotationFilePath)) {
      String line = br.readLine();
      while (line.startsWith("#")) {
        line = br.readLine();
      } // this skips the comments (including the definition of the header)
      while ((line = br.readLine()) != null) {
        HpoAnnotationLineOld aline = HpoAnnotationLineOld.constructFromString(line);
        if (!aline.hasValidNumberOfFields()) {
          this.errors.add(String.format("Invalid number of fields: %s", line));
          continue;
        }
        List<TermId> ids = termToDisease.computeIfAbsent(aline.getDiseaseTermId().get(), k -> new ArrayList<>());
        Optional<TermId> diseaseIdOptional = aline.getDiseaseTermId();
        if (diseaseIdOptional.isPresent()) {
          TermId diseaseId = diseaseIdOptional.get();
          if (!ids.contains(diseaseId)) ids.add(diseaseId);

          disease2AnnotLineMap.computeIfAbsent(diseaseId, k -> new ArrayList<>()).add(aline);
        }
      }
      this.phenotypeToDiseaseMap = Map.copyOf(termToDisease);
    } catch (IOException e) {
      throw new PhenolException(String.format("Could not read annotation file: %s", e.getMessage()));
    }
    // When we get down here, we have added all of the disease annotations to the disease2AnnotLineMap
    // Now we want to transform them into HpoDisease objects
    for (TermId diseaseId : disease2AnnotLineMap.keySet()) {
      String diseaseDatabasePrefix = diseaseId.getPrefix();
      DiseaseDatabase diseaseDb = fromString(diseaseDatabasePrefix);
      if (!databasePrefixes.contains(diseaseDb)) {
        continue; // skip unless we want to keep this database
      }
      List<HpoAnnotationLineOld> annots = disease2AnnotLineMap.get(diseaseId);
      List<HpoAnnotationLine> phenoListBuilder = new ArrayList<>();
      List<TermId> inheritanceListBuilder = new ArrayList<>();
      List<TermId> negativeTermListBuilder = new ArrayList<>();
      List<TermId> clinicalModifierListBuilder = new ArrayList<>();
      List<TermId> clinicalCourseListBuilder = new ArrayList<>();
      String diseaseName = null;
      for (HpoAnnotationLineOld line : annots) {
        try {
          TermId phenotypeId = TermId.of(line.getPhenotypeId());
          if (isInheritanceTerm(phenotypeId)) {
            inheritanceListBuilder.add(phenotypeId);
          } else if (isClinicalModifierTerm(phenotypeId)) {
            clinicalModifierListBuilder.add(phenotypeId);
          } else if (isClinicalCourse(phenotypeId)) {
            clinicalCourseListBuilder.add(phenotypeId);
          } else if (line.isNOT()) {
            negativeTermListBuilder.add(phenotypeId);
          } else {
            HpoAnnotationLine tidm = HpoAnnotationLineOld.toHpoAnnotation(line, ontology);
            phenoListBuilder.add(tidm);
          }
          if (line.getDatabaseObjectName() != null)
            diseaseName = line.getDatabaseObjectName();
        } catch (Exception e) {
          this.errors.add(String.format("PHENOL ERROR] Line: %s--could not parse annotation: %s ",
            line.toString(), e.getMessage()));
        }
      }
//      HpoDisease hpoDisease = HpoDisease.of(diseaseId, diseaseName,
//        List.copyOf(phenoListBuilder),
//        List.copyOf(inheritanceListBuilder),
//        List.copyOf(negativeTermListBuilder),
//        List.copyOf(clinicalModifiers),
//        List.copyOf(clinicalCourseTerms));
//      this.diseaseMap.put(hpoDisease.getDiseaseDatabaseId(), hpoDisease);
    }
    return diseaseMap;
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
  private boolean isInheritanceTerm(TermId tid) {
    return tid.equals(HpoModeOfInheritanceTermIds.INHERITANCE_ROOT) || existsPath(this.ontology, tid, HpoModeOfInheritanceTermIds.INHERITANCE_ROOT);
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
  private boolean isClinicalModifierTerm(TermId tid) {
    final TermId CLINICAL_MODIFIER_ROOT = TermId.of("HP:0012823");
    return tid.equals(CLINICAL_MODIFIER_ROOT) || existsPath(this.ontology, tid, CLINICAL_MODIFIER_ROOT);
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
  private boolean isClinicalCourse(TermId tid) {
    final TermId CLINICAL_COURSE = TermId.of("HP:0031797");
    return tid.equals(CLINICAL_COURSE) || existsPath(this.ontology, tid, CLINICAL_COURSE);
  }

  /**
   * Create a mapping from diseaseIds (e.g., OMIM:600123) to the HPO terms that the disease
   * is directly annotated to.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @return A map from disease Ids to a collection of directly annotating HPO terms
   */
  public static Map<TermId, Collection<TermId>> diseaseIdToDirectHpoTermIds(Map<TermId, HpoDisease> diseaseMap) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
      tmpMap.put(diseaseId, hpoTerms);
    }
    return Map.copyOf(tmpMap);
  }

  /**
   * Create a mapping from HPO ids to the disease Ids they are directly annotated to.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @param ontology A reference to the HPO ontology
   * @return A map from HPO ids to Disease ids (direct annotations only).
   */
  public static Map<TermId, Collection<TermId>> hpoTermIdToDiseaseIdsDirect(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTermIds = disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
      for (TermId tid : hpoTermIds) {
        tmpMap.putIfAbsent(tid, new HashSet<>());
        tmpMap.get(tid).add(diseaseId);
      }
    }
    return Map.copyOf(tmpMap);
  }

  /**
   * Create a mapping from diseaseIds (e.g., OMIM:600123) to the HPO terms that the disease
   * is directly annotated to as well as all propagated annotations.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @param ontology A reference to the HPO ontology
   * @return A map from disease Ids to a collection of directly annotating HPO terms
   */
  public static Map<TermId, Collection<TermId>> diseaseIdToPropagatedHpoTermIds(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      tmpMap.putIfAbsent(diseaseId, new HashSet<>());
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, new HashSet<>(hpoTerms), true);
      for (TermId tid : inclAncestorTermIds) {
        tmpMap.get(diseaseId).add(tid);
      }
    }
    return Map.copyOf(tmpMap);
  }

  /**
   * Create a mapping from HPO ids to the disease Ids they are directly annotated to and also propagated annotations.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @param ontology A reference to the HPO ontology
   * @return A map from HPO ids to Disease ids (direct annotations only).
   */
  public static Map<TermId, Collection<TermId>> hpoTermIdToDiseaseIdsPropagated(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTermIds = disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, new HashSet<>(hpoTermIds), true);
      for (TermId tid : inclAncestorTermIds) {
        tmpMap.putIfAbsent(tid, new HashSet<>());
        tmpMap.get(tid).add(diseaseId);
      }
    }
    return Map.copyOf(tmpMap);
  }

}
