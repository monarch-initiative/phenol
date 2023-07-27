package org.monarchinitiative.phenol.graph;

import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

public class OntologyGraphEdges {

  private OntologyGraphEdges(){}


  public static List<OntologyGraphEdge<TermId>> hierarchyEdges() {
    return List.of(
      OntologyGraphEdge.of(TermId.of("HP:01"), TermId.of("HP:1"), RelationshipType.IS_A),
      OntologyGraphEdge.of(TermId.of("HP:010"), TermId.of("HP:01"), RelationshipType.IS_A),
      OntologyGraphEdge.of(TermId.of("HP:011"), TermId.of("HP:01"), RelationshipType.IS_A),
      // Multi-parent
      OntologyGraphEdge.of(TermId.of("HP:0110"), TermId.of("HP:010"), RelationshipType.IS_A),
      OntologyGraphEdge.of(TermId.of("HP:0110"), TermId.of("HP:011"), RelationshipType.IS_A),

      OntologyGraphEdge.of(TermId.of("HP:02"), TermId.of("HP:1"), RelationshipType.IS_A),
      OntologyGraphEdge.of(TermId.of("HP:020"), TermId.of("HP:02"), RelationshipType.IS_A),
      OntologyGraphEdge.of(TermId.of("HP:021"), TermId.of("HP:02"), RelationshipType.IS_A),
      OntologyGraphEdge.of(TermId.of("HP:022"), TermId.of("HP:02"), RelationshipType.IS_A),

      OntologyGraphEdge.of(TermId.of("HP:03"), TermId.of("HP:1"), RelationshipType.IS_A)
    );
  }

  public static List<OntologyGraphEdge<TermId>> makeHierarchyAndPartOfEdges() {
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>();

    edges.addAll(hierarchyEdges());
    edges.addAll(List.of(
      OntologyGraphEdge.of(TermId.of("PART:1"), TermId.of("HP:1"), RelationshipType.PART_OF),
      OntologyGraphEdge.of(TermId.of("PART:011"), TermId.of("HP:011"), RelationshipType.PART_OF),
      OntologyGraphEdge.of(TermId.of("PART:0110"), TermId.of("HP:0110"), RelationshipType.PART_OF)
    ));

    return edges;
  }

}
