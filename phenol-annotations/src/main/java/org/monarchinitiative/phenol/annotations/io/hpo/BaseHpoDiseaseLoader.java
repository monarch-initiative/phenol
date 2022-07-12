package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.constants.hpo.HpoClinicalModifierTermIds;
import org.monarchinitiative.phenol.constants.hpo.HpoModeOfInheritanceTermIds;
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

abstract class BaseHpoDiseaseLoader implements HpoDiseaseLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoDiseaseLoaderDefault.class);

  private static final Pattern HPO_PATTERN = Pattern.compile("HP:\\d{7}");
  private static final Pattern RATIO_PATTERN = Pattern.compile("(?<numerator>\\d+)/(?<denominator>\\d+)");
  private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("(?<value>\\d+\\.?(\\d+)?)%");
  protected final HpoDiseaseAnnotationFactory factory = HpoDiseaseAnnotationFactoryDefault.instance();;

  private final int cohortSize;
  private final boolean salvageNegatedFrequencies;
  protected final Set<String> databasePrefixes;
  protected final Set<TermId> clinicalCourseSubHierarchy;
  protected final Set<TermId> inheritanceSubHierarchy;


  protected BaseHpoDiseaseLoader(Ontology hpo, HpoDiseaseLoaderOptions options) {
    Objects.requireNonNull(hpo, "HPO ontology must not be null.");
    Objects.requireNonNull(options, "Options must not be null.");
    this.cohortSize = options.cohortSize();
    this.salvageNegatedFrequencies = options.salvageNegatedFrequencies();
    this.databasePrefixes = options.includedDatabases().stream()
      .map(DiseaseDatabase::prefix)
      .collect(Collectors.toUnmodifiableSet());

    this.clinicalCourseSubHierarchy = hpo.containsTerm(HpoClinicalModifierTermIds.CLINICAL_COURSE)
      ? hpo.subOntology(HpoClinicalModifierTermIds.CLINICAL_COURSE).getNonObsoleteTermIds()
      : Set.of();
    this.inheritanceSubHierarchy = hpo.containsTerm(org.monarchinitiative.phenol.constants.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT)
      ? hpo.subOntology(HpoModeOfInheritanceTermIds.INHERITANCE_ROOT).getNonObsoleteTermIds()
      : Set.of();
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

  /**
   * Assemble {@link HpoDisease} from a number of {@link HpoAnnotationLine}s.
   * @param annotationLines the annotation lines.
   */
  protected abstract Optional<HpoDisease> assembleHpoDisease(TermId diseaseId, Iterable<HpoAnnotationLine> annotationLines);


  protected Ratio parseFrequency(boolean isNegated, String frequency) throws IllegalArgumentException {
    boolean notDone = true;
    int numerator = -1, denominator = -1;

    if ("".equals(frequency)) {
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

}
