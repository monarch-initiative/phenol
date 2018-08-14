package org.monarchinitiative.phenol.io.obo.hpo;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class HpOboParserTest {

  private HpoOntology ontology;

  @Before
  public void setUp() throws IOException, PhenolException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    final HpOboParser parser = new HpOboParser(classLoader.getResourceAsStream("hp_head.obo"));
    ontology = parser.parse();
  }

  @Test
  public void testParseHpoHead()  {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertNotNull(graph);
  }

  @Test
  public void testGetRightNumberOfTerms() {
    int expectedTermCount=15; // there are 15 non-obsolete terms in hp_head.obo
    assertEquals(expectedTermCount,ontology.countAllTerms());
  }

  @Test
  public void testGetRootTerm() {
    TermId rootId = TermId.constructWithPrefix("HP:0000001");
     assertEquals(rootId,ontology.getRootTermId());
  }

  @Test
  public void testGetNonRootTerms() {
    // outside of the root these four terms are in the hp_head file
    TermId tid1 = TermId.constructWithPrefix("HP:0000005");
    TermId tid2 = TermId.constructWithPrefix("HP:0000006");
    TermId tid3 = TermId.constructWithPrefix("HP:0000007");
    TermId tid4 = TermId.constructWithPrefix("HP:0000118");
    Map<TermId,Term> termmap=ontology.getTermMap();
    assertTrue(termmap.containsKey(tid1));
    assertTrue(termmap.containsKey(tid2));
    assertTrue(termmap.containsKey(tid3));
    assertTrue(termmap.containsKey(tid4));

  }



}
