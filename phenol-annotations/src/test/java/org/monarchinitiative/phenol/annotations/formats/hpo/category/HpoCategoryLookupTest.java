package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HpoCategoryLookupTest {

  private static HpoCategoryLookup hpoCategoryLookup;

  @BeforeAll
  public static void init() throws IOException {
    URL hpOboURL = HpoCategoryLookupTest.class.getResource("/hpo_toy.json");
    hpoCategoryLookup = new HpoCategoryLookup(
      OntologyLoader.loadOntology(new File(hpOboURL.getFile())).graph(), HpoCategories.preset());
  }

  @Test
  void getPrioritizedCategory() {
    TermId autosomalDominant = TermId.of("HP:0000006");
    Term category = hpoCategoryLookup.getPrioritizedCategory(autosomalDominant).get();
    assertEquals(HpoCategories.INHERITANCE.id(), category.id());
  }

  @Test
  void getArachnoCategory() {
    TermId arachno = TermId.of("HP:0001166");
    Term category = hpoCategoryLookup.getPrioritizedCategory(arachno).get();
    assertEquals(HpoCategories.LIMBS.id(), category.id());
  }


  @Test
  void testInvalidModifierTerm(){
    TermId veryFrequent = TermId.of("HP:0040281");
    Optional<Term> category = hpoCategoryLookup.getPrioritizedCategory(veryFrequent);
	  assertTrue(category.isEmpty());
  }
}
