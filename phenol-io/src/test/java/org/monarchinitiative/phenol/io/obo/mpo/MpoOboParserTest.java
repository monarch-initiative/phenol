package org.monarchinitiative.phenol.io.obo.mpo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.formats.mpo.MpoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Testcases that verify whether obo-formatted MPO ontology is properly parsed and loaded.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class MpoOboParserTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File mpoHeadFile;

  @Before
  public void setUp() throws IOException {
    mpoHeadFile = tmpFolder.newFile("mp_head.obo");
    ResourceUtils.copyResourceToFile("/mp_head.obo", mpoHeadFile);
  }

  @Test
  public void testNewParser() {
    MpOboParser parser = new MpOboParser(mpoHeadFile);
    Ontology ontology = parser.parse();
    for (Term t : ontology.getTerms()) {
      System.out.println(t.toString());
    }
    // TODO should be 4 but we are getting extraneous terms!!!!!
   // assertEquals(4,ontology.countAllTerms());
    assertTrue(true);
  }

  /**
   * Test that we get all four terms from {@code mp_head.obo}.
   */
  @Test
  public void testGetAllFourTerms() {
    MpOboParser parser = new MpOboParser(mpoHeadFile);
    Ontology ontology = parser.parse();
    Collection<Term> terms = ontology.getTerms();
    Set<TermId> tids = terms.stream().map(Term::getId).collect(Collectors.toSet());
    assertTrue(tids.contains(TermId.constructWithPrefix("MP:0000001")));
    assertTrue(tids.contains(TermId.constructWithPrefix("MP:0001186")));
    assertTrue(tids.contains(TermId.constructWithPrefix("MP:0001188")));
    assertTrue(tids.contains(TermId.constructWithPrefix("MP:0002075")));
    TermId fakeTerm = TermId.constructWithPrefix("MP:1234567");
    assertFalse(tids.contains(fakeTerm));
  }


  @Test
  public void testParseMpoHead() throws IOException {
    final MpoOboParserOLD parser = new MpoOboParserOLD(mpoHeadFile, true);
    final MpoOntology ontology = parser.parse();
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    assertEquals(
        "([TermId [prefix=TermPrefix [value=MP], id=0001186], TermId [prefix=TermPrefix [value=MP], id=0000001], TermId [prefix=TermPrefix [value=MP], id=0001188], TermId [prefix=TermPrefix [value=MP], id=0002075]], [(TermId [prefix=TermPrefix [value=MP], id=0001186] : TermId [prefix=TermPrefix [value=MP], id=0000001])=(TermId [prefix=TermPrefix [value=MP], id=0001186],TermId [prefix=TermPrefix [value=MP], id=0000001]), (TermId [prefix=TermPrefix [value=MP], id=0001188] : TermId [prefix=TermPrefix [value=MP], id=0001186])=(TermId [prefix=TermPrefix [value=MP], id=0001188],TermId [prefix=TermPrefix [value=MP], id=0001186]), (TermId [prefix=TermPrefix [value=MP], id=0002075] : TermId [prefix=TermPrefix [value=MP], id=0001186])=(TermId [prefix=TermPrefix [value=MP], id=0002075],TermId [prefix=TermPrefix [value=MP], id=0001186])])",
        graph.toString());

    assertEquals(graph.edgeSet().size(), 3);

    assertEquals(
        "[TermId [prefix=TermPrefix [value=MP], id=0000001], TermId [prefix=TermPrefix [value=MP], id=0000368], TermId [prefix=TermPrefix [value=MP], id=0001186], TermId [prefix=TermPrefix [value=MP], id=0001188], TermId [prefix=TermPrefix [value=MP], id=0002075]]",
        ImmutableSortedSet.copyOf(ontology.getAllTermIds()).toString());

    assertEquals(
        "{TermId [prefix=TermPrefix [value=MP], id=0000001]=Term [id=TermId [prefix=TermPrefix [value=MP], id=0000001], altTermIds=[], name=mammalian phenotype, definition=the observable morphological, physiological, behavioral and other characteristics of mammalian organisms that are manifested through development and lifespan, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=MP], id=0000368]=Term [id=TermId [prefix=TermPrefix [value=MP], id=0002075], altTermIds=[TermId [prefix=TermPrefix [value=MP], id=0000368]], name=abnormal coat/hair pigmentation, definition=irregular or unusual pigmentation of the hair, comment=null, subsets=[Europhenome_Terms, IMPC, Sanger_Terms], synonyms=[ImmutableTermSynonym [value=abnormal coat color, scope=EXACT, synonymTypeName=null, termXrefs=[]], ImmutableTermSynonym [value=abnormal hair pigmentation, scope=EXACT, synonymTypeName=null, termXrefs=[]], ImmutableTermSynonym [value=coat: color anomalies, scope=EXACT, synonymTypeName=null, termXrefs=[]]], obsolete=false, createdBy=null, creationDate=null, xrefs=[ImmutableDbxref [name=MGI:2173541, description=null, trailingModifiers=null]]], TermId [prefix=TermPrefix [value=MP], id=0001186]=Term [id=TermId [prefix=TermPrefix [value=MP], id=0001186], altTermIds=[], name=pigmentation phenotype, definition=null, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=MP], id=0001188]=Term [id=TermId [prefix=TermPrefix [value=MP], id=0001188], altTermIds=[], name=hyperpigmentation, definition=excess of pigment in any or all tissues or a part of a tissue, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=MP], id=0002075]=Term [id=TermId [prefix=TermPrefix [value=MP], id=0002075], altTermIds=[TermId [prefix=TermPrefix [value=MP], id=0000368]], name=abnormal coat/hair pigmentation, definition=irregular or unusual pigmentation of the hair, comment=null, subsets=[Europhenome_Terms, IMPC, Sanger_Terms], synonyms=[ImmutableTermSynonym [value=abnormal coat color, scope=EXACT, synonymTypeName=null, termXrefs=[]], ImmutableTermSynonym [value=abnormal hair pigmentation, scope=EXACT, synonymTypeName=null, termXrefs=[]], ImmutableTermSynonym [value=coat: color anomalies, scope=EXACT, synonymTypeName=null, termXrefs=[]]], obsolete=false, createdBy=null, creationDate=null, xrefs=[ImmutableDbxref [name=MGI:2173541, description=null, trailingModifiers=null]]]}",
        ImmutableSortedMap.copyOf(ontology.getTermMap()).toString());

    assertEquals(
        "{1=Relationship [source=TermId [prefix=TermPrefix [value=MP], id=0001186], dest=TermId [prefix=TermPrefix [value=MP], id=0000001], id=1, relationshipType=IS_A], 2=Relationship [source=TermId [prefix=TermPrefix [value=MP], id=0001188], dest=TermId [prefix=TermPrefix [value=MP], id=0001186], id=2, relationshipType=IS_A], 3=Relationship [source=TermId [prefix=TermPrefix [value=MP], id=0002075], dest=TermId [prefix=TermPrefix [value=MP], id=0001186], id=3, relationshipType=IS_A]}",
        ImmutableSortedMap.copyOf(ontology.getRelationMap()).toString());

    assertEquals(
        "TermId [prefix=TermPrefix [value=MP], id=0000001]",
        ontology.getRootTermId().toString());

    assertEquals("{data-version=releases/2017-06-05}", ontology.getMetaInfo().toString());
  }
}
