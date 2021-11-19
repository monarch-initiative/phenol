package org.monarchinitiative.phenol.io.obographs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geneontology.obographs.core.model.GraphDocument;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads an ontology using the obograph library.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OboGraphDocumentLoader {

  private OboGraphDocumentLoader() {
  }

  public static GraphDocument loadJson(Path path) throws IOException {
    try (InputStream is = Files.newInputStream(path)) {
      return loadJson(is);
    }
  }

  public static GraphDocument loadJson(InputStream inputStream) throws IOException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(inputStream, GraphDocument.class);
  }

}
