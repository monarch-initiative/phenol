package org.monarchinitiative.phenol.io;

import com.google.common.collect.ImmutableMap;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.SimpleXref;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test cases that verify whether obo-formatted MPO ontology is properly parsed and loaded.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class OntologyLoaderMpoTest {
  //  TODO Rework for JSON files
//  private Ontology ontology;
//
//  public OntologyLoaderMpoTest() throws PhenolException {
//    this.ontology = OntologyLoader.loadOntology(Paths.get("src/test/resources/mgi/mp_head.obo").toFile());
//  }
//
//  @Test
//  public void testNumberOfTermsParsed() {
//    // mp_head.obo has four valid MP terms
//    assertEquals(4, ontology.countNonObsoleteTerms());
//  }
//
//  @Test
//  public void testFindPrimaryId() {
//    // MP:0000368 is an alt_id of MP:0002075
//    TermId altId = TermId.of("MP:0000368");
//    TermId primaryId = TermId.of("MP:0002075");
//    assertEquals(primaryId, ontology.getPrimaryTermId(altId));
//  }
//
//  @Test
//  public void testPmidDatabaseXref() {
//    //id: MP:0001188
//    //name: hyperpigmentation
//    //def: "excess of pigment in any or all tissues or a part of a tissue" [ISBN:0-683-40008-8, PMID:9778510]
//    TermId tid = TermId.of("MP:0001188");
//    Term term = ontology.getTermMap().get(tid);
//    List<SimpleXref> pmidXrefs = term.getPmidXrefs();
//    assertEquals(1, pmidXrefs.size());
//    SimpleXref sxref = pmidXrefs.get(0);
//    assertTrue(sxref.isPmid());
//    assertEquals("PMID:9778510", sxref.getCurie());
//
//    List<SimpleXref> allXrefs = term.getDatabaseXrefs();
//    assertEquals(2, allXrefs.size());
//    Optional<SimpleXref> isbn = allXrefs.stream().filter(SimpleXref::isIsbn).findAny();
//    assertTrue(isbn.isPresent());
//    sxref = isbn.get();
//    assertEquals("0-683-40008-8", sxref.getId());
//  }
//
//  @Test
//  public void testMgiXref() {
//    //id: MP:0002075
//    //name: abnormal coat/hair pigmentation
//    //alt_id: MP:0000368
//    //def: "irregular or unusual pigmentation of the hair" [MGI:csmith]
//    TermId tid = TermId.of("MP:0002075");
//    Term term = ontology.getTermMap().get(tid);
//    List<SimpleXref> allXrefs = term.getDatabaseXrefs();
//    assertEquals(1, allXrefs.size());
//    SimpleXref sxref = allXrefs.get(0);
//    assertTrue(sxref.isMgi());
//    assertEquals("MGI:csmith", sxref.getCurie());
//    assertEquals("csmith", sxref.getId());
//  }
//
//  /**
//   * Test that we get all four terms from {@code mp_head.obo}.
//   */
//  @Test
//  public void testGetAllFourTerms() {
//    Collection<Term> terms = ontology.getTerms();
//    Set<TermId> tids = terms.stream().map(Term::getId).collect(Collectors.toSet());
//    assertTrue(tids.contains(TermId.of("MP:0000001")));
//    assertTrue(tids.contains(TermId.of("MP:0001186")));
//    assertTrue(tids.contains(TermId.of("MP:0001188")));
//    assertTrue(tids.contains(TermId.of("MP:0002075")));
//    TermId fakeTerm = TermId.of("MP:1234567");
//    assertFalse(tids.contains(fakeTerm));
//  }
//
//  @Test
//  public void testParseMpoHead() {
//
//    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
//
//    assertEquals(3, graph.edgeSet().size());
//    assertEquals(TermId.of("MP:0000001"), ontology.getRootTermId());
//    assertEquals(ImmutableMap.of("data-version","http://purl.obolibrary.org/obo/mp/releases/2017-06-05/mp.owl"), ontology.getMetaInfo());
//  }
}
