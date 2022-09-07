package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public interface HpoaDiseaseDataLoader {

  static HpoaDiseaseDataLoader of(Set<String> databasePrefixes) {
    return new HpoaDiseaseDataLoaderDefault(databasePrefixes);
  }

  HpoaDiseaseDataContainer loadDiseaseData(InputStream is) throws IOException;

  default HpoaDiseaseDataContainer loadDiseaseData(Path path) throws IOException {
    try (InputStream is = openForReading(path)) {
      return loadDiseaseData(is);}
  }

  private static BufferedInputStream openForReading(Path path) throws IOException {
    InputStream is = Files.newInputStream(path);
    if (path.toFile().getName().endsWith("gz"))
      is = new GZIPInputStream(is);
    return new BufferedInputStream(is);
  }

}
