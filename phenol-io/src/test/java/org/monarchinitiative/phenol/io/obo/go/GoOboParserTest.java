package org.monarchinitiative.phenol.io.obo.go;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Testcases that verify whether obo-formatted Go ontology is properly parsed and loaded.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class GoOboParserTest {

  private GoOntology ontology;

  @Before
  public void setUp() throws IOException, PhenolException {
	ClassLoader classLoader = this.getClass().getClassLoader();
    GoOboParser parser = new GoOboParser(classLoader.getResourceAsStream("go_head.obo"));
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
    System.err.println(graph.toString());
    assertEquals(expected, graph.edgeSet().size());
  }

  @Test
  public void testArtificialRootTerm() {
    TermId tid = ontology.getRootTermId();
    Term root = ontology.getTermMap().get(tid);
    TermId expected = TermId.constructWithPrefix("GO:0000000");
    assertEquals(expected,tid);
  }



  @Test
  public void testParseHpoHead() throws IOException {
//    final GoOboParserOLD parser = new GoOboParserOLD(goHeadFile, true);
//    final GoOntology ontology = parser.parse();
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();



//
//    assertEquals(
//        "[TermId [prefix=TermPrefix [value=GO], id=0000000], TermId [prefix=TermPrefix [value=GO], id=0000004], TermId [prefix=TermPrefix [value=GO], id=0003674], TermId [prefix=TermPrefix [value=GO], id=0005554], TermId [prefix=TermPrefix [value=GO], id=0005575], TermId [prefix=TermPrefix [value=GO], id=0007582], TermId [prefix=TermPrefix [value=GO], id=0008150], TermId [prefix=TermPrefix [value=GO], id=0008372]]",
//        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());
//
//    assertThat(
//        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString(),
//        startsWith("{TermId"));
//
//    assertThat(
//        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString(),
//        endsWith("description=null, trailingModifiers=null]]]}"));
//
//    assertEquals(
//        "{1=Relationship [source=TermId [prefix=TermPrefix [value=GO], id=0003674], dest=TermId [prefix=TermPrefix [value=GO], id=0000000], id=1, relationshipType=IS_A], 2=Relationship [source=TermId [prefix=TermPrefix [value=GO], id=0005575], dest=TermId [prefix=TermPrefix [value=GO], id=0000000], id=2, relationshipType=IS_A], 3=Relationship [source=TermId [prefix=TermPrefix [value=GO], id=0008150], dest=TermId [prefix=TermPrefix [value=GO], id=0000000], id=3, relationshipType=IS_A]}",
//        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());
//
//
//
//    assertEquals(
//        "{data-version=releases/2017-06-16, remark=Includes Ontology(OntologyID(OntologyIRI(<http://purl.obolibrary.org/obo/go/never_in_taxon.owl>))) [Axioms: 18 Logical Axioms: 0]}",
//        ontology.getMetaInfo().toString());
  }
}
