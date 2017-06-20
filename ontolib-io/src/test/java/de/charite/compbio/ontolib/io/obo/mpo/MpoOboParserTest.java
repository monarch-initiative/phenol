package de.charite.compbio.ontolib.io.obo.mpo;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import de.charite.compbio.ontolib.formats.mpo.MpoOntology;
import de.charite.compbio.ontolib.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MpoOboParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File mpoHeadFile;

  @Before
  public void setUp() throws IOException {
    mpoHeadFile = tmpFolder.newFile("mp_head.obo");
    ResourceUtils.copyResourceToFile("/mp_head.obo", mpoHeadFile);
  }

  @Test
  public void testParseHpoHead() throws IOException {
    final MpoOboParser parser = new MpoOboParser(mpoHeadFile, true);
    final MpoOntology ontology = parser.parse();

    assertEquals(
        "ImmutableDirectedGraph [edgeLists={ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1], id=1]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], id=2], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], id=3]], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1], id=1]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], id=2]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], id=3]]]}, edgeCount=3]",
        ontology.getGraph().toString());
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075]]",
        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());
    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1]=MPOTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1], altTermIds=[], name=mammalian phenotype, definition=the observable morphological, physiological, behavioral and other characteristics of mammalian organisms that are manifested through development and lifespan, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186]=MPOTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], altTermIds=[], name=pigmentation phenotype, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188]=MPOTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188], altTermIds=[], name=hyperpigmentation, definition=excess of pigment in any or all tissues or a part of a tissue, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075]=MPOTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075], altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=368]], name=abnormal coat/hair pigmentation, definition=irregular or unusual pigmentation of the hair, comment=null, subsets=[Europhenome_Terms, IMPC, Sanger_Terms], synonyms=[ImmutableTermSynonym [value=abnormal coat color, scope=EXACT, synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=abnormal hair pigmentation, scope=EXACT, synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=coat: color anomalies, scope=EXACT, synonymTypeName=null, termXRefs=[]]], obsolete=false, createdBy=null, creationDate=null]}",
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString());
    assertEquals(
        "{1=MpoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1], id=1, relationQualifier=IS_A], 2=MpoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1188], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], id=2, relationQualifier=IS_A], 3=MpoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=2075], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1186], id=3, relationQualifier=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=1]",
        ontology.getRootTermId().toString());
  }

}
