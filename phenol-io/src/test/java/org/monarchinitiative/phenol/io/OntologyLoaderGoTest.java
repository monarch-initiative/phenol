package org.monarchinitiative.phenol.io;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Testcases that verify whether obo-formatted Go ontology is properly parsed and loaded.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OntologyLoaderGoTest {

  private Ontology ontology;

  @BeforeEach
    public void beforeEach() throws Exception {
    try (InputStream is = OntologyLoaderGoTest.class.getResourceAsStream("/go/go_head.json")) {
      ontology = OntologyLoader.loadOntology(is);
    }
  }


  /** The obo file has the three top-level GO terms and one child each (i.e., three asserted is_a links).
   * The input of the GO file should create a new artificial root and should attach each of the top level terms,
   * and thus we expect 3+3=6 edges.
   */
  @Test
  public void testEdgeSetSize() {
    DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertEquals(6, graph.edgeSet().size());
  }

  @Test
  public void testArtificialRootTerm() {
    assertEquals(TermId.of("owl:Thing"), ontology.getRootTermId());
  }

  /**
   * For local testing with the full Gene Ontology file. This does not need to run in a normal build
   * @throws FileNotFoundException if the test GO file cannot be found
   * @throws PhenolException upon parse errors
   */
  @Test
  public void testReal() throws Exception {
    String localpath = "src/test/resources/go/go_head.json";
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

  @Test
  public void testMetadata() {
    Map<String, String> metaInfo = ontology.getMetaInfo();
    assertThat(metaInfo, hasEntry("release", "2017-06-16"));
    assertThat(metaInfo, hasEntry("data-version", "http://purl.obolibrary.org/obo/go/releases/2017-06-16/go.owl"));
  }

}
