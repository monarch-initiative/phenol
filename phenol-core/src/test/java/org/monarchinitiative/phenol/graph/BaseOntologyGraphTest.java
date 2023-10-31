package org.monarchinitiative.phenol.graph;

import org.junit.jupiter.api.BeforeAll;
import org.monarchinitiative.phenol.graph.csr.poly.CsrPolyOntologyGraph;
import org.monarchinitiative.phenol.graph.csr.poly.StaticCsrArray;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Base for {@link org.monarchinitiative.phenol.graph.OntologyGraph} tests that provides small CSR graph for testing.
 */
public class BaseOntologyGraphTest {

  private static final byte C = 0b01; // child
  private static final byte P = 0b10; // parent

  protected static final TermId UNKNOWN = TermId.of("HP:999");
  protected static CsrPolyOntologyGraph<TermId, Byte> GRAPH;

  @BeforeAll
  public static void setUpBefore() {
    GRAPH = prepareSimpleGraph();
  }

  protected static CsrPolyOntologyGraph<TermId, Byte> prepareSimpleGraph() {
    TermId root = TermId.of("HP:1");

    TermId[] nodes = Stream.of(
        "HP:01", "HP:010", "HP:011", "HP:0110",
        "HP:02", "HP:020", "HP:021", "HP:022",
        "HP:03", "HP:1")
      .map(TermId::of)
      .toArray(TermId[]::new);

    int[] indptr = new int[] {0, 3, 5, 7, 9, 13, 14, 15, 16, 17, 20};
    int[] indices = new int[] {1, 2, 9, 0, 3, 0, 3, 1, 2, 5, 6, 7, 9, 4, 4, 4, 9, 0, 4, 8};
    Byte[] data = new Byte[] {C, C, P, P, C, P, C, P, P, C, C, C, P, P, P, P, P, C, C, C};
    StaticCsrArray<Byte> am = new StaticCsrArray<>(indptr, indices, data);
    Predicate<Byte> hierarchy = b -> (b & P) > 0;
    Predicate<Byte> hierarchyInverted = b -> (b & C) > 0;

    return new CsrPolyOntologyGraph<>(root, nodes, am, TermId::compareTo, hierarchy, hierarchyInverted);
  }
}
