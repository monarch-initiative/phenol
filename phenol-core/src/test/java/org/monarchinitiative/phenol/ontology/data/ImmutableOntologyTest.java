package org.monarchinitiative.phenol.ontology.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Test;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

import java.util.Map;

/**
 * Testcases that verify the implementation of {@link ImmutableOntology}
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class ImmutableOntologyTest extends ImmutableOntologyTestBase {

  @Test
  public void test() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    assertEquals("{}", ontology.getMetaInfo().toString());

    assertEquals(
        "([TermId [prefix=TermPrefix [value=HP], id=0000001], TermId [prefix=TermPrefix [value=HP], id=0000002], TermId [prefix=TermPrefix [value=HP], id=0000003], TermId [prefix=TermPrefix [value=HP], id=0000004], TermId [prefix=TermPrefix [value=HP], id=0000005]], [(TermId [prefix=TermPrefix [value=HP], id=0000001] : TermId [prefix=TermPrefix [value=HP], id=0000002])=(TermId [prefix=TermPrefix [value=HP], id=0000001],TermId [prefix=TermPrefix [value=HP], id=0000002]), (TermId [prefix=TermPrefix [value=HP], id=0000001] : TermId [prefix=TermPrefix [value=HP], id=0000003])=(TermId [prefix=TermPrefix [value=HP], id=0000001],TermId [prefix=TermPrefix [value=HP], id=0000003]), (TermId [prefix=TermPrefix [value=HP], id=0000001] : TermId [prefix=TermPrefix [value=HP], id=0000004])=(TermId [prefix=TermPrefix [value=HP], id=0000001],TermId [prefix=TermPrefix [value=HP], id=0000004]), (TermId [prefix=TermPrefix [value=HP], id=0000002] : TermId [prefix=TermPrefix [value=HP], id=0000005])=(TermId [prefix=TermPrefix [value=HP], id=0000002],TermId [prefix=TermPrefix [value=HP], id=0000005]), (TermId [prefix=TermPrefix [value=HP], id=0000003] : TermId [prefix=TermPrefix [value=HP], id=0000005])=(TermId [prefix=TermPrefix [value=HP], id=0000003],TermId [prefix=TermPrefix [value=HP], id=0000005]), (TermId [prefix=TermPrefix [value=HP], id=0000004] : TermId [prefix=TermPrefix [value=HP], id=0000005])=(TermId [prefix=TermPrefix [value=HP], id=0000004],TermId [prefix=TermPrefix [value=HP], id=0000005])])",
        graph.toString());

    assertEquals(graph.edgeSet().size(), 6);

//    assertEquals(
//        "{TermId [prefix=TermPrefix [value=HP], id=0000001]=CommonTerm [id=TermId [prefix=TermPrefix [value=HP], id=0000001], altTermIds=[], name=term1, definition=some definition 1, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=HP], id=0000002]=CommonTerm [termId=TermId [prefix=TermPrefix [value=HP], id=0000002], altTermIds=[], name=term2, definition=some definition 2, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=HP], id=0000003]=Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000003], altTermIds=[], name=term3, definition=some definition 3, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=HP], id=0000004]=Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000004], altTermIds=[], name=term4, definition=some definition 4, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], TermId [prefix=TermPrefix [value=HP], id=0000005]=Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000005], altTermIds=[], name=term5, definition=some definition 5, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]]}",
//        ontology.getTermMap().toString());

//    assertEquals(
//        "{1=Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000001], dest=TermId [prefix=TermPrefix [value=HP], id=0000002], id=1], 2=Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000001], dest=TermId [prefix=TermPrefix [value=HP], id=0000003], id=2], 3=Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000001], dest=TermId [prefix=TermPrefix [value=HP], id=0000004], id=3], 4=Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000002], dest=TermId [prefix=TermPrefix [value=HP], id=0000005], id=4], 5=Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000003], dest=TermId [prefix=TermPrefix [value=HP], id=0000005], id=5], 6=Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000004], dest=TermId [prefix=TermPrefix [value=HP], id=0000005], id=6]}",
//        ontology.getRelationMap().toString());

    assertTrue(ontology.isRootTerm(id5));

    assertEquals(
        "[TermId [prefix=TermPrefix [value=HP], id=0000001], TermId [prefix=TermPrefix [value=HP], id=0000002], TermId [prefix=TermPrefix [value=HP], id=0000003], TermId [prefix=TermPrefix [value=HP], id=0000004], TermId [prefix=TermPrefix [value=HP], id=0000005]]",
        ontology.getAllTermIds().toString());

//    assertEquals(
//        "[CommonTerm [id=TermId [prefix=TermPrefix [value=HP], id=0000001], altTermIds=[], name=term1, definition=some definition 1, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000002], altTermIds=[], name=term2, definition=some definition 2, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000003], altTermIds=[], name=term3, definition=some definition 3, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000004], altTermIds=[], name=term4, definition=some definition 4, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], Term [termId=TermId [prefix=TermPrefix [value=HP], id=0000005], altTermIds=[], name=term5, definition=some definition 5, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]]]",
//        ontology.getTerms().toString());

    assertEquals(5, ontology.countAllTerms());

    assertEquals(
        "[TermId [prefix=TermPrefix [value=HP], id=0000001], TermId [prefix=TermPrefix [value=HP], id=0000002], TermId [prefix=TermPrefix [value=HP], id=0000003], TermId [prefix=TermPrefix [value=HP], id=0000004], TermId [prefix=TermPrefix [value=HP], id=0000005]]",
        ontology.getNonObsoleteTermIds().toString());

    assertEquals("[]", ontology.getObsoleteTermIds().toString());

    assertEquals(
        "[TermId [prefix=TermPrefix [value=HP], id=0000002], TermId [prefix=TermPrefix [value=HP], id=0000003], TermId [prefix=TermPrefix [value=HP], id=0000004]]",
        ontology.getParentTermIds(id1).toString());
  }

  /**
   * The subontology defined by TermI with id4 should consist of only the terms id4 and id1. The
   * termmap should thus contain only two terms. The subontology does not contain the original root
   * of the ontology, id5.
   */
  @Test
  public void testSubontologyCreation() {
    ImmutableOntology subontology =
        (ImmutableOntology) ontology.subOntology(id4);
    assertTrue(subontology.getTermMap().containsKey(id4));
    assertTrue(subontology.getTermMap().containsKey(id1));
    assertEquals(ontology.getTermMap().size() , 5);
    assertEquals(subontology.getTermMap().size() , 2);
    assertFalse(subontology.getTermMap().containsKey(id5));
  }

  /**
   * The parent ontology has six relations 1 Relationship [source=TermId
   * [prefix=TermPrefix [value=HP], id=0000001], dest=TermId
   * [prefix=TermPrefix [value=HP], id=0000002], id=1] 2 Relationship
   * [source=TermId [prefix=TermPrefix [value=HP], id=0000001],
   * dest=TermId [prefix=TermPrefix [value=HP], id=0000003], id=2] 3
   * Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000001],
   * dest=TermId [prefix=TermPrefix [value=HP], id=0000004], id=3] 4
   * Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000002],
   * dest=TermId [prefix=TermPrefix [value=HP], id=0000005], id=4] 5
   * Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000003],
   * dest=TermId [prefix=TermPrefix [value=HP], id=0000005], id=5] 6
   * Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000004],
   * dest=TermId [prefix=TermPrefix [value=HP], id=0000005], id=6] The subontology
   * has just the terms id1 and id4, and thus should just have only one relation./subontology 3
   * Relationship [source=TermId [prefix=TermPrefix [value=HP], id=0000001],
   * dest=TermId [prefix=TermPrefix [value=HP], id=0000004], id=3]
   */
  @Test
  public void testSubontologyRelations() {
    ImmutableOntology subontology =
        (ImmutableOntology) ontology.subOntology(id4);
    Map<Integer, Relationship> relationMap = ontology.getRelationMap();
    int expectedSize = 6;
    assertEquals(expectedSize, relationMap.size());
    relationMap = subontology.getRelationMap();
    expectedSize = 1;
    assertEquals(expectedSize, relationMap.size());
  }
}
