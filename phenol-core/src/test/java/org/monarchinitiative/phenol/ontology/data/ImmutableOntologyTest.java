package org.monarchinitiative.phenol.ontology.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.TestOntology;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testcases that verify the implementation of {@link ImmutableOntology}
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
class ImmutableOntologyTest {

  private final Ontology ontology = TestOntology.ontology();

//  @Disabled("Currently failing on a string comparison")
  @Test
  void test() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();

    assertEquals(ImmutableMap.of(), ontology.getMetaInfo());

    // TODO: this fails the vertex order
    //  Expected: [HP:0000001, HP:0000002, HP:0000003, HP:0000004, HP:0000005]
    //  Actual:   [HP:0000002, HP:0000001, HP:0000004, HP:0000003, HP:0000005]
//    assertEquals(
//      "([HP:0000001, HP:0000002, HP:0000003, HP:0000004, HP:0000005], [(HP:0000001 : HP:0000002)=(HP:0000001,HP:0000002), (HP:0000001 : HP:0000003)=(HP:0000001,HP:0000003), (HP:0000001 : HP:0000004)=(HP:0000001,HP:0000004), (HP:0000002 : HP:0000005)=(HP:0000002,HP:0000005), (HP:0000003 : HP:0000005)=(HP:0000003,HP:0000005), (HP:0000004 : HP:0000005)=(HP:0000004,HP:0000005)])",
//      graph.toString());

    assertEquals(
      ImmutableSet.of(
        TermId.of("HP:0000001"),
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004"),
        TermId.of("HP:0000005")
      ),
      graph.vertexSet());

    // surely should be able to create a set of edges?
    assertEquals(6, graph.edgeSet().size());

//    assertEquals(
//        "{HP:0000001=CommonTerm [id=HP:0000001, altTermIds=[], name=term1, definition=some definition 1, comment=null, subsets=[], synonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], HP:0000002=CommonTerm [termId=HP:0000002, altTermIds=[], name=term2, definition=some definition 2, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], HP:0000003=Term [termId=HP:0000003, altTermIds=[], name=term3, definition=some definition 3, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], HP:0000004=Term [termId=HP:0000004, altTermIds=[], name=term4, definition=some definition 4, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]], HP:0000005=Term [termId=HP:0000005, altTermIds=[], name=term5, definition=some definition 5, comment=null, subsets=[], termSynonyms=[], obsolete=false, createdBy=null, creationDate=null, xrefs=[]]}",
//        ontology.getTermMap().toString());

//    assertEquals(
//        "{1=Relationship [source=HP:0000001, dest=HP:0000002, id=1], 2=Relationship [source=HP:0000001, dest=HP:0000003, id=2], 3=Relationship [source=HP:0000001, dest=HP:0000004, id=3], 4=Relationship [source=HP:0000002, dest=HP:0000005, id=4], 5=Relationship [source=HP:0000003, dest=HP:0000005, id=5], 6=Relationship [source=HP:0000004, dest=HP:0000005, id=6]}",
//        ontology.getRelationMap().toString());

    assertTrue(ontology.isRootTerm(TestOntology.rootId()));

    assertEquals(
      ImmutableSet.of(
        TermId.of("HP:0000001"),
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004"),
        TermId.of("HP:0000005")
      ),
      ontology.getAllTermIds());

    assertEquals(5, ontology.countAllTerms());

    assertEquals(
      ImmutableSet.of(
        TermId.of("HP:0000001"),
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004"),
        TermId.of("HP:0000005")
      ),
      ontology.getNonObsoleteTermIds());

    assertTrue(ontology.getObsoleteTermIds().isEmpty());

    assertEquals(
      ImmutableSet.of(
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004")
      ),
      ontology.getParentTermIds(TestOntology.TERM_ID_1));
  }

  /**
   * The subontology defined by TermI with id4 should consist of only the terms id4 and id1. The
   * termmap should thus contain only two terms. The subontology does not contain the original root
   * of the ontology, id5.
   */
  @Test
  public void testSubontologyCreation() {
    Ontology subontology = ontology.subOntology(TestOntology.TERM_ID_4);
    assertTrue(subontology.getTermMap().containsKey(TestOntology.TERM_ID_4));
    assertTrue(subontology.getTermMap().containsKey(TestOntology.TERM_ID_1));
    assertEquals(5, ontology.getTermMap().size());
    assertEquals(2, subontology.getTermMap().size());
    assertFalse(subontology.getTermMap().containsKey(TestOntology.rootId()));
  }

  /**
   * The parent ontology has six relations
   * 1 Relationship [source=HP:0000001, dest=HP:0000002, id=1]
   * 2 Relationship [source=HP:0000001, dest=HP:0000003, id=2]
   * 3 Relationship [source=HP:0000001, dest=HP:0000004, id=3]
   * 4 Relationship [source=HP:0000002, dest=HP:0000005, id=4]
   * 5 Relationship [source=HP:0000003, dest=HP:0000005, id=5]
   * 6 Relationship [source=HP:0000004, dest=HP:0000005, id=6]
   * The subontology has just the terms id1 and id4, and thus should just have only one relation./subontology 3
   * Relationship [source=HP:0000001, dest=HP:0000004, id=3]
   */
  @Test
  void testSubontologyRelations() {
    Ontology subontology = ontology.subOntology(TestOntology.TERM_ID_4);
    assertEquals(6, ontology.getRelationMap().size());
    assertEquals(1, subontology.getRelationMap().size());
  }

  @Test
  void testBuilder() {
    TermId rootId = TermId.of("owl:Thing");
    Term root = Term.of(rootId, "root");

    TermId childId = TermId.of("HP:0000001");
    Term child = Term.of(childId, "child");

    TermId child2Id = TermId.of("HP:0000002");
    Term child2 = Term.of(child2Id, "child2");

    TermId child3Id = TermId.of("HP:0000003");
    TermId child3SecondaryId = TermId.of("HP:3333333");
    Term child3 = Term.builder()
      .id(child3Id)
      .name("child3")
      .altTermIds(ImmutableList.of(child3SecondaryId))
      .build();

    TermId obsoleteId = TermId.of("HP:0000004");
    Term obsolete = Term.builder().id(obsoleteId).name("obsolete").obsolete(true).build();

    ImmutableList<Term> terms = ImmutableList.of(root, child, child2, child3, obsolete);


    Relationship relationship1 = new Relationship(childId, rootId, 1, RelationshipType.IS_A);
    Relationship relationship2 = new Relationship(child2Id, rootId, 2, RelationshipType.IS_A);
    Relationship relationship3 = new Relationship(child3Id, child2Id, 3, RelationshipType.IS_A);
    Relationship relationship4 = new Relationship(childId, obsoleteId, 4, RelationshipType.IS_A);

    ImmutableList<Relationship> relationships = ImmutableList.of(relationship1, relationship2, relationship3, relationship4);


    Ontology instance = ImmutableOntology.builder()
      .terms(terms)
      .relationships(relationships)
      .build();

    assertEquals(rootId, instance.getRootTermId());
    assertTrue(instance.isRootTerm(rootId));

    Set<TermId> allTermIds = terms.stream().map(Term::getId).collect(Collectors.toSet());
    assertEquals(allTermIds, instance.getAllTermIds());

    Set<TermId> nonObsoleteTermIds = terms.stream()
      .filter(term -> !term.isObsolete())
      .map(Term::getId)
      .collect(Collectors.toSet());
    assertEquals(nonObsoleteTermIds, instance.getNonObsoleteTermIds());


    assertEquals(child3Id, instance.getPrimaryTermId(child3Id));
    assertEquals(child3Id, instance.getPrimaryTermId(child3SecondaryId));

    assertEquals(ImmutableSet.of(rootId), instance.getParentTermIds(child2Id));
    assertEquals(ImmutableSet.of(child2Id), instance.getParentTermIds(child3Id));
    assertEquals(ImmutableSet.of(child3Id, child2Id, rootId), instance.getAncestorTermIds(child3Id));
    assertEquals(ImmutableSet.of(child3Id, child2Id), instance.getAncestorTermIds(child3Id, false));
  }

  @Test
  void testBuilderMultipleRoots() {

    TermId childId = TermId.of("HP:0000001");
    Term child = Term.of(childId, "child");

    TermId child2Id = TermId.of("HP:0000002");
    Term child2 = Term.of(child2Id, "child2");

    TermId child3Id = TermId.of("HP:0000003");
    Term child3 = Term.of(child3Id, "child3");

    TermId obsoleteId = TermId.of("HP:0000004");
    Term obsolete = Term.builder().id(obsoleteId).name("obsolete").obsolete(true).build();

    ImmutableList<Term> terms = ImmutableList.of(child, child2, child3, obsolete);

    Relationship relationship3 = new Relationship(child3Id, child2Id, 3, RelationshipType.IS_A);
    Relationship relationship4 = new Relationship(childId, obsoleteId, 4, RelationshipType.IS_A);

    ImmutableList<Relationship> relationships = ImmutableList.of(relationship3, relationship4);


    Ontology instance = ImmutableOntology.builder()
      .terms(terms)
      .relationships(relationships)
      .build();

    TermId rootId = TermId.of("owl:Thing");
    assertEquals(rootId, instance.getRootTermId());
    assertTrue(instance.isRootTerm(rootId));

    assertEquals(ImmutableSet.of(child2Id), instance.getParentTermIds(child3Id));

    assertEquals(ImmutableSet.of(child3Id, child2Id, rootId), instance.getAncestorTermIds(child3Id));
    assertEquals(ImmutableSet.of(child3Id, child2Id), instance.getAncestorTermIds(child3Id, false));
  }

  @Test
  void testBuilderSingleRootCandidate() {

    TermId rootId = TermId.of("HP:0000000");
    Term root = Term.of(rootId, "root");

    TermId childId = TermId.of("HP:0000001");
    Term child = Term.of(childId, "child");

    Relationship relationship = new Relationship(childId, rootId, 1, RelationshipType.IS_A);

    Ontology instance = ImmutableOntology.builder()
      .terms(ImmutableList.of(root, child))
      .relationships(ImmutableList.of(relationship))
      .build();

    assertEquals(rootId, instance.getRootTermId());
    assertTrue(instance.isRootTerm(rootId));
  }


  @Test
  void testBuilderNoRoot() {

    TermId childId = TermId.of("HP:0000001");
    Term child = Term.of(childId, "child");

    TermId child2Id = TermId.of("HP:0000002");
    Term child2 = Term.of(child2Id, "child2");

    assertThrows(PhenolRuntimeException.class, () -> ImmutableOntology.builder()
      .terms(ImmutableList.of(child, child2))
      .relationships(ImmutableList.of())
      .build(), "No root candidate found.");
  }
}
