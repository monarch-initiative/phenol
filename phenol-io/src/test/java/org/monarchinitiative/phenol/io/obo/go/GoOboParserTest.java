package org.monarchinitiative.phenol.io.obo.go;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import org.monarchinitiative.phenol.ontology.data.*;


/**
 * Testcases that verify whether obo-formatted Go ontology is properly parsed and loaded.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class GoOboParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private Ontology ontology;

  @Before
  public void setUp() throws IOException, PhenolException {

    File goHeadFile;
    goHeadFile = tmpFolder.newFile("go_head.obo");
    ResourceUtils.copyResourceToFile("/go/go_head.obo", goHeadFile);
    GoOboParser parser = new GoOboParser(goHeadFile);

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
    TermId expected = TermId.constructWithPrefix("GO:0000000");
    assertEquals(expected,tid);
  }

  /**
   * For local testing with the full Gene Ontology file. This does not need to run in a normal build
   * @throws FileNotFoundException
   * @throws PhenolException
   */
  @Test @Ignore public void testReal() throws FileNotFoundException, PhenolException {
    String localpath="/home/robinp/data/go/go.obo";
    TermPrefix RO_PREFIX=new TermPrefix("RO");
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
