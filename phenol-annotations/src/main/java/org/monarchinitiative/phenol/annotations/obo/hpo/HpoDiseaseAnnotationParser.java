package org.monarchinitiative.phenol.annotations.obo.hpo;

import com.google.common.collect.*;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoModeOfInheritanceTermIds;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import org.monarchinitiative.phenol.ontology.data.*;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * This class parses the phenotype.hpoa file into a collection of HpoDisease objects.
 * If errors are enountered, the line is skipped and the error is added to the list
 * {@link #errors}. Client code can ask if the parsing was error-free with the method
 * {@link #validParse()} and can retrieve the errors (if any) with {@link #getErrors()}.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:michael.gargano@jax.org">Michael Gargano</a>
 */
public class HpoDiseaseAnnotationParser {
  /**
   * Path to the phenotype.hpoa annotation file.
   */
  private final String annotationFilePath;
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
  private ImmutableMultimap<TermId, TermId> phenotypeToDiseaseMap;
  /**
   * List of errors encountered during parsing of the annotation file.
   */
  private List<String> errors;
  /**
   * We will, by default, parse in disease models from the following databases.
   */
  private final Set<String> DEFAULT_DATABASE_PREFIXES = ImmutableSet.of("OMIM", "ORPHA", "DECIPHER");
  /**
   * We will parse in disease models from the following databases.
   */
  private final Set<String> databasePrefixes;


  public static Map<TermId, HpoDisease> loadDiseaseMap(String annotationFile, Ontology ontology) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology);
    try {
      return parser.parse();
    } catch (PhenolException e) {
      System.err.println("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
    }
    throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
  }

  public static Map<TermId, HpoDisease> loadDiseaseMap(String annotationFile, Ontology ontology, List<String> databases) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology, databases);
    try {
      return parser.parse();
    } catch (PhenolException e) {
      System.err.println("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
    }
    throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
  }


  private HpoDiseaseAnnotationParser(String annotationFile, Ontology ontology) {
    this.annotationFilePath = annotationFile;
    this.ontology = ontology;
    this.diseaseMap = new HashMap<>();
    databasePrefixes = DEFAULT_DATABASE_PREFIXES;
  }

  /**
   * Create {@link HpoDisease} objects from the {@code phenotype.hpoa} file
   *
   * @param annotationFile path to the the {@code phenotype.hpoa} file
   * @param ontology       reference to HPO Ontology object
   * @param databases      list of databases we will keep for parsing (from OMIM, ORPHA, DECIPHER)
   */
  private HpoDiseaseAnnotationParser(String annotationFile, Ontology ontology, List<String> databases) {
    this.annotationFilePath = annotationFile;
    this.ontology = ontology;
    this.diseaseMap = new HashMap<>();
    ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
    for (String database : databases) {
      builder.add(database);
    }
    databasePrefixes = builder.build();
  }

  private HpoDiseaseAnnotationParser(File annotationFile, Ontology ontology) {
    this.annotationFilePath = annotationFile.getAbsolutePath();
    this.ontology = ontology;
    this.diseaseMap = new HashMap<>();
    databasePrefixes = DEFAULT_DATABASE_PREFIXES;
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

  private ImmutableMultimap<TermId, TermId> getTermToDiseaseMap() {
    return this.phenotypeToDiseaseMap;
  }

  /**
   * Get a map from HPO terms to diseases. This function includes OMIM, ORPHA, and DECIPHER references.
   * @param annotationFile path to the the {@code phenotype.hpoa} file
   * @param ontology reference to HPO Ontology object
   * @return map with key being an HPO TermId object, and value being a list of TermIds representing diseases.
   */
  public static Multimap<TermId, TermId> loadTermToDiseaseMap(String annotationFile, Ontology ontology) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology);
    try {
      parser.parse(); // ignore return value for this
      return parser.getTermToDiseaseMap();
    } catch (PhenolException e) {
      System.err.println("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
    }
    throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
  }

  /**
   * Parse the {@code phenotype.hpoa} file and return a map of diseases.
   *
   * @return a map with key: disease id, e.g., OMIM:600123, and value the corresponding HpoDisease object.
   */
  public Map<TermId, HpoDisease> parse() throws PhenolException {
    // First stage of parsing is to get the lines parsed and sorted according to disease.
    Map<TermId, List<HpoAnnotationLine>> disease2AnnotLineMap = new HashMap<>();
    Multimap<TermId, TermId> termToDisease = ArrayListMultimap.create();
    ImmutableList.Builder<String> errorbuilder = new ImmutableList.Builder<>();

    try (BufferedReader br = new BufferedReader(new FileReader(this.annotationFilePath))) {
      String line = br.readLine();
      while (line.startsWith("#")) {
        line = br.readLine();
      } // this skips the comments (including the definition of the header)
      while ((line = br.readLine()) != null) {
        HpoAnnotationLine aline = HpoAnnotationLine.constructFromString(line);
        if (!aline.hasValidNumberOfFields()) {
          errorbuilder.add(String.format("Invalid number of fields: %s", line));
          continue;
        }
        if (!termToDisease.containsEntry(aline.getPhenotypeId(), aline.getDiseaseTermId())) {
          termToDisease.put(aline.getPhenotypeId(), aline.getDiseaseTermId());
        }

        TermId diseaseId = aline.getDiseaseTermId();
        disease2AnnotLineMap.putIfAbsent(diseaseId, new ArrayList<>());
        List<HpoAnnotationLine> annots = disease2AnnotLineMap.get(diseaseId);
        annots.add(aline);

      }
      ImmutableMultimap.Builder<TermId, TermId> builderTermToDisease = new ImmutableMultimap.Builder<>();
      builderTermToDisease.putAll(termToDisease);
      this.phenotypeToDiseaseMap = builderTermToDisease.build();
    } catch (IOException e) {
      throw new PhenolException(String.format("Could not read annotation file: %s", e.getMessage()));
    }
    // When we get down here, we have added all of the disease annotations to the disease2AnnotLineMap
    // Now we want to transform them into HpoDisease objects
    for (TermId diseaseId : disease2AnnotLineMap.keySet()) {
      String diseaseDatabasePrefix = diseaseId.getPrefix();
      if (!databasePrefixes.contains(diseaseDatabasePrefix)) {
        continue; // skip unless we want to keep this database
      }
      List<HpoAnnotationLine> annots = disease2AnnotLineMap.get(diseaseId);
      final ImmutableList.Builder<HpoAnnotation> phenoListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> inheritanceListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> negativeTermListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> clinicalModifierListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> clinicalCourseListBuilder = ImmutableList.builder();
      String diseaseName = null;
      for (HpoAnnotationLine line : annots) {
        try {
          if (isInheritanceTerm(line.getPhenotypeId())) {
            inheritanceListBuilder.add(line.getPhenotypeId());
          } else if (isClinicalModifierTerm(line.getPhenotypeId())) {
            clinicalModifierListBuilder.add(line.getPhenotypeId());
          } else if (isClinicalCourse(line.getPhenotypeId())) {
            clinicalCourseListBuilder.add(line.getPhenotypeId());
          } else if (line.isNOT()) {
            negativeTermListBuilder.add(line.getPhenotypeId());
          } else {
            HpoAnnotation tidm = HpoAnnotationLine.toHpoAnnotation(line, ontology);
            phenoListBuilder.add(tidm);
          }
          if (line.getDbObjectName() != null) diseaseName = line.getDbObjectName();
        } catch (Exception e) {
          errorbuilder.add(String.format("PHENOL ERROR] Line: %s--could not parse annotation: %s ",
            line.toString(), e.getMessage()));
        }
      }
      HpoDisease hpoDisease =
        new HpoDisease(
          diseaseName,
          diseaseId,
          phenoListBuilder.build(),
          inheritanceListBuilder.build(),
          negativeTermListBuilder.build(),
          clinicalModifierListBuilder.build(),
          clinicalCourseListBuilder.build());
      this.diseaseMap.put(hpoDisease.getDiseaseDatabaseId(), hpoDisease);
    }
    this.errors = errorbuilder.build();
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
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(String, Ontology)} function
   * @return A map from disease Ids to a collection of directly annotating HPO terms
   */
  public static Map<TermId, Collection<TermId>> diseaseIdToDirectHpoTermIds(Map<TermId, HpoDisease> diseaseMap) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIdList();
      tmpMap.put(diseaseId, hpoTerms);
    }
    return ImmutableMap.copyOf(tmpMap);
  }

  /**
   * Create a mapping from HPO ids to the disease Ids they are directly annotated to.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(String, Ontology)} function
   * @param ontology A reference to the HPO ontology
   * @return A map from HPO ids to Disease ids (direct annotations only).
   */
  public static
  Map<TermId, Collection<TermId>> hpoTermIdToDiseaseIdsDirect(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTermIds = disease.getPhenotypicAbnormalityTermIdList();
      for (TermId tid : hpoTermIds) {
        tmpMap.putIfAbsent(tid, new HashSet<>());
        tmpMap.get(tid).add(diseaseId);
      }
    }
    return ImmutableMap.copyOf(tmpMap);
  }

  /**
   * Create a mapping from diseaseIds (e.g., OMIM:600123) to the HPO terms that the disease
   * is directly annotated to as well as all propagated annotations.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(String, Ontology)} function
   * @param ontology A reference to the HPO ontology
   * @return A map from disease Ids to a collection of directly annotating HPO terms
   */
  public static
  Map<TermId, Collection<TermId>> diseaseIdToPropagatedHpoTermIds(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      tmpMap.putIfAbsent(diseaseId, new HashSet<>());
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIdList();
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, Sets.newHashSet(hpoTerms), true);
      for (TermId tid : inclAncestorTermIds) {
        tmpMap.get(diseaseId).add(tid);
      }
    }
    return ImmutableMap.copyOf(tmpMap);
  }

  /**
   * Create a mapping from HPO ids to the disease Ids they are directly annotated to and also propagated annotations.
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(String, Ontology)} function
   * @param ontology A reference to the HPO ontology
   * @return A map from HPO ids to Disease ids (direct annotations only).
   */
  public static
  Map<TermId, Collection<TermId>> hpoTermIdToDiseaseIdsPropagated(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTermIds = disease.getPhenotypicAbnormalityTermIdList();
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, Sets.newHashSet(hpoTermIds), true);
      for (TermId tid : inclAncestorTermIds) {
        tmpMap.putIfAbsent(tid, new HashSet<>());
        tmpMap.get(tid).add(diseaseId);
      }
    }
    return ImmutableMap.copyOf(tmpMap);
  }

}
