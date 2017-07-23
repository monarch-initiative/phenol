package com.github.phenomics.ontolib.io.obo.go;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.phenomics.ontolib.formats.go.GoOntology;
import com.github.phenomics.ontolib.io.utils.ResourceUtils;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

public class GoOboParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

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

    assertEquals(
        "ImmutableDirectedGraph [edgeLists={ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=1], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=2], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=3]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=1]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=2]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=3]]]}, edgeCount=3]",
        ontology.getGraph().toString());
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000004], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005554], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0007582], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008372]]",
        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());
    assertThat(ImmutableSortedMap.copyOf(ontology.getTermMap()).toString(),
        startsWith("{ImmutableTermId"));
    assertThat(ImmutableSortedMap.copyOf(ontology.getTermMap()).toString(),
        endsWith("createdBy=null, creationDate=null]}"));
    assertEquals(
        "{1=GoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0003674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=1, relationQualifier=IS_A], 2=GoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0005575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=2, relationQualifier=IS_A], 3=GoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0008150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000], id=3, relationQualifier=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0000000]",
        ontology.getRootTermId().toString());
    assertEquals(
        "{data-version=releases/2017-06-16, remark=Includes Ontology(OntologyID(OntologyIRI(<http://purl.obolibrary.org/obo/go/never_in_taxon.owl>))) [Axioms: 18 Logical Axioms: 0]}",
        ontology.getMetaInfo().toString());
  }

}
