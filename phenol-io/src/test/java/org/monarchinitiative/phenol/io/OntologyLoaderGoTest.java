package org.monarchinitiative.phenol.io;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Testcases that verify whether obo-formatted Go ontology is properly parsed and loaded.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
class OntologyLoaderGoTest {

  private final Ontology ontology;

  OntologyLoaderGoTest() throws PhenolException {
      this.ontology = OntologyLoader.loadOntology(Paths.get("src/test/resources/go/go_head.obo").toFile(), "GO");
  }

  /** The obo file has the three top-level GO terms and one child each (i.e., three asserted is_a links).
   * The input of the GO file should create a new artificial root and should attach each of the top level terms,
   * and thus we expect 3+3=6 edges.
   */
  @Test
  void testEdgeSetSize() {
    DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertEquals(6, graph.edgeSet().size());
  }

  @Test
  void testArtificialRootTerm() {
    assertEquals(TermId.of("owl:Thing"), ontology.getRootTermId());
  }

  /**
   * For local testing with the full Gene Ontology file. This does not need to run in a normal build
   * @throws FileNotFoundException if the test GO file cannot be found
   * @throws PhenolException upon parse errors
   */
  @Disabled
  @Test void testReal() throws Exception {
    String localpath = "src/test/resources/go.obo";
    String RO_PREFIX = "RO";
    Ontology goOntology = OntologyLoader.loadOntology(Paths.get(localpath).toFile(), "GO");
    assertNotNull(goOntology);
    Map<TermId, Term> termMap = goOntology.getTermMap();
    for (TermId tid : termMap.keySet()) {
      if (tid.getPrefix().equals(RO_PREFIX)) {
        System.err.println("FOUND " + tid.getPrefix());
        fail();
      }
    }
    Map<Integer, Relationship> relMap = ontology.getRelationMap();
    for (Relationship r : relMap.values()) {
      if (r.getSource().getPrefix().equals(RO_PREFIX)){
        System.err.println("FOUND2 " + r);
        fail();
      }
    }
  }
}
