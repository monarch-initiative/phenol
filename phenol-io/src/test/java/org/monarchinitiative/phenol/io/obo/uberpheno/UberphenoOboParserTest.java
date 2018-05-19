package org.monarchinitiative.phenol.io.obo.uberpheno;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.monarchinitiative.phenol.formats.uberpheno.UberphenoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import org.monarchinitiative.phenol.ontology.data.TermId;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Testcases that verify whether Go ontology is properly parsed and loaded.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class UberphenoOboParserTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

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
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    assertEquals(
        "([ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001]], [(ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001] : ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001],ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]), (ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001] : ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001],ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]), (ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186] : ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186],ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001]), (ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001] : ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001])=(ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001],ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001])])",
        graph.toString());

    assertEquals(graph.edgeSet().size(), 4);

    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001]]",
        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());

    assertEquals(
        "{ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]=Term [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], altTermIds=[], name=All, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001]=Term [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], altTermIds=[], name=mammalian phenotype, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186]=Term [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], altTermIds=[], name=pigmentation phenotype, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]=Term [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], altTermIds=[], name=UBERPHENO_ROOT, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001]=Term [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001], altTermIds=[], name=abnormal(ly) quality zebrafish anatomical entity, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]]}",
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString());

    assertEquals(
        "{1=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=1, relationshipType=IS_A], 2=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=2, relationshipType=IS_A], 3=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=ZP], id=0000001], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001], id=3, relationshipType=IS_A], 4=Relationship [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0001186], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=MP], id=0000001], id=4, relationshipType=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());

    assertEquals(
        "ImmutableTermId [prefix=ImmutableTermPrefix [value=UBERPHENO], id=00000001]",
        ontology.getRootTermId().toString());

    assertEquals("{date=20:01:2012 06:00}", ontology.getMetaInfo().toString());
  }
}
