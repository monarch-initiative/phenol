package com.github.phenomics.ontolib.ontology.algo;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.github.phenomics.ontolib.ontology.data.ImmutableOntology;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermVisitor;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableOntologyTestBase;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableTerm;
import com.github.phenomics.ontolib.ontology.testdata.vegetables.VegetableTermRelation;

public class OntologyTermsTest extends VegetableOntologyTestBase {

  @Test
  public void testVisitChildrenOf() {
    Set<TermId> children = new TreeSet<>();

    OntologyTerms.visitChildrenOf(idRootVegetable, ontology,
        new TermVisitor<ImmutableOntology<VegetableTerm, VegetableTermRelation>>() {
          @Override
          public boolean visit(ImmutableOntology<VegetableTerm, VegetableTermRelation> ontology,
              TermId termId) {
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

    OntologyTerms.visitParentsOf(idBlueCarrot, ontology,
        new TermVisitor<ImmutableOntology<VegetableTerm, VegetableTermRelation>>() {
          @Override
          public boolean visit(ImmutableOntology<VegetableTerm, VegetableTermRelation> ontology,
              TermId termId) {
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
