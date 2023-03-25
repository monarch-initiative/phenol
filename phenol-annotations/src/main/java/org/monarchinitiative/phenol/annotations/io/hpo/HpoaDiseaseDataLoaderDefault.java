package org.monarchinitiative.phenol.annotations.io.hpo;

import org.apache.commons.text.CaseUtils;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class HpoaDiseaseDataLoaderDefault implements HpoaDiseaseDataLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoaDiseaseDataLoaderDefault.class);

  private final Set<DiseaseDatabase> databasePrefixes;

  HpoaDiseaseDataLoaderDefault(Set<DiseaseDatabase> prefixes) {
    this.databasePrefixes = Objects.requireNonNull(prefixes);
  }

  @Override
  public HpoaDiseaseDataContainer loadDiseaseData(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

    Map<TermId, List<HpoAnnotationLine>> annotationLinesByDiseaseId = new HashMap<>();
    boolean header = false;
    HpoAnnotationMetadata metadata = new HpoAnnotationMetadata();
    StringBuilder metaDataBuilder = new StringBuilder();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      // Comment Lines
      if (line.startsWith("#")) {
        metaDataBuilder.append(String.format("%s\n", line.replace("#", "")));
        continue;
      }

      // Header Line
      // Build our yml for metadata to HpoaMetadata
      if (!header) {
        Constructor c = new Constructor(HpoAnnotationMetadata.class, new LoaderOptions());
        c.setPropertyUtils(new PropertyUtils() {
          @Override
          public Property getProperty(Class<? extends Object> type, String name){
            name = CaseUtils.toCamelCase(String.join(" ", name.split("\\-")), false);
            return super.getProperty(type, name);
          }
        });
        final Yaml yaml = new Yaml(c);
        metadata = yaml.load(metaDataBuilder.toString());
        header = true;
        continue;
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

    if (metadata.version == null || metadata.version.isEmpty()){
        LOGGER.error("HPOA disease data version was not found");
        throw new IOException("Missing version header line.");
    }


    List<HpoaDiseaseData> data = annotationLinesByDiseaseId.entrySet().stream()
      .map(processAnnotationEntry())
      .collect(Collectors.toList());

    return new HpoaDiseaseDataContainer(metadata.version, data);
  }

  private static Function<Map.Entry<TermId, List<HpoAnnotationLine>>, HpoaDiseaseData> processAnnotationEntry() {
    return e -> {
      // We're guaranteed to have at least one item in the list, we would not have the entry otherwise.
      String diseaseName = e.getValue().get(0).diseaseName();
      return new HpoaDiseaseData(e.getKey(), diseaseName, e.getValue());
    };
  }

}
