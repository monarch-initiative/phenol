package org.monarchinitiative.phenol.io;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.geneontology.obographs.model.GraphDocument;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.obo.OboGraphDocumentAdaptor;
import org.monarchinitiative.phenol.io.obo.OboGraphDocumentLoader;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

  public static Ontology loadOntology(File file) throws PhenolException {
    return loadOntology(file, CurieUtilBuilder.defaultCurieUtil());
  }

  public static Ontology loadOntology(File file, String... termIdPrefixes) throws PhenolException {
    return loadOntology(file, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static Ontology loadOntology(File file, CurieUtil curieUtil, String... termIdPrefixes) throws PhenolException {
    try {
      return loadOntology(new FileInputStream(file), curieUtil, termIdPrefixes);
    } catch (FileNotFoundException e) {
      throw new PhenolException(e);
    }
  }

  public static Ontology loadOntology(InputStream inputStream) throws PhenolException {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil());
  }

  public static Ontology loadOntology(InputStream inputStream, String... termIdPrefixes) throws PhenolException {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static Ontology loadOntology(InputStream inputStream, CurieUtil curieUtil, String... termIdPrefixes) throws PhenolException {

    GraphDocument graphDocument = loadGraphDocument(inputStream);
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

  private static GraphDocument loadGraphDocument(InputStream inputStream) throws PhenolException {
    // The input file might be json or obo/owl
    try {
      return OboGraphDocumentLoader.loadObo(inputStream);
    } catch (Exception e) {
      logger.debug("Error loading OBO", e);
    }

    try {
      return OboGraphDocumentLoader.loadJson(inputStream);
    } catch (Exception e) {
      throw new PhenolException("Unable to load ontology", e);
    }
  }
}
