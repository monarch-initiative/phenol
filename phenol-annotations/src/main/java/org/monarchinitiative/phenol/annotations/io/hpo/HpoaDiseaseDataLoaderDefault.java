package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class HpoaDiseaseDataLoaderDefault implements HpoaDiseaseDataLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoaDiseaseDataLoaderDefault.class);
  private static final Pattern VERSION_PATTERN = Pattern.compile("#(version|date): (?<version>[\\w-]+)$");
  private static final Pattern HPO_VERSION_PATTERN = Pattern.compile("#hpo-version: .*/(?<hpoversion>[^/]+)/hp\\.(json|obo\\.owl|obo|owl)$", Pattern.CASE_INSENSITIVE);

  private final Set<DiseaseDatabase> databasePrefixes;

  HpoaDiseaseDataLoaderDefault(Set<DiseaseDatabase> prefixes) {
    this.databasePrefixes = Objects.requireNonNull(prefixes);
  }

  @Override
  public HpoaDiseaseDataContainer loadDiseaseData(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

    Map<TermId, List<HpoAnnotationLine>> annotationLinesByDiseaseId = new HashMap<>();
    boolean finishedParsingHeader = false;
    String version = null;
    String hpoVersion = null;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      // Comment Lines
      if (line.startsWith("#")) {
        if(line.startsWith("#version:") || line.startsWith("#date:")){
          Matcher matcher = VERSION_PATTERN.matcher(line);
          if (matcher.matches()){
            version = matcher.group("version");
            LOGGER.debug("Detected HPO annotations version {}", version);
          } else {
            LOGGER.warn("Could not parse HPO annotations version from the line {}", line);
          }
        } else if(line.toLowerCase().startsWith("#hpo-version:")){
            Matcher matcher = HPO_VERSION_PATTERN.matcher(line);
            if (matcher.matches()){
              hpoVersion = matcher.group("hpoversion");
              LOGGER.debug("HPO version used to generate the annotations {}", hpoVersion);
            } else {
              LOGGER.warn("Could not determine HPO version from the line {}", line);
            }
        }
        continue;
      }

      // The new format has a header that does not start with `#`
      if (!finishedParsingHeader) {
        if (line.startsWith("database_id")) {
          if (line.split("\t").length != 12) {
            LOGGER.error("HPOA header column does not have expected column length.");
            throw new IOException("HPOA header column does not have expected column length.");
          }
          finishedParsingHeader = true;
          continue;
        }
      }

      HpoAnnotationLine annotationLine;
      try {
        annotationLine = HpoAnnotationLine.of(line);
      } catch (PhenolException e) {
        LOGGER.warn("Error {} while parsing line: {}", e.getMessage(), line);
        continue;
      }

      if (!databasePrefixes.contains(DiseaseDatabase.fromString(annotationLine.diseaseId().getPrefix())))
        continue; // skip unless we want to keep this database

      annotationLinesByDiseaseId.computeIfAbsent(annotationLine.diseaseId(), k -> new ArrayList<>())
        .add(annotationLine);
    }

    List<HpoaDiseaseData> data = annotationLinesByDiseaseId.entrySet().stream()
      .map(processAnnotationEntry())
      .collect(Collectors.toList());

    return new HpoaDiseaseDataContainer(version, hpoVersion, data);
  }

  private static Function<Map.Entry<TermId, List<HpoAnnotationLine>>, HpoaDiseaseData> processAnnotationEntry() {
    return e -> {
      // We're guaranteed to have at least one item in the list, we would not have the entry otherwise.
      String diseaseName = e.getValue().get(0).diseaseName();
      return new HpoaDiseaseData(e.getKey(), diseaseName, e.getValue());
    };
  }

}
