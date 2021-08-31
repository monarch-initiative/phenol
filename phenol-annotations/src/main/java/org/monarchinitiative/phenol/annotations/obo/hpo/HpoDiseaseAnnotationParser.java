package org.monarchinitiative.phenol.annotations.obo.hpo;

import com.google.common.collect.*;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * This class parses the phenotype.hpoa file into a collection of HpoDisease objects.
 * If errors are encountered, the line is skipped and the error is logged.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:michael.gargano@jax.org">Michael Gargano</a>
 */
@Deprecated // in favor of HpoAnnotationLoader
public class HpoDiseaseAnnotationParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseAnnotationParser.class);

  private static final String DEFAULT_FREQUENCY_STRING = "n/a";

  /**
   * We will, by default, parse in disease models from the following databases.
   */
  private static final Set<String> DEFAULT_DATABASE_PREFIXES = ImmutableSet.of("OMIM", "ORPHA", "DECIPHER");
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
   * We will parse in disease models from the following databases.
   */
  private final Set<String> databasePrefixes;
  /**
   * Key: HpoPhenotypeId; Value: corresponding {@link HpoDisease} object.
   */
  private Map<TermId, Set<TermId>> phenotypeToDiseaseMap;

  private HpoDiseaseAnnotationParser(Path annotationFile, Ontology ontology) {
    this(annotationFile, ontology, DEFAULT_DATABASE_PREFIXES);
  }

  /**
   * Create {@link HpoDisease} objects from the {@code phenotype.hpoa} file
   *
   * @param annotationFile path to the the {@code phenotype.hpoa} file
   * @param ontology       reference to HPO Ontology object
   * @param databases      list of databases we will keep for parsing (from OMIM, ORPHA, DECIPHER)
   */
  private HpoDiseaseAnnotationParser(Path annotationFile, Ontology ontology, Set<String> databases) {
    this.annotationFilePath = annotationFile;
    this.ontology = ontology;
    this.diseaseMap = new HashMap<>();
    this.databasePrefixes = databases;
  }

  public static Map<TermId, HpoDisease> loadDiseaseMap(Path annotationFile, Ontology ontology) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology);
    try {
      return parser.parse();
    } catch (PhenolException e) {
      LOGGER.error("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
      throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
    }
  }

  public static Map<TermId, HpoDisease> loadDiseaseMap(Path annotationFile, Ontology ontology, Set<String> databases) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology, databases);
    try {
      return parser.parse();
    } catch (PhenolException e) {
      LOGGER.error("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
      throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
    }
  }

  /**
   * Get a map from HPO terms to diseases. This function includes OMIM, ORPHA, and DECIPHER references.
   *
   * @param annotationFile path to the the {@code phenotype.hpoa} file
   * @param ontology       reference to HPO Ontology object
   * @return map with key being an HPO TermId object, and value being a list of TermIds representing diseases.
   */
  // TODO - do we really need this?
  public static Map<TermId, Set<TermId>> loadTermToDiseaseMap(Path annotationFile, Ontology ontology) {
    HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(annotationFile, ontology);
    try {
      parser.parse(); // ignore return value for this
      return parser.getTermToDiseaseMap();
    } catch (PhenolException e) {
      LOGGER.warn("Could not load HPO annotations at " + annotationFile + ": " + e.getMessage());
    }
    throw new PhenolRuntimeException("Could not load HPO annotations at " + annotationFile);
  }

  /**
   * Extract the {@link HpoFrequency} object that corresponds to the frequency modifier in an
   * annotation line. Note that we are expecting there to be one of three kinds of frequency
   * information (an HPO term, n/m or x%). If we find nothing or there is some parsing error,
   * return the default frequency (obligate, 100%).
   *
   * @param freq The representation of the frequency, if any, in the {@code
   *             phenotype_annotation.tab} file
   * @return the corresponding {@link HpoFrequency} object or the default {@link HpoFrequency}
   * object (100%).
   */
  private static double getFrequency(String freq, Ontology ontology) {
    if (freq == null || freq.isEmpty()) {
      return HpoFrequency.OBLIGATE.mean();
    }
    int i = freq.indexOf('%');
    if (i > 0) {
      return 0.01 * Double.parseDouble(freq.substring(0, i));
    }
    i = freq.indexOf('/');
    if (i > 0 && freq.length() > (i + 1)) {
      int n = Integer.parseInt(freq.substring(0, i));
      int m = Integer.parseInt(freq.substring(i + 1));
      return (double) n / (double) m;
    }
    try {
      TermId tid = string2TermId(freq, ontology);
      if (tid != null) return HpoFrequency.fromTermId(tid).get().mean();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // if we get here we could not parse the Frequency, return the default 100%
    return HpoFrequency.OBLIGATE.mean();
  }

  /**
   * Go from HP:0000123 to the corresponding TermId
   *
   * @param hp String version of an HPO term id
   * @return corresponding {@link TermId} object
   */
  private static TermId string2TermId(String hp, Ontology ontology) {
    if (!hp.startsWith("HP:")) {
      return null;
    } else {
      TermId tid = TermId.of(hp);
      if (ontology.getTermMap().containsKey(tid)) {
        return ontology
          .getTermMap()
          .get(tid)
          .getId(); // replace alt_id with current if if necessary
      } else {
        return null;
      }
    }
  }

  /**
   * This fucnction transforms a {@link TermId} into an {@link HpoOnset} object. If the argument is
   * null, it means that no annotation for the onset was provided in the annotation line, and
   * then this function returns null.
   *
   * @param ons The {@link TermId} of an HPO Onset
   * @return The {@link HpoOnset} object corresponding to the {@link TermId} in the argument
   */
  private static HpoOnset getOnset(TermId ons) {
    if (ons == null) return HpoOnset.UNKNOWN;
    return HpoOnset.fromTermId(ons);
  }

  /**
   * @param lst List of semicolon separated HPO term ids from the modifier subontology
   * @return Immutable List of {@link TermId} objects
   */
  private static List<TermId> getModifiers(String lst) {
    ImmutableList.Builder<TermId> builder = new ImmutableList.Builder<>();
    if (lst == null || lst.isEmpty()) return builder.build(); //return empty list
    String[] modifierTermStrings = lst.split(";");
    for (String mt : modifierTermStrings) {
      TermId mtId = TermId.of(mt.trim());
      builder.add(mtId);
    }
    return builder.build();
  }

  /**
   * Create a mapping from diseaseIds (e.g., OMIM:600123) to the HPO terms that the disease
   * is directly annotated to.
   *
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
    return ImmutableMap.copyOf(tmpMap);
  }

  /**
   * Create a mapping from HPO ids to the disease Ids they are directly annotated to.
   *
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @param ontology   A reference to the HPO ontology
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
    return ImmutableMap.copyOf(tmpMap);
  }

  /**
   * Create a mapping from diseaseIds (e.g., OMIM:600123) to the HPO terms that the disease
   * is directly annotated to as well as all propagated annotations.
   *
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @param ontology   A reference to the HPO ontology
   * @return A map from disease Ids to a collection of directly annotating HPO terms
   */
  public static Map<TermId, Collection<TermId>> diseaseIdToPropagatedHpoTermIds(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      tmpMap.putIfAbsent(diseaseId, new HashSet<>());
      Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toSet()), true);
      for (TermId tid : inclAncestorTermIds) {
        tmpMap.get(diseaseId).add(tid);
      }
    }
    return ImmutableMap.copyOf(tmpMap);
  }

  /**
   * Create a mapping from HPO ids to the disease Ids they are directly annotated to and also propagated annotations.
   *
   * @param diseaseMap A disease map as generated by the {@link #loadDiseaseMap(Path, Ontology)} function
   * @param ontology   A reference to the HPO ontology
   * @return A map from HPO ids to Disease ids (direct annotations only).
   */
  public static Map<TermId, Collection<TermId>> hpoTermIdToDiseaseIdsPropagated(Map<TermId, HpoDisease> diseaseMap, Ontology ontology) {
    Map<TermId, Collection<TermId>> tmpMap = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(ontology, disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toSet()), true);
      for (TermId tid : inclAncestorTermIds) {
        tmpMap.putIfAbsent(tid, new HashSet<>());
        tmpMap.get(tid).add(diseaseId);
      }
    }
    return ImmutableMap.copyOf(tmpMap);
  }

  private Map<TermId, Set<TermId>> getTermToDiseaseMap() {
    return this.phenotypeToDiseaseMap;
  }

  /**
   * Parse the {@code phenotype.hpoa} file and return a map of diseases.
   *
   * @return a map with key: disease id, e.g., OMIM:600123, and value the corresponding HpoDisease object.
   */
  public Map<TermId, HpoDisease> parse() throws PhenolException {
//    // First stage of parsing is to get the lines parsed and sorted according to disease.
//    Map<TermId, List<HpoAnnotationLine>> disease2AnnotLineMap = new HashMap<>();
//    this.phenotypeToDiseaseMap = new HashMap<>();
//
//    try (BufferedReader br = Files.newBufferedReader(annotationFilePath)) {
//      String line = br.readLine();
//      while (line.startsWith("#")) {
//        line = br.readLine();
//      } // this skips the comments (including the definition of the header)
//      while ((line = br.readLine()) != null) {
//        HpoAnnotationLine annotationLine = HpoAnnotationLine.constructFromString(line);
//        if (!annotationLine.hasValidNumberOfFields()) {
//          LOGGER.warn(String.format("Invalid number of fields: %s", line));
//          continue;
//        }
//        phenotypeToDiseaseMap.putIfAbsent(annotationLine.getPhenotypeId(), new HashSet<>());
//        phenotypeToDiseaseMap.get(annotationLine.getPhenotypeId()).add(annotationLine.getDiseaseTermId());
//
//        TermId diseaseId = annotationLine.getDiseaseTermId();
//        disease2AnnotLineMap.putIfAbsent(diseaseId, new ArrayList<>());
//        List<HpoAnnotationLine> annots = disease2AnnotLineMap.get(diseaseId);
//        annots.add(annotationLine);
//      }
//
//    } catch (IOException e) {
//      throw new PhenolException(String.format("Could not read annotation file: %s", e.getMessage()));
//    }
//    // When we get down here, we have added all the disease annotations to the disease2AnnotLineMap
//    // Now we want to transform them into HpoDisease objects
//    for (TermId diseaseId : disease2AnnotLineMap.keySet()) {
//      String diseaseDatabasePrefix = diseaseId.getPrefix();
//      if (!databasePrefixes.contains(diseaseDatabasePrefix)) {
//        continue; // skip unless we want to keep this database
//      }
//      List<HpoAnnotationLine> annots = disease2AnnotLineMap.get(diseaseId);
//      List<HpoDiseaseAnnotation> phenoListBuilder = new LinkedList<>();
//      List<TermId> inheritanceListBuilder = new LinkedList<>();
//      List<TermId> negativeTermListBuilder = new LinkedList<>();
//      List<TermId> clinicalModifierListBuilder = new LinkedList<>();
//      List<TermId> clinicalCourseListBuilder = new LinkedList<>();
//      String diseaseName = null;
//      for (HpoAnnotationLine line : annots) {
//        try {
//          if (isInheritanceTerm(line.getPhenotypeId())) {
//            inheritanceListBuilder.add(line.getPhenotypeId());
//          } else if (isClinicalModifierTerm(line.getPhenotypeId())) {
//            clinicalModifierListBuilder.add(line.getPhenotypeId());
//          } else if (isClinicalCourse(line.getPhenotypeId())) {
//            clinicalCourseListBuilder.add(line.getPhenotypeId());
//          } else if (line.isNegated()) {
//            negativeTermListBuilder.add(line.getPhenotypeId());
//          } else {
//            phenoListBuilder.add(toHpoAnnotation(line, ontology));
//          }
//          if (line.getDbObjectName() != null) diseaseName = line.getDbObjectName();
//        } catch (Exception e) {
//          LOGGER.warn(String.format("PHENOL ERROR] Line: %s--could not parse annotation: %s ", line.toString(), e.getMessage()));
//        }
//      }
//      HpoDiseaseDefault hpoDisease =
//        new HpoDiseaseDefault(
//          diseaseName,
//          diseaseId,
//          phenoListBuilder,
//          inheritanceListBuilder,
//          negativeTermListBuilder,
//          clinicalModifierListBuilder,
//          clinicalCourseListBuilder);
//      this.diseaseMap.put(hpoDisease.getDiseaseDatabaseId(), hpoDisease);
//    }
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

}
