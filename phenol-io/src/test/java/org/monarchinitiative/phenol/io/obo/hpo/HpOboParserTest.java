package org.monarchinitiative.phenol.io.obo.hpo;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.obo.DbXrefList;
import org.monarchinitiative.phenol.io.obo.OboDocConverter;
import org.monarchinitiative.phenol.ontology.data.*;
import org.obolibrary.oboformat.model.*;
import org.obolibrary.oboformat.parser.OBOFormatConstants;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.parser.OBOFormatParser;

public class HpOboParserTest {

  private HpoOntology ontology;

  @Before
  public void setUp() throws PhenolException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    final HpOboParser parser = new HpOboParser(classLoader.getResourceAsStream("hp_head.obo"));
    ontology = parser.parse();
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


  @Ignore
  @Test
  public void testParseFullHpo() throws Exception {
//    for (int i = 0; i < 10; i++) {
      HpOboParser parser = new HpOboParser(Paths.get("src/test/resources/hp.owl").toAbsolutePath().toFile());
      System.out.println("Starting full HPO load");
      Instant start = Instant.now();
      HpoOntology ontology = parser.parse();
      Instant end = Instant.now();
      System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//      ontology.getTerms().stream().limit(1240).forEach(System.out::println);
//    }
  }

  @Ignore
  @Test
  public void testParseFullHpoWithOboFormatParser() throws Exception {
//    for (int i = 0; i < 10; i++) {
    System.out.println("Starting full HPO load");
    Instant start = Instant.now();
    OBOFormatParser oboFormatParser = new OBOFormatParser();
    OBODoc oboDoc = oboFormatParser.parse(Paths.get("src/test/resources/hp.obo").toAbsolutePath().toFile());
    OboDocConverter oboDocConverter = OboDocConverter.convert(oboDoc);

//    System.out.println(oboDocConverter.getRelationships());

    Instant end = Instant.now();
    System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//    }
  }
}
