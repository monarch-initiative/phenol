package org.monarchinitiative.phenol.io;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatParser;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OntologyLoaderHpoTest {

  private Ontology ontology;

  public OntologyLoaderHpoTest() throws PhenolException {
    this.ontology = OntologyLoader.loadOntology(Paths.get("src/test/resources/hp_head.obo").toFile());
  }

  @Test
  public void testParseHpoHead() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertNotNull(graph);
  }

  @Test
  public void testGetRightNumberOfTerms() {
    int expectedTermCount = 22; // there are 22 non-obsolete terms in hp_head.obo
    assertEquals(expectedTermCount, ontology.countAllTerms());
  }

  @Test
  public void testGetRootTerm() {
    TermId rootId = TermId.of("HP:0000001");
    assertEquals(rootId, ontology.getRootTermId());
  }

  @Test
  public void testGetNonRootTerms() {
    // outside of the root these four terms are in the hp_head file
    TermId tid1 = TermId.of("HP:0000005");
    TermId tid2 = TermId.of("HP:0000006");
    TermId tid3 = TermId.of("HP:0000007");
    TermId tid4 = TermId.of("HP:0000118");
    Map<TermId, Term> termMap = ontology.getTermMap();
    assertTrue(termMap.containsKey(tid1));
    assertTrue(termMap.containsKey(tid2));
    assertTrue(termMap.containsKey(tid3));
    assertTrue(termMap.containsKey(tid4));
    Term term1 = termMap.get(tid1);
    System.out.println(term1);
  }


  @Disabled
  @Test
  public void testParseFullHpo() throws Exception {
//    for (int i = 0; i < 10; i++) {
      System.out.println("Starting full HPO load");
      Instant start = Instant.now();
      Ontology ontology = OntologyLoader.loadOntology(Paths.get("src/test/resources/hp.obo").toFile());
      Instant end = Instant.now();
      System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//      ontology.getTerms().stream().limit(1240).forEach(System.out::println);
//    }
  }

  @Disabled
  @Test
  public void testParseFullHpoWithOboFormatParser() throws Exception {
//    for (int i = 0; i < 10; i++) {
    System.out.println("Starting full HPO load");
    Instant start = Instant.now();
    OBOFormatParser oboFormatParser = new OBOFormatParser();
    OBODoc oboDoc = oboFormatParser.parse(Paths.get("src/test/resources/hp.obo").toFile());
//    OboDocConverter oboDocConverter = OboDocConverter.convert(oboDoc);

//    System.out.println(oboDocConverter.getRelationships());

    Instant end = Instant.now();
    System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//    }
  }
}
