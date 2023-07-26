package org.monarchinitiative.phenol.io;

import org.geneontology.obographs.core.model.GraphDocument;
import org.monarchinitiative.phenol.io.obographs.OboGraphDocumentAdaptor;
import org.monarchinitiative.phenol.io.utils.CurieUtil;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Set;

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
    GraphDocument graphDocument = OntologyLoadingRoutines.loadGraphDocument(file);
    return loadOntology(graphDocument, curieUtil, termIdPrefixes);
  }

  public static Ontology loadOntology(InputStream inputStream) {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil());
  }

  public static Ontology loadOntology(InputStream inputStream, String... termIdPrefixes) {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static Ontology loadOntology(InputStream inputStream, CurieUtil curieUtil, String... termIdPrefixes) {
    return loadOntology(inputStream, curieUtil, OntologyLoaderOptions.defaultOptions(), termIdPrefixes);
  }

  public static Ontology loadOntology(InputStream inputStream,
                                      CurieUtil curieUtil,
                                      OntologyLoaderOptions options,
                                      String... termIdPrefixes) {
    GraphDocument graphDocument = OntologyLoadingRoutines.loadGraphDocument(inputStream);
    return loadOntology(graphDocument, curieUtil, options, termIdPrefixes);
  }

  public static Ontology loadOntology(GraphDocument graphDocument, CurieUtil curieUtil, String... termIdPrefixes) {
    return loadOntology(graphDocument, curieUtil, OntologyLoaderOptions.defaultOptions(), termIdPrefixes);
  }

  public static Ontology loadOntology(GraphDocument graphDocument,
                                      CurieUtil curieUtil,
                                      OntologyLoaderOptions options,
                                      String... termIdPrefixes) {
    logger.debug("Finished loading ontology");
    logger.debug("Creating phenol ontology");
    OboGraphDocumentAdaptor graphDocumentAdaptor = OboGraphDocumentAdaptor.builder()
      .curieUtil(curieUtil)
      .wantedTermIdPrefixes(Set.of(termIdPrefixes))
      .discardNonPropagatingRelationships(options.discardNonPropagatingRelationships())
      .build(graphDocument);

    Ontology ontology = ImmutableOntology.builder()
      .metaInfo(graphDocumentAdaptor.getMetaInfo())
      .terms(graphDocumentAdaptor.getTerms())
      .relationships(graphDocumentAdaptor.getRelationships())
      .build();
    logger.debug("Parsed a total of {} terms", ontology.getTerms().size());
    return ontology;
  }

}
