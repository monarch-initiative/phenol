package org.monarchinitiative.phenol.annotations.io;

import org.monarchinitiative.phenol.annotations.disease.DiseaseFeatureFrequency;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

public class HpoAnnotationLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoAnnotationLoader.class);

  private static final Pattern RATIO_TERM = Pattern.compile("(?<numerator>\\d+)/(?<denominator>\\d+)");

  /**
   * Names of the header fields. Every valid small file should have a header line with exactly these
   * fields.
   */
  private static final String[] HEADER_FIELDS = {
    "DatabaseID",
    "DiseaseName",
    "Qualifier",
    "HPO_ID",
    "Reference",
    "Evidence",
    "Onset",
    "Frequency",
    "Sex",
    "Modifier",
    "Aspect",
    "Biocuration"
  };

  /**
   * We will, by default, parse in disease models from the following databases.
   */
  private static final Set<String> DEFAULT_DATABASE_PREFIXES = Set.of("OMIM", "ORPHA", "DECIPHER");

  private HpoAnnotationLoader() {
  }

  public static Map<TermId, HpoDisease> loadDiseaseMap(File annotationFile, Ontology ontology) throws PhenolException {
    return loadDiseaseMap(annotationFile, ontology, DEFAULT_DATABASE_PREFIXES);
  }

  public static List<HpoAnnotationLine> loadAnnotations(BufferedReader reader, Set<String> databasePrefixes) throws IOException {
    List<HpoAnnotationLine> lines = new LinkedList<>();

    String line;
    while ((line = reader.readLine()) != null) {
      if (line.startsWith("#")) {
        // header
        if (line.startsWith("#DatabaseID")) {
          List<String> errors = isValidHeaderLine(line.substring(1)); // remove leading #
          if (!errors.isEmpty()) {
            throw new IOException(String.format("Errors: %s", String.join(", ", errors)));
          }
        }
      } else {
        // body
        toHpoAnnotationLine(line, databasePrefixes)
          .ifPresent(lines::add);
      }
    }

    return lines;
  }

  public static Map<TermId, HpoDisease> loadDiseaseMap(File annotationFile, Ontology ontology, Set<String> databases) throws PhenolException {
    // TODO(pnrobinson) - do we actually need to check if a term that we find in the Frequency column is a descendant
    //  of Frequency HP:0040279? Isn't this taken care of during HPOA release process?
    //  If no, we do not require to have the Ontology here.

    // First stage of parsing is to get the lines parsed.
    List<HpoAnnotationLine> annotationLines;
    try (BufferedReader br = Files.newBufferedReader(annotationFile.toPath())) {
      annotationLines = loadAnnotations(br, databases);
    } catch (IOException e) {
      throw new PhenolException(String.format("Could not read annotation file: %s", e.getMessage()));
    }

    // Then we group the lines by disease ID
    Map<TermId, List<HpoAnnotationLine>> diseaseToAnnotationLines = annotationLines.stream()
      .collect(Collectors.groupingBy(HpoAnnotationLine::diseaseId));

    // When we get down here, we have added all the disease annotations to the diseaseToAnnotationLines
    // Now we want to transform them into HpoDisease objects
    return diseaseToAnnotationLines.values().stream()
      .map(HpoAnnotationLoader::buildDisease)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toMap(HpoDisease::diseaseDatabaseTermId, Function.identity()));
  }


  /**
   * @param line The header line of a V2 small file
   * @return true if the fields of the line exactly match {@link #HEADER_FIELDS}.
   */
  private static List<String> isValidHeaderLine(String line) {
    String[] fields = line.split("\t");
    if (fields.length != HEADER_FIELDS.length) {
      return List.of(String.format("Expected %d fields in header line but got %d", HEADER_FIELDS.length, fields.length));
    }
    for (int i = 0; i < HEADER_FIELDS.length; i++) {
      if (!fields[i].equals(HEADER_FIELDS[i])) {
        return List.of(String.format("Expected header field %d to be %s but got %s", i, HEADER_FIELDS[i], fields[i]));

      }
    }
    return List.of();
  }


  static Optional<HpoAnnotationLine> toHpoAnnotationLine(String line, Set<String> databasePrefixes) {
    String[] fields = line.split("\t");
    if (fields.length != HEADER_FIELDS.length) {
      LOGGER.warn("Annotation line with " + fields.length + " fields: \"" + line + "\"");
      return Optional.empty();
    }

    // parse columns denoting TermIds
    TermId databaseId, phenotypeId;
    try {
      // these fields are mandatory
      databaseId = TermId.of(fields[0]);
      phenotypeId = TermId.of(fields[3]);
    } catch (PhenolRuntimeException e) {
      LOGGER.warn("Invalid DatabaseID or HPO_ID field in line `{}`", line);
      return Optional.empty();
    }

    if (!databasePrefixes.contains(databaseId.getPrefix())) {
      return Optional.empty();
    }

    String dbObjectName = fields[1]; // diseaseName

    // Onset and frequency may be null, and modifiers may be empty
    HpoOnset onset = parseOnset(fields[6]).orElse(null);
    DiseaseFeatureFrequency diseaseFeatureFrequency = parseFrequency(fields[7]);
    List<TermId> modifierList = parseModifiers(fields[9]);

    String publication = fields[4];
    EvidenceCode evidence = EvidenceCode.fromString(fields[5]);
    Sex sex = Sex.fromString(fields[8]).orElse(null);
    String isNegatedString = fields[2];
    boolean isNegated = (isNegatedString != null && isNegatedString.equalsIgnoreCase("NOT"));

    Optional<Aspect> aspect = Aspect.fromString(fields[10]);
    if (aspect.isEmpty()) {
      // Aspect must be valid
      LOGGER.warn("Invalid aspect field in line `{}`", line);
      return Optional.empty();
    }

    String curationInfo = fields[11];

    HpoAnnotationLine hpoAnnotationLine = HpoAnnotationLine.of(databaseId, dbObjectName, isNegated,
      phenotypeId, publication, evidence,
      onset, diseaseFeatureFrequency, sex, modifierList, aspect.get(), curationInfo);
    return Optional.of(hpoAnnotationLine);
  }

  private static Optional<HpoOnset> parseOnset(String field) {
    if (!field.equals("")) {
      return HpoOnset.fromHpoIdString(field);
    }
    return Optional.empty();
  }

  private static List<TermId> parseModifiers(String field) {
    return Arrays.stream(field.split(";"))
      .map(HpoAnnotationLoader::parseTermOrEmpty)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
  }

  private static Optional<TermId> parseTermOrEmpty(String value) {
    try {
      return Optional.of(TermId.of(value));
    } catch (PhenolRuntimeException ignored) {
      return Optional.empty();
    }
  }

  private static DiseaseFeatureFrequency parseFrequency(String value) {
    // Might be absent ...
    if (value.equals(""))
      return null;

    // ... or an HPO term ...
    try {
      Optional<HpoFrequency> hpoFrequency = HpoFrequency.fromTermId(TermId.of(value));
      if (hpoFrequency.isPresent())
        return hpoFrequency.get();
    } catch (PhenolRuntimeException ignored) {
      // term ID is invalid, the field may still be a ration
    }

    // ... or a ratio, e.g. 10/20
    Matcher matcher = RATIO_TERM.matcher(value);
    if (matcher.matches()) {
      int numerator = Integer.parseInt(matcher.group("numerator"));
      int denominator = Integer.parseInt(matcher.group("denominator"));
      return DiseaseFeatureFrequency.exact(numerator, denominator);
    }

    return null;
  }

  private static Optional<HpoDisease> buildDisease(List<HpoAnnotationLine> annotationLines) {
    if (annotationLines.isEmpty())
      return Optional.empty();

    HpoAnnotationLine firstAnnotationLine = annotationLines.get(0);
    TermId diseaseTermId = firstAnnotationLine.diseaseId();
    String diseaseName = firstAnnotationLine.getDbObjectName();

    List<TermId> negatedAnnotations = new LinkedList<>();
    List<HpoAnnotationLine> phenotypes = new LinkedList<>();
    Set<TermId> globalOnsets = new HashSet<>();
    List<TermId> modesOfInheritance = new LinkedList<>();

    for (HpoAnnotationLine line : annotationLines) {
      if (line.isNegated()) {
        // only Aspect.PHENOTYPIC_ABNORMALITY are present or absent, all other aspects are always present
        negatedAnnotations.add(line.phenotypeId());
        continue;
      }


      // check if the `line.phenotypeId()` is a member of the phenotypic abnormality subgraph,

      switch (line.aspect()) {
        case PHENOTYPIC_ABNORMALITY:
          phenotypes.add(line);
          break;
        case INHERITANCE:
          modesOfInheritance.add(line.phenotypeId());
          break;
        case ONSET_AND_CLINICAL_COURSE:
          // global disease onset
          globalOnsets.add(line.phenotypeId());
          break;
        case CLINICAL_MODIFIER:
          // modifiers must relate to a feature and not to a disease
          LOGGER.info("Ignoring modifier line for disease {} : {} - `{}`", diseaseTermId, diseaseName, line);
          break;
        default:
          break;
      }
    }

    HpoOnset globalDiseaseOnset = globalOnsets.stream()
      .map(HpoOnset::fromTermId)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .min(HpoOnset::compareTo)
      .orElse(HpoOnset.ONSET);

    List<HpoDiseaseAnnotation> metadata = processDiseaseAnnotations(phenotypes, globalDiseaseOnset);
    return Optional.of(HpoDisease.of(diseaseName, diseaseTermId, metadata, modesOfInheritance, negatedAnnotations));
  }

  private static List<HpoDiseaseAnnotation> processDiseaseAnnotations(List<HpoAnnotationLine> phenotypes, HpoOnset globalDiseaseOnset) {
    // Process the annotations available for a single disease.
    /*
    Group all lines for a single annotation/phenotype term.
    The annotations may describe different onsets, they may come from multiple publications, etc.
    */
    Map<TermId, List<HpoAnnotationLine>> lineByPhenotype = phenotypes.stream()
      .collect(Collectors.groupingBy(HpoAnnotationLine::phenotypeId));

    List<HpoDiseaseAnnotation> annotations = new LinkedList<>();
    for (Map.Entry<TermId, List<HpoAnnotationLine>> entry : lineByPhenotype.entrySet()) {
      TermId annotationId = entry.getKey();
      List<HpoAnnotationLine> annotationLines = entry.getValue();

      List<HpoDiseaseAnnotationMetadata> metadata = processSingleAnnotation(annotationLines, globalDiseaseOnset);

      annotations.add(HpoDiseaseAnnotation.of(annotationId, metadata));
    }

    return annotations;
  }

  private static List<HpoDiseaseAnnotationMetadata> processSingleAnnotation(List<HpoAnnotationLine> annotationLines, HpoOnset globalDiseaseOnset) {
    // Process the lines available for a single annotation/phenotype term.
    // The lines may represent different onsets, frequencies of occurrence of the phenotype term, or just the same term
    // reported in multiple publications

    // TODO - think about this
    //  we may aggregate multiple publications?
    List<HpoDiseaseAnnotationMetadata> metadata = new ArrayList<>(annotationLines.size());
    for (HpoAnnotationLine line : annotationLines) {

      HpoDiseaseAnnotationMetadata meta = HpoDiseaseAnnotationMetadata.of(
        line.onset().orElse(globalDiseaseOnset),
        line.frequency().orElse(HpoFrequency.OBLIGATE),
        line.modifiers(),
        line.sex().orElse(null));

      metadata.add(meta);
    }
    return metadata;
  }

  /*
   ----------------------------------------------- OBSOLETE ------------------------------------------------------------
   */

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
    final TermId CLINICAL_MODIFIER_ROOT = TermId.of("HP:0012823");
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
    final TermId CLINICAL_COURSE = TermId.of("HP:0031797");
    return tid.equals(CLINICAL_COURSE) || existsPath(ontology, tid, CLINICAL_COURSE);
  }

}
