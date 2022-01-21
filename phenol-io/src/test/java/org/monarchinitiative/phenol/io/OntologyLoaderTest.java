package org.monarchinitiative.phenol.io;

import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Test;

/**
 * A testcase that tests the codes of loading a dummy ontology built from ncit.owl.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class OntologyLoaderTest {

  @Test
  public void loadJson() {
    Path ontologyPath = Paths.get("src/test/resources/hp_small.json");
    Ontology ontology = OntologyLoader.loadOntology(ontologyPath.toFile());
    // this file has 5 example HP terms
    assertEquals(5, ontology.countAllTerms());
  }

  @Test
  public void loadJsonStream() throws Exception {
    Path ontologyPath = Paths.get("src/test/resources/hp_small.json");
    Ontology ontology = OntologyLoader.loadOntology(Files.newInputStream(ontologyPath));
    assertThat(ontology.countAllTerms(), is(5));
  }

  @Test
  public void testNCITLoad() {
    Path ncitPath = Paths.get("src/test/resources/ncit_module.json");
    Ontology ontology = OntologyLoader.loadOntology(ncitPath.toFile(), "NCIT");
    DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    // 1. Checking vertices
    // In this dummy ontology, we have 6 classes.
    TermId t1 = TermId.of("NCIT:C2919");
    TermId t2 = TermId.of("NCIT:C2852");
    TermId t3 = TermId.of("NCIT:C48596");
    TermId t4 = TermId.of("NCIT:C60312");
    TermId t5 = TermId.of("NCIT:C116977");
    TermId t6 = TermId.of("NCIT:C126659");

    assertTrue(graph.vertexSet().contains(t1));
    assertTrue(graph.vertexSet().contains(t2));
    assertTrue(graph.vertexSet().contains(t3));
    assertTrue(graph.vertexSet().contains(t4));
    assertTrue(graph.vertexSet().contains(t5));
    // This one is not recognized as a node due to the lack of rdfs:label.
    assertFalse(graph.vertexSet().contains(t6));

    // 2. Checking edges
    // Two subclasses are translated into two edges. Other axioms are not yet captured.
    // Given there is no root term an artificial one is created and linked
    assertEquals(4, graph.edgeSet().size());
    assertNotNull(graph.getEdge(t1, t2));
    assertNotNull(graph.getEdge(t1, t3));

    // 3. Checking TermIds
    // This is essentially the same as checking vertices.
    assertTrue(ontology.getAllTermIds().contains(t1));
    assertTrue(ontology.getAllTermIds().contains(t2));
    assertTrue(ontology.getAllTermIds().contains(t3));
    assertTrue(ontology.getAllTermIds().contains(t4));
    assertTrue(ontology.getAllTermIds().contains(t5));
    assertFalse(ontology.getAllTermIds().contains(t6));

    // 4. Checking RelationMap
    // All meta-information on edges are available in RelationMap instance.
    Relationship gr1 = ontology.getRelationMap().get(graph.getEdge(t1, t2).getId());
    Relationship gr2 = ontology.getRelationMap().get(graph.getEdge(t1, t3).getId());
    assertNotNull(gr1);
    assertNotNull(gr2);
    assertEquals(RelationshipType.IS_A, gr1.getRelationshipType());
    assertEquals(RelationshipType.IS_A, gr2.getRelationshipType());

    // 5. The example file contains multiple roots; thus we just put owl:Thing as the root.
    assertEquals(TermId.of("owl:Thing"), ontology.getRootTermId());
  }

  @Test
  public void testMONDOLoad() {
    Path mondoPath = Paths.get("src", "test", "resources", "mondo_module.json");
    Ontology ontology = OntologyLoader.loadOntology(mondoPath.toFile(), "MONDO");
    List<String> xrefs =
      Arrays.asList(
        "DOID:0060111",
        "ICD10:D28.2",
        "MedDRA:10053865",
        "NCIT:C4517",
        "Orphanet:180237",
        "SCTID:92100009",
        "UMLS:C0346190");

    // 1. Check whether the example Term instance properly read all xref entries.
    for (Term gt : ontology.getTerms()) {
      for (Dbxref xref : gt.getXrefs()) {
        boolean containFlag = false;
        for (String xrefStr : xrefs) {
          if (xref.getName().contains(xrefStr)) {
            containFlag = true;
            break;
          }
        }
        if (!containFlag) fail("Xref " + xref.getName() + " is not available.");
      }
    }

    // 2. This sample ontology file contains a single root labeled as MONDO:0000624.
    assertEquals(TermId.of("MONDO:0000624"), ontology.getRootTermId());
  }

  @Test
  public void testLoadEctoSubset() {
    Path ectoPath = Paths.get("src/test/resources/ecto.json");

    // ECTO isn't mapped in the default Curie mappings, so we need to add it here (the PURL isn't correct)
    //CurieUtil curieUtil = CurieUtilBuilder.withDefaultsAnd(ImmutableMap.of("ECTO", "http://purl.obolibrary.org/obo/ECTO_"));
    //assertTrue(curieUtil.getCurieMap().containsKey("ECTO"));

    // ECTO also contains a bunch of unknown relationships so we're going to simplify this graph by only
    // loading ECTO nodes (this ignores the true root term XCO:0000000) and other nodes from CHEBI,
    // BFO and UBERON among others.
    //Ontology ecto = OntologyLoader.loadOntology(ectoPath.toFile(), curieUtil, "ECTO");
    Ontology ecto = OntologyLoader.loadOntology(ectoPath.toFile(), "ECTO");

    ecto.getRelationMap()
      .values()
      .forEach(relationship -> assertEquals(RelationshipType.IS_A, relationship.getRelationshipType()));
    Term rootT = ecto.getTermMap().get(ecto.getRootTermId());
    assertEquals("artificial root term", rootT.getName());
    assertEquals(TermId.of("owl:Thing"), ecto.getRootTermId());

    Set<String> termPrefixes = ecto.getAllTermIds().stream().map(TermId::getPrefix).collect(toSet());
    assertFalse(termPrefixes.contains("NCIT"));
    assertFalse(termPrefixes.contains("CHEBI"));
    assertFalse(termPrefixes.contains("GO"));
    // 2270 ECTO terms plus owl:thing
    assertEquals(2271, ecto.countNonObsoleteTerms());
    assertEquals(0, ecto.countAlternateTermIds());
  }

  /**
   * $ grep '\[Term\]' ecto.obo | wc -l
   * 8346
   * But four terms are obsolete and we add one term for owl:thing, so we arrive at 8343
   */
  @Test
  public void testLoadEctoAll() {
    Path ectoPath = Paths.get("src/test/resources/ecto.json");

    // ECTO isn't mapped in the default Curie mappings, so we need to add it here
    //CurieUtil curieUtil = CurieUtilBuilder.withDefaultsAnd(ImmutableMap.of("ECTO", "http://http://purl.obolibrary.org/obo/ECTO_"));
    //Ontology permissiveOntology = OntologyLoader.loadOntology(ectoPath.toFile(), curieUtil);
    Ontology permissiveOntology = OntologyLoader.loadOntology(ectoPath.toFile());

    assertEquals(TermId.of("owl:Thing"), permissiveOntology.getRootTermId());
    assertEquals(8343, permissiveOntology.countNonObsoleteTerms());
    assertEquals(4, permissiveOntology.countAlternateTermIds());

    Set<String> prefixes = permissiveOntology.getAllTermIds()
      .stream()
      .map(TermId::getPrefix)
      .collect(toSet());

    assertTrue(prefixes.contains("NCIT"));
    assertTrue(prefixes.contains("CHEBI"));
    assertTrue(prefixes.contains("GO"));

    // Only a subset of the terms are ECTO
    // $ grep 'id: ECTO' phenol/phenol-io/src/test/resources/ecto.obo | wc -l
    // 2270
   long ectoTermCount = permissiveOntology.getTermMap().values().
                        stream().
                        filter(term -> term.id().getPrefix().equals("ECTO")).
                        count();
   assertEquals(2270, ectoTermCount);
  }



}
