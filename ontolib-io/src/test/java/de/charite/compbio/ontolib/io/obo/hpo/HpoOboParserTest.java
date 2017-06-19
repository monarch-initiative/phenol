package de.charite.compbio.ontolib.io.obo.hpo;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import de.charite.compbio.ontolib.formats.hpo.HpoOntology;
import de.charite.compbio.ontolib.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HpoOboParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File hpoHeadFile;

  @Before
  public void setUp() throws IOException {
    hpoHeadFile = tmpFolder.newFile("hp_head.obo");
    ResourceUtils.copyResourceToFile("/hp_head.obo", hpoHeadFile);
  }

  @Test
  public void testParseHpoHead() throws IOException {
    final HpoOboParser parser = new HpoOboParser(hpoHeadFile, true);
    final HpoOntology ontology = parser.parse();

    assertEquals(
        "ImmutableDirectedGraph [edgeLists={ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000001]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
            + "[source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000005], "
            + "dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], "
            + "id=1]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], "
            + "id=0000005]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
            + "[source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000006], "
            + "dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000005], "
            + "id=2], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000007], dest=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000005], id=3]], outEdges=[ImmutableEdge [source=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000005], dest=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000001], id=1]]], ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000006]=ImmutableVertexEdgeList "
            + "[inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000006], dest=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000005], id=2]]], ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000007]=ImmutableVertexEdgeList "
            + "[inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000007], dest=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000005], id=3]]]}, edgeCount=3]",
        ontology.getGraph().toString());
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], "
            + "ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000005], "
            + "ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000006], "
            + "ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000007]]",
        ImmutableSortedSet.copyOf(ontology.getTermIds()).toString());
    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]=HPOTerm "
            + "[id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], "
            + "altTermIds=[], name=All, definition=null, comment=Root of all terms in the "
            + "Human Phenotype Ontology., subsets=[], synonyms=[], obsolete=false, "
            + "createdBy=null, creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000005]=HPOTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000005], altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0001453], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], "
            + "id=0001461]], name=Mode of inheritance, definition=The pattern in which a "
            + "particular genetic trait or disorder is passed from one generation to the next., "
            + "comment=null, subsets=[], synonyms=[ImmutableTermSynonym [value=Inheritance, "
            + "scope=EXACT, synonymTypeName=null, termXRefs=[]]], obsolete=false, createdBy=null, "
            + "creationDate=null], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], "
            + "id=0000006]=HPOTerm [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], "
            + "id=0000006], altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], "
            + "id=0001415], ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0001447], "
            + "ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0001448], "
            + "ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0001451], ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0001455], ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0001456], ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0001463]], name=Autosomal "
            + "dominant inheritance, definition=A mode of inheritance that is observed "
            + "for traits related to a gene encoded on one of the autosomes (i.e., the human "
            + "chromosomes 1-22) in which a trait manifests in heterozygotes. In the "
            + "context of medical genetics, an autosomal dominant disorder is caused when "
            + "a single copy of the mutant allele is present. Males and females are affected "
            + "equally, and can both transmit the disorder with a risk of 50% for each child "
            + "of inheriting the mutant allele., comment=null, subsets=[], "
            + "synonyms=[ImmutableTermSynonym [value=Autosomal dominant, scope=EXACT, "
            + "synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=Autosomal "
            + "dominant form, scope=RELATED, synonymTypeName=null, termXRefs=[ImmutableTermXRef "
            + "[id=null, description=null]]], ImmutableTermSynonym [value=Autosomal dominant "
            + "type, scope=RELATED, synonymTypeName=null, termXRefs=[ImmutableTermXRef [id=null, "
            + "description=null]]]], obsolete=false, createdBy=null, creationDate=null], "
            + "ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000007]=HPOTerm "
            + "[id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000007], "
            + "altTermIds=[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0001416], "
            + "ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0001526]], "
            + "name=Autosomal recessive inheritance, definition=A mode of inheritance that is "
            + "observed for traits related to a gene encoded on one of the autosomes (i.e., "
            + "the human chromosomes 1-22) in which a trait manifests in homozygotes. In the "
            + "context of medical genetics, autosomal recessive disorders manifest in homozygotes "
            + "(with two copies of the mutant allele) or compound heterozygotes (whereby each "
            + "copy of a gene has a distinct mutant allele)., comment=null, subsets=[], "
            + "synonyms=[ImmutableTermSynonym [value=Autosomal recessive, scope=EXACT, "
            + "synonymTypeName=null, termXRefs=[]], ImmutableTermSynonym [value=Autosomal "
            + "recessive form, scope=RELATED, synonymTypeName=null, termXRefs=[ImmutableTermXRef "
            + "[id=null, description=null]]], ImmutableTermSynonym [value=Autosomal recessive "
            + "predisposition, scope=RELATED, synonymTypeName=null, termXRefs=[]]], "
            + "obsolete=false, createdBy=null, creationDate=null]}",
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString());
    assertEquals(
        "{1=HpoTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000005], dest=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=0000001], id=1, relationQualifier=IS_A], 2=HpoTermRelation "
            + "[source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000006], "
            + "dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000005], "
            + "id=2, relationQualifier=IS_A], 3=HpoTermRelation [source=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000007], dest=ImmutableTermId "
            + "[prefix=ImmutableTermPrefix [value=HP], id=0000005], id=3, "
            + "relationQualifier=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]",
        ontology.getRootTermId().toString());
  }

}
