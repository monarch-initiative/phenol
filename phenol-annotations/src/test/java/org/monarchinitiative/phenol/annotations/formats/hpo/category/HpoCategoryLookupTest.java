package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class HpoCategoryLookupTest {

  private static HpoCategoryLookup hpoCategoryLookup;
  private static final TermId INHERITANCE_ID = TermId.of("HP:0000005");
  private static final TermId LIMBS_ID = TermId.of("HP:0040064");
  private static final TermId CLINICAL_COURSE_ID = TermId.of("HP:0031797");

  @BeforeAll
  public static void init() throws IOException {
    URL hpOboURL = HpoCategoryLookupTest.class.getResource("/hpo_toy.json");
    hpoCategoryLookup = new HpoCategoryLookup(OntologyLoader.loadOntology(new File(hpOboURL.getFile())).graph());
  }

  @Test
  void getPrioritizedCategory() {
    TermId autosomalDominant = TermId.of("HP:0000006");
    Term category = hpoCategoryLookup.getPrioritizedCategory(autosomalDominant);
    assertEquals(category.id(), INHERITANCE_ID);
  }


  @Test
  void testCategories(){
    assertEquals(hpoCategoryLookup.categories().length, 27);
  }

  @Test
  void testInvalidModifierTerm(){
    TermId veryFrequent = TermId.of("HP:0040281");
    Term category = hpoCategoryLookup.getPrioritizedCategory(veryFrequent);
    assertNull(category);
  }
}
