package org.monarchinitiative.phenol.io.owl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.monarchinitiative.phenol.formats.generic.GenericTerm;
import org.monarchinitiative.phenol.formats.generic.GenericRelationship;
import org.monarchinitiative.phenol.formats.generic.GenericRelationshipType;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.owl.generic.GenericOwlFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.Dbxref;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermPrefix;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * A testcase that tests the codes of loading a dummy ontology built from ncit.owl.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class OwlImmutableOntologyLoaderTest {

  @Test
  public void testNCITLoad() throws Exception {
    final OwlImmutableOntologyLoader<GenericTerm, GenericRelationship> loader =
        new OwlImmutableOntologyLoader<GenericTerm, GenericRelationship>(
            new File("src/test/resources/ncit_module.owl"));

    final GenericOwlFactory cof = new GenericOwlFactory();
    final ImmutableOntology<GenericTerm, GenericRelationship> ontology = loader.load(cof);
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    // 1. Checking vertices
    // In this dummy ontology, we have 6 classes.
    ImmutableTermPrefix termPrefix = new ImmutableTermPrefix("NCIT");
    ImmutableTermId t1 = new ImmutableTermId(termPrefix, "C2919");
    ImmutableTermId t2 = new ImmutableTermId(termPrefix, "C2852");
    ImmutableTermId t3 = new ImmutableTermId(termPrefix, "C48596");
    ImmutableTermId t4 = new ImmutableTermId(termPrefix, "C60312");
    ImmutableTermId t5 = new ImmutableTermId(termPrefix, "C116977");
    ImmutableTermId t6 = new ImmutableTermId(termPrefix, "C126659");

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
    assertTrue(graph.getEdge(t1, t2) != null);
    assertTrue(graph.getEdge(t1, t3) != null);

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
    GenericRelationship gr1 = ontology.getRelationMap().get(graph.getEdge(t1, t2).getId());
    GenericRelationship gr2 = ontology.getRelationMap().get(graph.getEdge(t1, t3).getId());
    assertNotNull(gr1);
    assertNotNull(gr2);
    assertEquals(gr1.getRelationshipType(), GenericRelationshipType.IS_A);
    assertEquals(gr2.getRelationshipType(), GenericRelationshipType.IS_A);

    // 5. The example file contains multiple roots; thus we just put owl:Thing as the root.
    TermId rootTermId = ontology.getRootTermId();
    assertEquals(rootTermId.getPrefix().getValue(), "owl");
    assertEquals(rootTermId.getId(), "Thing");
  }

  @Test
  public void testMONDOLoad() throws Exception {
    final OwlImmutableOntologyLoader<GenericTerm, GenericRelationship> loader =
        new OwlImmutableOntologyLoader<GenericTerm, GenericRelationship>(
            new File("src/test/resources/mondo_module.owl"));

    final GenericOwlFactory cof = new GenericOwlFactory();
    final ImmutableOntology<GenericTerm, GenericRelationship> ontology = loader.load(cof);
    final List<String> xrefs =
        Arrays.asList(
            "DOID:0060111",
            "ICD10:D28.2",
            "MedDRA:10053865",
            "NCIT:C4517",
            "Orphanet:180237",
            "SCTID:92100009",
            "UMLS:C0346190");

    // 1. Check whether the example GenericTerm instance properly read all xref entries.
    for (GenericTerm gt : ontology.getTerms()) {
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