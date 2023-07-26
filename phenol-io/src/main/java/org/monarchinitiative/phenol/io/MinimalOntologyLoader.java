package org.monarchinitiative.phenol.io;

import org.geneontology.obographs.core.model.GraphDocument;
import org.monarchinitiative.phenol.io.obographs.OboGraphDocumentAdaptor;
import org.monarchinitiative.phenol.io.utils.CurieUtil;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.impl.SimpleMinimalOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

/**
 * Entry class for loading a {@link MinimalOntology} implemented using vanilla Java.
 * <p>
 * The loader methods start from a {@linkplain File} or an {@link InputStream} and can read ontologies in OWL, OBO,
 * or JSON format.
 * <p>
 * See {@link OntologyLoader} for more details.
 *
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class MinimalOntologyLoader {

  private static final Logger logger = LoggerFactory.getLogger(MinimalOntologyLoader.class);

  private MinimalOntologyLoader() {
  }

  public static MinimalOntology loadOntology(File file) {
    return loadOntology(file, CurieUtilBuilder.defaultCurieUtil());
  }

  public static MinimalOntology loadOntology(File file, String... termIdPrefixes) {
    return loadOntology(file, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static MinimalOntology loadOntology(File file, CurieUtil curieUtil, String... termIdPrefixes) {
    GraphDocument graphDocument = OntologyLoadingRoutines.loadGraphDocument(file);
    return loadOntology(graphDocument, curieUtil, termIdPrefixes);
  }

  public static MinimalOntology loadOntology(InputStream inputStream) {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil());
  }

  public static MinimalOntology loadOntology(InputStream inputStream, String... termIdPrefixes) {
    return loadOntology(inputStream, CurieUtilBuilder.defaultCurieUtil(), termIdPrefixes);
  }

  public static MinimalOntology loadOntology(InputStream inputStream, CurieUtil curieUtil, String... termIdPrefixes) {
    return loadOntology(inputStream, curieUtil, OntologyLoaderOptions.defaultOptions(), termIdPrefixes);
  }

  public static MinimalOntology loadOntology(GraphDocument graphDocument,
                                             CurieUtil curieUtil,
                                             String... termIdPrefixes) {
    return loadOntology(graphDocument, curieUtil, OntologyLoaderOptions.defaultOptions(), termIdPrefixes);
  }

  public static MinimalOntology loadOntology(InputStream inputStream,
                                             CurieUtil curieUtil,
                                             OntologyLoaderOptions options,
                                             String... termIdPrefixes) {
    GraphDocument graphDocument = OntologyLoadingRoutines.loadGraphDocument(inputStream);
    return loadOntology(graphDocument, curieUtil, options, termIdPrefixes);
  }

  public static MinimalOntology loadOntology(GraphDocument graphDocument,
                                             CurieUtil curieUtil,
                                             OntologyLoaderOptions options,
                                             String... termIdPrefixes) {
    logger.debug("Finished loading ontology");
    logger.debug("Creating minimal ontology");
    OboGraphDocumentAdaptor graphDocumentAdaptor = OboGraphDocumentAdaptor.builder()
      .curieUtil(curieUtil)
      .wantedTermIdPrefixes(Set.of(termIdPrefixes))
      .discardNonPropagatingRelationships(options.discardNonPropagatingRelationships())
      .build(graphDocument);

    SimpleMinimalOntology ontology = SimpleMinimalOntology.builder()
      .hierarchyRelationshipType(RelationshipType.IS_A)
      .metaInfo(graphDocumentAdaptor.getMetaInfo())
      .terms(graphDocumentAdaptor.getTerms())
      .relationships(graphDocumentAdaptor.getRelationships())
      .build();
    logger.debug("Parsed a total of {} terms", ontology.getTerms().size());
    return ontology;
  }
}
