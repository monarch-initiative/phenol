package com.github.phenomics.ontolib.io.obo.uberpheno;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.phenomics.ontolib.formats.uberpheno.UberphenoOntology;
import com.github.phenomics.ontolib.io.obo.uberpheno.UberphenoOboParser;
import com.github.phenomics.ontolib.io.utils.ResourceUtils;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

public class UberphenoOboParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File uberphenoHeadFile;

  @Before
  public void setUp() throws IOException {
    uberphenoHeadFile = tmpFolder.newFile("crossSpeciesPheno_head.obo");
    ResourceUtils.copyResourceToFile("/crossSpeciesPheno_head.obo", uberphenoHeadFile);
  }

  @Test
  public void testParseUberphenoHead() throws IOException {
    final UberphenoOboParser parser = new UberphenoOboParser(uberphenoHeadFile, true);
    final UberphenoOntology ontology = parser.parse();

    assertEquals(
        "ImmutableDirectedGraph [edgeLists={ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=2]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], id=4]], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=1]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], id=4]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=2], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=1], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=3]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=3]]]}, edgeCount=4]",
        ontology.getGraph().toString());
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001]]",
        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());
    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]=UberphenoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], altTermIds=[], name=All, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001]=UberphenoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], altTermIds=[], name=mammalian phenotype, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186]=UberphenoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], altTermIds=[], name=pigmentation phenotype, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]=UberphenoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], altTermIds=[], name=UBERPHENO_ROOT, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001]=UberphenoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001], altTermIds=[], name=abnormal(ly) quality zebrafish anatomical entity, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]]}",
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString());
    assertEquals(
        "{1=UberphenoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=1, relationQualifier=IS_A], 2=UberphenoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=2, relationQualifier=IS_A], 3=UberphenoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=3, relationQualifier=IS_A], 4=UberphenoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], id=4, relationQualifier=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]",
        ontology.getRootTermId().toString());
    assertEquals("{date=20:01:2012 06:00}", ontology.getMetaInfo().toString());
  }

}
