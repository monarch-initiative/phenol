package org.monarchinitiative.phenol.annotations.io;

import org.monarchinitiative.phenol.annotations.disease.Disease;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface DiseaseParser<T extends Disease> {

  List<T> parse(BufferedReader reader) throws IOException ;

  /**
   * Read input from <code>is</code> using {@link StandardCharsets#UTF_8} encoding.
   *
   * @param is input stream
   * @return list of diseases
   */
  default List<T> parse(InputStream is) throws IOException {
    return parse(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
  }

  default List<T> parse(Path path) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return parse(reader);
    }
  }
}
