package org.monarchinitiative.phenol.ontology.algo;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermVisitor;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableOntologyTestBase;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableTerm;
import org.monarchinitiative.phenol.ontology.testdata.vegetables.VegetableRelationship;

public class OntologyTermsTest extends VegetableOntologyTestBase {

  @Test
  public void testVisitChildrenOf() {
    Set<TermId> children = new TreeSet<>();

    OntologyTerms.visitChildrenOf(
        idRootVegetable,
        ontology,
        new TermVisitor<ImmutableOntology<VegetableTerm, VegetableRelationship>>() {
          @Override
          public boolean visit(
              ImmutableOntology<VegetableTerm, VegetableRelationship> ontology, TermId termId) {
            children.add(termId);
            return true;
          }
        });

    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000002], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000004], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000005], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000006], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000007]]",
        children.toString());
  }

  @Test
  public void testChildrenOf() {
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000004], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000005], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000006], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000007], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000002]]",
        OntologyTerms.childrenOf(idRootVegetable, ontology).toString());
  }

  @Test
  public void testVisitParentsOf() {
    Set<TermId> parents = new TreeSet<>();

    OntologyTerms.visitParentsOf(
        idBlueCarrot,
        ontology,
        new TermVisitor<ImmutableOntology<VegetableTerm, VegetableRelationship>>() {
          @Override
          public boolean visit(
              ImmutableOntology<VegetableTerm, VegetableRelationship> ontology, TermId termId) {
            parents.add(termId);
            return true;
          }
        });

    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000002], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000004], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000007]]",
        parents.toString());
  }

  @Test
  public void testParentsOf() {
    assertEquals(
        "[ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000004], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000007], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000001], ImmutableTermId [prefix=ImmutableTermPrefix [value=VO], id=0000002]]",
        OntologyTerms.parentsOf(idBlueCarrot, ontology).toString());
  }
}
