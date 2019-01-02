package org.monarchinitiative.phenol.io.obo.go;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Testcases that verify whether obo-formatted Go ontology is properly parsed and loaded.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class GoOboParserTest {

  private final Ontology ontology;

  public GoOboParserTest() throws IOException, PhenolException {
      GoOboParser parser = new GoOboParser(Paths.get("src/test/resources/go/go_head.obo").toFile());
      this.ontology = parser.parse();
  }

  /** The obo file has the three top-level GO terms and one child each (i.e., three asserted is_a links).
   * The input of the GO file should create a new artificial root and should attach each of the top level terms,
   * and thus we expect 3+3=6 edges.
   */
  @Test
  public void testEdgeSetSize() {
    int expected=6;
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertEquals(expected, graph.edgeSet().size());
  }

  @Test
  public void testArtificialRootTerm() {
    TermId tid = ontology.getRootTermId();
    TermId expected = TermId.of("GO:0000000");
    assertEquals(expected,tid);
  }

  /**
   * For local testing with the full Gene Ontology file. This does not need to run in a normal build
   * @throws FileNotFoundException
   * @throws PhenolException
   */
  @Disabled
  @Test public void testReal() throws FileNotFoundException, PhenolException {
    String localpath="/home/robinp/data/go/go.obo";
    String RO_PREFIX = "RO";
    GoOboParser parser = new GoOboParser(localpath);
    GoOntology gontology=parser.parse();
    Map<TermId,Term> termmap =  gontology.getTermMap();
    assertNotNull(gontology);
    for (TermId tid : termmap.keySet()) {
     // String name = termmap.get(tid).getName();
      tid=termmap.get(tid).getId();
      if (tid.getPrefix().equals(RO_PREFIX)) {
        System.err.println("FOUND " + tid.getPrefix());
      }
//      System.out.println("Retrieving ancestors for " + name +"[" + tid.getIdWithPrefix() +"]");
//      Set<TermId> ancs = getAncestorTerms(gontology,tid,true);
//      System.out.println(String.format("%s: ancestors-n=%s",tid,ancs.size()));
    }
    Map<Integer, Relationship> relmap = ontology.getRelationMap();
    for (Relationship r : relmap.values()) {
      if (r.getSource().getPrefix().equals(RO_PREFIX)){
        System.err.println("FOUND2 " + r);
      }
    }
  }



}
