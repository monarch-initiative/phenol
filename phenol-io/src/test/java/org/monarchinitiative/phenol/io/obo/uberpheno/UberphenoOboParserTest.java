//package org.monarchinitiative.phenol.io.obo.uberpheno;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Optional;
//
//import org.jgrapht.graph.DefaultDirectedGraph;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
//
//import org.monarchinitiative.phenol.formats.uberpheno.UberphenoOntology;
//import org.monarchinitiative.phenol.graph.IdLabeledEdge;
//import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
//import org.monarchinitiative.phenol.io.utils.ResourceUtils;
//import org.monarchinitiative.phenol.ontology.data.Ontology;
//import org.monarchinitiative.phenol.ontology.data.Relationship;
//import org.monarchinitiative.phenol.ontology.data.TermId;
//
//import com.google.common.collect.ImmutableSortedMap;
//import com.google.common.collect.ImmutableSortedSet;
//
///**
// * Testcases that verify whether Go ontology is properly parsed and loaded.
// *
// * @author Unknowns
// * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
// */
//public class UberphenoOboParserTest {
//
//  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();
//
//  private File uberphenoHeadFile;
//
//  private Optional<Ontology> optionalOntology;
//
//  @Before
//  public void setUp() throws IOException {
//    uberphenoHeadFile = tmpFolder.newFile("crossSpeciesPheno_head.obo");
//    ResourceUtils.copyResourceToFile("/crossSpeciesPheno_head.obo", uberphenoHeadFile);
//    OboOntologyLoader loader = new OboOntologyLoader(uberphenoHeadFile);
//    optionalOntology = loader.load();
//  }
//
//  @Test
//  public void testOntologyLoaded() {
//    assertTrue(optionalOntology.isPresent());
//  }
//
//  @Test
//  public void testGetAllFourEdges() {
//    Ontology ontology = optionalOntology.get();
//    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
//    assertEquals(graph.edgeSet().size(), 4);
//  }
//
//  @Test
//  public void testGetAllFiveTerms() {
//    Ontology ontology = optionalOntology.get();
//    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
//    assertEquals(5,ontology.countAllTerms());
//  }
//
//  @Test
//  public void testRelationMap() {
//    Ontology ontology = optionalOntology.get();
//    Map<Integer,Relationship> relmap = ontology.getRelationMap();
//    assertEquals(4,relmap.size());
//  }
//
//  @Test
//  public void testRootTerm() {
//    Ontology ontology = optionalOntology.get();
//    TermId root = TermId.constructWithPrefix("UBERPHENO:00000001");
//    //TODO -- currently broken!!!!!!!!!!!
//    //assertEquals(root,ontology.getRootTermId());
//  }
//
//}
