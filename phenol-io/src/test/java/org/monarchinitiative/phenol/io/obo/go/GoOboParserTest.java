package org.monarchinitiative.phenol.io.obo.go;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import org.monarchinitiative.phenol.ontology.data.TermId;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Testcases that verify whether obo-formatted Go ontology is properly parsed and loaded.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GoOboParserTest {
  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();
  private File goHeadFile;

  @Before
  public void setUp() throws IOException {
    goHeadFile = tmpFolder.newFile("go_head.obo");
    ResourceUtils.copyResourceToFile("/go_head.obo", goHeadFile);
  }

  @Test
  public void testParseHpoHead() throws IOException {
    final GoOboParser parser = new GoOboParser(goHeadFile, true);
    final GoOntology ontology = parser.parse();
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    assertEquals(
        "([ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150]], [(ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674] : ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674],ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000]), (ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575] : ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575],ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000]), (ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150] : ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150],ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000])])",
        graph.toString());

    assertEquals(graph.edgeSet().size(), 3);

    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000004], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005554], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0007582], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008372]]",
        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());

    assertThat(
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString(),
        startsWith("{ImmutableTermId"));

    assertThat(
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString(),
        endsWith("description=null, trailingModifiers=null]]]}"));

    assertEquals(
        "{1=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=1, relationshipType=IS_A], 2=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=2, relationshipType=IS_A], 3=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=3, relationshipType=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());

    assertEquals(
        "ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000]",
        ontology.getRootTermId().toString());

    assertEquals(
        "{data-version=releases/2017-06-16, remark=Includes Ontology(OntologyID(OntologyIRI(<http://purl.obolibrary.org/obo/go/never_in_taxon.owl>))) [Axioms: 18 Logical Axioms: 0]}",
        ontology.getMetaInfo().toString());
  }
}
