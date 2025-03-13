package org.monarchinitiative.phenol.io.utils;

import org.geneontology.obographs.core.io.OgJsonReader;
import org.geneontology.obographs.core.model.GraphDocument;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.MinimalOntologyLoader;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Class with static utility method to use in ontology loaders.
 *
 * @see OntologyLoader
 * @see MinimalOntologyLoader
 */
public class OntologyLoadingRoutines {

  private static final Logger logger = LoggerFactory.getLogger(OntologyLoadingRoutines.class);

  private OntologyLoadingRoutines(){}

  public static GraphDocument loadGraphDocument(File file) {
    try (InputStream is = new BufferedInputStream(new FileInputStream(file))){
      return loadGraphDocument(is);
    } catch (IOException e) {
      throw new PhenolRuntimeException("Unable to load ontology", e);
    }
  }

  public static GraphDocument loadGraphDocument(InputStream inputStream) {
    // The input file might be json or obo/owl. Try to make an educated guess.
    try (InputStream bufferedStream = new BufferedInputStream(inputStream)) {
      int readlimit = 16;
      bufferedStream.mark(readlimit);
      String firstBytes = readBytes(bufferedStream, readlimit);
      logger.debug("Read first bytes: " + firstBytes);
      if (isJsonGraphDoc(firstBytes)) {
        logger.debug("Looks like a JSON file...");
        try {
          bufferedStream.reset();
          return OgJsonReader.readInputStream(bufferedStream);
        } catch (Exception e) {
          throw new PhenolRuntimeException("Error loading JSON", e);
        }
      } else {
        try {
          bufferedStream.reset();
        } catch (Exception e) {
          throw new PhenolRuntimeException("Error loading OBO/OWL", e);
        }
      }
      logger.debug("Looks like a OBO/OWL file...");
      logger.error("OBO/OWL support was removed since 2.0.0");
      throw new PhenolRuntimeException("OBO/OWL support was removed since 2.0.0, use JSON instead");
    } catch (IOException e) {
      throw new PhenolRuntimeException("Unable to load ontology", e);
    }
  }

  private static String readBytes(InputStream bufferedStream, int readlimit) throws IOException {
    byte[] firstFewBytes = new byte[readlimit];
    if (bufferedStream.read(firstFewBytes) == readlimit) {
      return new String(firstFewBytes);
    }
    return null;
  }

  private static boolean isJsonGraphDoc(String firstBytes) {
    return firstBytes != null && firstBytes.replace("\\W+", "").startsWith("{");
  }

}
