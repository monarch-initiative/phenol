package de.charite.compbio.ontolib.io.obo.go;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import de.charite.compbio.ontolib.formats.go.GoOntology;
import de.charite.compbio.ontolib.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
        "ImmutableDirectedGraph [edgeLists={ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=1], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=2], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=3]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=1]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=2]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=3]]]}, edgeCount=3]",
        ontology.getGraph().toString());
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150]]",
        ImmutableSortedSet.copyOf(ontology.getTermIds()).toString());
    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0]=GoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], altTermIds=[], name=<artificial root>, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674]=GoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674], altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5554]], name=molecular_function, definition=Elemental activities, such as catalysis or binding, describing the actions of a gene product at the molecular level. A given gene product may exhibit one or more molecular functions., comment=Note that, in addition to forming the root of the molecular function ontology, this term is recommended for use for the annotation of gene products whose molecular function is unknown. Note that when this term is used for annotation, it indicates that no information was available about the molecular function of the gene product annotated as of the date the annotation was made; the evidence code ND, no data, is used to indicate this. Despite its name, this is not a type of 'function' in the sense typically defined by upper ontologies such as Basic Formal Ontology (BFO). It is instead a BFO:process carried out by a single gene product or complex., subsets=[goslim_aspergillus, goslim_candida, goslim_chembl, goslim_generic, goslim_metagenomics, goslim_pir, goslim_plant, goslim_yeast, gosubset_prok], synonyms=[], obsolete=false, createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575]=GoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575], altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8372]], name=cellular_component, definition=The part of a cell, extracellular environment or virus in which a gene product is located. A gene product may be located in one or more parts of a cell and its location may be as specific as a particular macromolecular complex, that is, a stable, persistent association of macromolecules that function together., comment=Note that, in addition to forming the root of the cellular component ontology, this term is recommended for use for the annotation of gene products whose cellular component is unknown. Note that when this term is used for annotation, it indicates that no information was available about the cellular component of the gene product annotated as of the date the annotation was made; the evidence code ND, no data, is used to indicate this., subsets=[goslim_aspergillus, goslim_candida, goslim_chembl, goslim_generic, goslim_metagenomics, goslim_pir, goslim_plant, goslim_yeast, gosubset_prok], synonyms=[ImmutableTermSynonym [value=cell or subcellular entity, scope=EXACT, synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=cellular component, scope=EXACT, synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=subcellular entity, scope=RELATED, synonymTypeName=null, termXRefs=[ImmutableTermXRef [id=null, description=null]]]], obsolete=false, createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150]=GoTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150], altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=4], ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=7582]], name=biological_process, definition=Any process specifically pertinent to the functioning of integrated living units: cells, tissues, organs, and organisms. A process is a collection of molecular events with a defined beginning and end., comment=Note that, in addition to forming the root of the biological process ontology, this term is recommended for use for the annotation of gene products whose biological process is unknown. Note that when this term is used for annotation, it indicates that no information was available about the biological process of the gene product annotated as of the date the annotation was made; the evidence code ND, no data, is used to indicate this., subsets=[goslim_aspergillus, goslim_candida, goslim_chembl, goslim_generic, goslim_metagenomics, goslim_pir, goslim_plant, goslim_pombe, goslim_yeast, gosubset_prok], synonyms=[ImmutableTermSynonym [value=biological process, scope=EXACT, synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=physiological process, scope=EXACT, synonymTypeName=null, termXRefs=[]]], obsolete=false, createdBy=null, creationDate=null]}",
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString());
    assertEquals(
        "{1=GoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=3674], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=1, relationQualifier=IS_A], 2=GoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=5575], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=2, relationQualifier=IS_A], 3=GoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=8150], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0], id=3, relationQualifier=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0]",
        ontology.getRootTermId().toString());
  }

}
