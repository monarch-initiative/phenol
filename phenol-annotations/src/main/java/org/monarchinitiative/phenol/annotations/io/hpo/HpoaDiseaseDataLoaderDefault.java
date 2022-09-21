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
  // To match a line like `#date: 2021-08-02`
  private static final Pattern VERSION_PATTERN = Pattern.compile("#date: (?<version>[\\w\\d-]+)$");

  private final Set<String> databasePrefixes;

  HpoaDiseaseDataLoaderDefault(Set<String> prefixes) {
    this.databasePrefixes = Objects.requireNonNull(prefixes);
  }

  @Override
  public HpoaDiseaseDataContainer loadDiseaseData(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

    Map<TermId, List<HpoAnnotationLine>> annotationLinesByDiseaseId = new HashMap<>();
    String version = null;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      if (line.startsWith("#")) {

        if (version == null) {
          // We don't have the version yet.
          Matcher matcher = VERSION_PATTERN.matcher(line);
          if (matcher.matches())
            version = matcher.group("version");
        }

        continue;
      }

      HpoAnnotationLine annotationLine;
      try {
        annotationLine = HpoAnnotationLine.of(line);
      } catch (PhenolException e) {
        LOGGER.warn("Error {} while parsing line: {}", e.getMessage(), line);
        continue;
      }

      if (!databasePrefixes.contains(annotationLine.diseaseId().getPrefix()))
        continue; // skip unless we want to keep this database

      annotationLinesByDiseaseId.computeIfAbsent(annotationLine.diseaseId(), k -> new ArrayList<>())
        .add(annotationLine);
    }

    if (version == null)
      LOGGER.warn("HPOA disease data version was not found");

    List<HpoaDiseaseData> data = annotationLinesByDiseaseId.entrySet().stream()
      .map(processAnnotationEntry())
      .collect(Collectors.toList());

    return new HpoaDiseaseDataContainer(version, data);
  }

  private static Function<Map.Entry<TermId, List<HpoAnnotationLine>>, HpoaDiseaseData> processAnnotationEntry() {
    return e -> {
      // We're guaranteed to have at least one item in the list, we would not have the entry otherwise.
      String diseaseName = e.getValue().get(0).diseaseName();
      return new HpoaDiseaseData(e.getKey(), diseaseName, e.getValue());
    };
  }

}
