package org.monarchinitiative.phenol.io;

import com.google.common.collect.ImmutableSet;
import org.geneontology.obographs.core.model.GraphDocument;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.obographs.OboGraphDocumentAdaptor;
import org.monarchinitiative.phenol.io.obographs.OboGraphDocumentLoader;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Entry class for loading an ontology from a File or InputStream. Files can be in OWL, OBO or JSON format and will be
 * handled transparently.
 * <p>
 * The default for loading from a file is that all terms will be loaded into the graph. For simpler ontologies with only
 * one namespace e.g. HPO it is safe to load the ontology without supplying any termId prefixes. For the GO, which
 * contains a mixture of GO, RO and BFO terms it is advisable to supply the 'GO' termId prefix otherwise there may be
 * relationships with RelationshipType.UNKNOWN. It is left to the user how best to specify what is loaded.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OntologyLoader {

  private static final Logger logger = LoggerFactory.getLogger(OntologyLoader.class);

  private OntologyLoader() {
  }

  public static Ontology loadOntology(File file) {
    return loadOntology(file, CurieUtilBuilder.defaultCurieUtil());
  }

  public static Ontology loadOntology(File file, String... termIdPrefixes) {
    return loadOntology(file, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static Ontology loadOntology(File file, CurieUtil curieUtil, String... termIdPrefixes) {
    GraphDocument graphDocument = loadGraphDocument(file);
    return loadOntology(graphDocument, curieUtil, termIdPrefixes);
  }

  public static Ontology loadOntology(InputStream inputStream) {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil());
  }

  public static Ontology loadOntology(InputStream inputStream, String... termIdPrefixes) {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static Ontology loadOntology(InputStream inputStream, CurieUtil curieUtil, String... termIdPrefixes) {
    GraphDocument graphDocument = loadGraphDocument(inputStream);
    return loadOntology(graphDocument, curieUtil, termIdPrefixes);
  }

  public static Ontology loadOntology(GraphDocument graphDocument, CurieUtil curieUtil, String... termIdPrefixes) {
    logger.debug("Finished loading ontology");
    logger.debug("Creating phenol ontology");
    OboGraphDocumentAdaptor graphDocumentAdaptor = OboGraphDocumentAdaptor.builder()
      .curieUtil(curieUtil)
      .wantedTermIdPrefixes(ImmutableSet.copyOf(termIdPrefixes))
      .build(graphDocument);

    Ontology ontology = graphDocumentAdaptor.buildOntology();
    logger.debug("Parsed a total of {} terms", ontology.countAllTerms());
    return ontology;
  }

  private static GraphDocument loadGraphDocument(File file) {
    try {
      return loadGraphDocument(new FileInputStream(file));
    } catch (IOException e) {
      throw new PhenolRuntimeException("Unable to load ontology", e);
    }
  }

  private static GraphDocument loadGraphDocument(InputStream inputStream) {
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
          return OboGraphDocumentLoader.loadJson(bufferedStream);
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
