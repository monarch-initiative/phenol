package org.monarchinitiative.phenol.io.benches;

import org.geneontology.obographs.core.model.GraphDocument;
import org.monarchinitiative.phenol.io.obographs.OboGraphDocumentAdaptor;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.io.utils.OntologyLoadingRoutines;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.impl.SimpleMinimalOntology;

import java.nio.file.Path;
import java.util.Set;

public abstract class OntologyGraphSetup {
  protected static MinimalOntology loadOntology(Path hpoPath,
                                                SimpleMinimalOntology.Builder.GraphImplementation graphImplementation) {
    GraphDocument graphDocument = OntologyLoadingRoutines.loadGraphDocument(hpoPath.toFile());

    OboGraphDocumentAdaptor graphDocumentAdaptor = OboGraphDocumentAdaptor.builder()
      .curieUtil(CurieUtilBuilder.defaultCurieUtil())
      .wantedTermIdPrefixes(Set.of("HP"))
      .discardNonPropagatingRelationships(true)
      .discardDuplicatedRelationships(true)
      .build(graphDocument);

    return SimpleMinimalOntology.builder()
      .graphImplementation(graphImplementation)
      .metaInfo(graphDocumentAdaptor.getMetaInfo())
      .terms(graphDocumentAdaptor.getTerms())
      .relationships(graphDocumentAdaptor.getRelationships())
      .hierarchyRelationshipType(RelationshipType.IS_A)
      .build();
  }
}
