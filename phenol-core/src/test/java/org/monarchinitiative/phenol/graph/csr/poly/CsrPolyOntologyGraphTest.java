package org.monarchinitiative.phenol.graph.csr.poly;

import org.junit.jupiter.api.Disabled;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.OntologyGraphEdges;
import org.monarchinitiative.phenol.graph.OntologyGraphTest;
import org.monarchinitiative.phenol.ontology.data.TermId;


/**
 * Check that we can use {@link CsrPolyOntologyGraph} as an {@link OntologyGraph}.
 */
@Disabled("CsrPolyOntologyGraph is deprecated")
public class CsrPolyOntologyGraphTest extends OntologyGraphTest {

//  private static final byte C = 0b01; // child
//  private static final byte P = 0b10; // parent
//
//  TermId root = TermId.of("HP:1");
//
//  TermId[] nodes = Stream.of(
//      "HP:01", "HP:010", "HP:011", "HP:0110",
//      "HP:02", "HP:020", "HP:021", "HP:022",
//      "HP:03", "HP:1")
//    .map(TermId::of)
//    .toArray(TermId[]::new);
//
//  int[] indptr = new int[] {0, 3, 5, 7, 9, 13, 14, 15, 16, 17, 20};
//  int[] indices = new int[] {1, 2, 9, 0, 3, 0, 3, 1, 2, 5, 6, 7, 9, 4, 4, 4, 9, 0, 4, 8};
//  Byte[] data = new Byte[] {C, C, P, P, C, P, C, P, P, C, C, C, P, P, P, P, P, C, C, C};
//  StaticCsrArray<Byte> am = new StaticCsrArray<>(indptr, indices, data);
//  Predicate<Byte> hierarchy = b -> (b & P) > 0;
//  Predicate<Byte> hierarchyInverted = b -> (b & C) > 0;
//
//    return new CsrPolyOntologyGraph<>(root, nodes, am, TermId::compareTo, hierarchy, hierarchyInverted);

  @Override
  protected OntologyGraph<TermId> getGraph() {
    return CsrPolyOntologyGraphBuilder.builder(Byte.class)
      .build(OntologyGraphEdges.HP1, OntologyGraphEdges.hierarchyEdges());
  }

}
