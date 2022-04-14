package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

public interface HpoDiseaseLoader {

  static HpoDiseaseLoader of(Ontology hpo) {
    return of(hpo, HpoDiseaseLoaderOptions.defaultOptions());
  }

  static HpoDiseaseLoader of(Ontology hpo, HpoDiseaseLoaderOptions options) {
    return new HpoDiseaseLoaderDefault(hpo, options);
  }

  HpoDiseases load(InputStream is) throws IOException;

  /**
   *
   * @param path to HPOA file. The file is assumed to be compressed if the file name ends with <em>*.gz</em>.
   * @return diseases
   * @throws IOException in case of IO errors
   */
  default HpoDiseases load(Path path) throws IOException {
    try (InputStream is = openForReading(path)) {
      return load(is);
    }
  }

  /**
   * Get input stream. The input is assumed to be compressed if the file name ends with <em>*.gz</em>.
   */
  private static BufferedInputStream openForReading(Path path) throws IOException {
    InputStream is = Files.newInputStream(path);
    if (path.toFile().getName().endsWith("gz"))
      is = new GZIPInputStream(is);
    return new BufferedInputStream(is);
  }

}
