package org.monarchinitiative.phenol.io.owl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.monarchinitiative.phenol.formats.generic.GenericTerm;
import org.monarchinitiative.phenol.formats.generic.GenericRelationship;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.owl.generic.GenericOwlFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Test;

import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * A testcase that tests the codes of loading a dummy ontology built from ncit.owl.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class OwlImmutableOntologyLoaderTest {
  @Test
  public void testLoader() throws Exception {
    // ncit_module.owl contains 6 classes where 2 classes are dummy ones.
    final OwlImmutableOntologyLoader<GenericTerm, GenericRelationship> loader =
        new OwlImmutableOntologyLoader<GenericTerm, GenericRelationship>(
            new File("src/test/resources/ncit_module.owl"));

    final GenericOwlFactory cof = new GenericOwlFactory();
    final ImmutableOntology<GenericTerm, GenericRelationship> ontology = loader.load(cof);
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    assertEquals(
        "([ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596]], [(ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919] : ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919],ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852]), (ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919] : ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919],ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596])])",
        graph.toString());

    assertEquals(graph.edgeSet().size(), 2);

    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C60312], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852]]",
        ontology.getAllTermIds().toString());

    assertEquals(
        "{1=GenericRelationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852], id=1, relationType=IS_A], 2=GenericRelationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596], id=2, relationType=IS_A]}",
        ontology.getRelationMap().toString());
  }
}
