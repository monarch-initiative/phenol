package org.monarchinitiative.phenol.graph;

import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

public class OntologyGraphEdges {

  public static final TermId HP1 = TermId.of("HP:1");
  public static final TermId HP01 = TermId.of("HP:01");
  public static final TermId HP010 = TermId.of("HP:010");
  public static final TermId HP011 = TermId.of("HP:011");
  public static final TermId HP0110 = TermId.of("HP:0110");
  public static final TermId HP02 = TermId.of("HP:02");
  public static final TermId HP020 = TermId.of("HP:020");
  public static final TermId HP021 = TermId.of("HP:021");
  public static final TermId HP022 = TermId.of("HP:022");
  public static final TermId HP03 = TermId.of("HP:03");

  public static final List<TermId> VERTICES = List.of(HP1,
    HP01, HP010, HP011, HP0110,
    HP02, HP020, HP021, HP022,
    HP03);

  private OntologyGraphEdges(){}


  public static List<OntologyGraphEdge<TermId>> hierarchyEdges() {
    return List.of(
      OntologyGraphEdge.of(HP01, HP1, RelationshipType.IS_A),
      OntologyGraphEdge.of(HP010, HP01, RelationshipType.IS_A),
      OntologyGraphEdge.of(HP011, HP01, RelationshipType.IS_A),
      // Multi-parent
      OntologyGraphEdge.of(HP0110, HP010, RelationshipType.IS_A),
      OntologyGraphEdge.of(HP0110, HP011, RelationshipType.IS_A),

      OntologyGraphEdge.of(HP02, HP1, RelationshipType.IS_A),
      OntologyGraphEdge.of(HP020, HP02, RelationshipType.IS_A),
      OntologyGraphEdge.of(HP021, HP02, RelationshipType.IS_A),
      OntologyGraphEdge.of(HP022, HP02, RelationshipType.IS_A),

      OntologyGraphEdge.of(HP03, HP1, RelationshipType.IS_A)
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
