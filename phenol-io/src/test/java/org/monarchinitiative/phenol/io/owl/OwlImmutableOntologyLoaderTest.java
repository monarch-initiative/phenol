package org.monarchinitiative.phenol.io.owl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Test;

/**
 * A testcase that tests the codes of loading a dummy ontology built from ncit.owl.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class OwlImmutableOntologyLoaderTest {

  @Test
  public void testNCITLoad() throws Exception {
    Path ncitPath = Paths.get("src","test","resources","ncit_module.owl");
    final OwlImmutableOntologyLoader loader =
        new OwlImmutableOntologyLoader(ncitPath.toFile());
    final ImmutableOntology ontology = loader.load();
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    // 1. Checking vertices
    // In this dummy ontology, we have 6 classes.
    TermPrefix termPrefix = new TermPrefix("NCIT");
    TermId t1 = new TermId(termPrefix, "C2919");
    TermId t2 = new TermId(termPrefix, "C2852");
    TermId t3 = new TermId(termPrefix, "C48596");
    TermId t4 = new TermId(termPrefix, "C60312");
    TermId t5 = new TermId(termPrefix, "C116977");
    TermId t6 = new TermId(termPrefix, "C126659");

    assertTrue(graph.vertexSet().contains(t1));
    assertTrue(graph.vertexSet().contains(t2));
    assertTrue(graph.vertexSet().contains(t3));
    assertTrue(graph.vertexSet().contains(t4));
    assertTrue(graph.vertexSet().contains(t5));
    // This one is not recognized as a node due to the lack of rdfs:label.
    assertFalse(graph.vertexSet().contains(t6));

    // 2. Checking edges
    // Two subclasses are translated into two edges. Other axioms are not yet captured.
    assertEquals(graph.edgeSet().size(), 2);
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
    assertEquals(gr1.getRelationshipType(), RelationshipType.IS_A);
    assertEquals(gr2.getRelationshipType(), RelationshipType.IS_A);

    // 5. The example file contains multiple roots; thus we just put owl:Thing as the root.
    TermId rootTermId = ontology.getRootTermId();
    assertEquals(rootTermId.getPrefix().getValue(), "owl");
    assertEquals(rootTermId.getId(), "Thing");
  }

  @Test
  public void testMONDOLoad() throws Exception {
    Path mondoPath = Paths.get("src","test","resources","mondo_module.owl");
    final OwlImmutableOntologyLoader loader =
        new OwlImmutableOntologyLoader(mondoPath.toFile());
    final ImmutableOntology ontology = loader.load();
    final List<String> xrefs =
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
        Boolean containFlag = false;
        for (String xrefStr : xrefs) {
          if (xref.getName().contains(xrefStr)) containFlag = true;
        }
        if (!containFlag) fail("Xref " + xref.getName() + " is not available.");
      }
    }

    // 2. This sample ontology file contains a single root labeled as MONDO:0000624.
    TermId rootTermId = ontology.getRootTermId();
    assertEquals(rootTermId.getPrefix().getValue(), "MONDO");
    assertEquals(rootTermId.getId(), "0000624");
  }
}
