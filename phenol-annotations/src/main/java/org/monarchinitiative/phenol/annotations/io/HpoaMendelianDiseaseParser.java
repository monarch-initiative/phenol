package org.monarchinitiative.phenol.annotations.io;

import org.monarchinitiative.phenol.annotations.base.AgeRange;
import org.monarchinitiative.phenol.annotations.base.Lifetimes;
import org.monarchinitiative.phenol.annotations.disease.DiseaseFeature;
import org.monarchinitiative.phenol.annotations.disease.DiseaseFeatureFrequency;
import org.monarchinitiative.phenol.annotations.disease.MendelianDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class HpoaMendelianDiseaseParser implements DiseaseParser<MendelianDisease> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoaMendelianDiseaseParser.class);

  public static final Set<String> DEFAULT_DATABASE_PREFIXES = Set.of("OMIM", "ORPHA", "DECIPHER");

  private final Set<String> databasePrefixes;

  private HpoaMendelianDiseaseParser(Collection<String> databasePrefixes) {
    this.databasePrefixes = Set.copyOf(Objects.requireNonNull(databasePrefixes, "Prefixes cannot be null"));
  }

  /**
   * @return parser that recognizes <code>OMIM</code>, <code>ORPHA</code>, and <code>DECIPHER</code> diseases.
   */
  public static HpoaMendelianDiseaseParser of() {
    return of(DEFAULT_DATABASE_PREFIXES);
  }

  public static HpoaMendelianDiseaseParser of(Collection<String> databasePrefixes) {
    return new HpoaMendelianDiseaseParser(databasePrefixes);
  }

  @Override
  public List<MendelianDisease> parse(BufferedReader reader) throws IOException {
    Instant start = Instant.now();
    List<HpoAnnotationLine> annotationLines = HpoAnnotationLoader.loadAnnotations(reader, databasePrefixes);

    Map<TermId, List<HpoAnnotationLine>> lineByDisease = annotationLines.stream()
      .collect(Collectors.groupingBy(HpoAnnotationLine::diseaseId));

    List<MendelianDisease> diseases = new ArrayList<>(lineByDisease.keySet().size());
    for (Map.Entry<TermId, List<HpoAnnotationLine>> diseaseEntry : lineByDisease.entrySet()) {
      diseases.add(processDisease(diseaseEntry.getKey(), diseaseEntry.getValue()));
    }
    Duration elapsed = Duration.between(start, Instant.now());
    LOGGER.debug("Assembled {} diseases from {} lines in {}.{}s", diseases.size(), annotationLines.size(), elapsed.toSecondsPart(), elapsed.toMillis());
    return diseases;
  }

  private static MendelianDisease processDisease(TermId diseaseId, List<HpoAnnotationLine> lines) {
    // this is safe due to the nature of HPOA files. We have diseaseId if we get here, so there must be 1+ HPOA lines
    HpoAnnotationLine first = lines.get(0);
    List<TermId> modesOfInheritance = new LinkedList<>();
    Set<DiseaseFeature> diseaseFeatures = new HashSet<>(lines.size());
    for (HpoAnnotationLine line : lines) {
      switch (line.aspect()) {
        case INHERITANCE:
          modesOfInheritance.add(line.phenotypeId());
          break;
        case PHENOTYPIC_ABNORMALITY:
          diseaseFeatures.add(mapToDiseaseFeature(line));
          break;
        default:
          break;
      }
    }

    return MendelianDisease.of(diseaseId, first.getDbObjectName(), diseaseFeatures, modesOfInheritance);
  }

  private static DiseaseFeature mapToDiseaseFeature(HpoAnnotationLine line) {
    AgeRange ageRange = AgeRange.of(line.onset().orElse(HpoOnset.ONSET), Lifetimes.HUMAN.deathAge());
    DiseaseFeatureFrequency freq = line.isNegated()
      ? DiseaseFeatureFrequency.exact(0, 1) // we assume it's a case report
      : line.frequency().orElse(DiseaseFeatureFrequency.caseReport());
    return DiseaseFeature.of(line.phenotypeId(), ageRange, freq, line.sex().orElse(null));
  }

}
