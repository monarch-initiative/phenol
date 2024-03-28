package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Until deprecation")
public class HpoCategoryMapTest {

  private static Ontology ontology;
  private static final TermId INHERITANCE_ID = TermId.of("HP:0000005");
  private static final TermId LIMBS_ID = TermId.of("HP:0040064");
  private static final TermId CLINICAL_COURSE_ID = TermId.of("HP:0031797");

  @BeforeAll
  public static void init() throws IOException {
    URL hpOboURL = HpoCategoryMapTest.class.getResource("/hpo_toy.json");
    HpoCategoryMapTest.ontology = OntologyLoader.loadOntology(new File(hpOboURL.getFile()));
  }


  /**
   * If we add the term for autosomal dominant, there should be only one category
   * INHERITANCE_ID
   */
  @Test
  public void testSingleInheritanceTerm() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId autosomalDominant = TermId.of("HP:0000006");
    categoryMap.addAnnotatedTerm(autosomalDominant, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    HpoCategory hpoCategory = catlist.get(0);
    assertEquals(1, hpoCategory.getNumberOfAnnotations());
    assertEquals(INHERITANCE_ID, hpoCategory.id());
    // This HpoCategory object should now contain all of the annotating terms underneath
    // inheritance id
    assertEquals(1, catlist.size());
    // The category should include all terms added to the map that are under INHERITANCE_ID
    List<TermId> annotatingTermsList = hpoCategory.getAnnotatingTermIds();
    assertEquals(1, annotatingTermsList.size());
    assertTrue(hpoCategory.hasAnnotation());
    assertEquals(autosomalDominant, annotatingTermsList.get(0));
  }

  /**
   * Same as above except we add two inheritance terms
   */
  @Test
  public void testTwoInheritanceTerms() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId autosomalDominant = TermId.of("HP:0000006");
    TermId autosomalRecessive = TermId.of("HP:0000007");
    categoryMap.addAnnotatedTerm(autosomalDominant, ontology);
    categoryMap.addAnnotatedTerm(autosomalRecessive, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // Both terms should go to the same HpoCategory
    assertEquals(1, catlist.size());
    HpoCategory hpoCategory = catlist.get(0);
    // but now the HpoCategory has two annotated terms
    assertEquals(2, hpoCategory.getNumberOfAnnotations());
    // Still annotated to INHERITANCE
    assertEquals(INHERITANCE_ID, hpoCategory.id());
    assertTrue(hpoCategory.hasAnnotation());
    List<TermId> annotatingTermsList = hpoCategory.getAnnotatingTermIds();
    assertEquals(2, annotatingTermsList.size());
    assertTrue(annotatingTermsList.contains(autosomalDominant));
    assertTrue(annotatingTermsList.contains(autosomalRecessive));
  }

  /**
   * Arachnodactyly (HP:0001166) -- this is from LIMBS_ID
   */
  @Test
  public void testArachnodactyly(){
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId arachnodactyly = TermId.of("HP:0001166");
    categoryMap.addAnnotatedTerm(arachnodactyly, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // Both terms should go to the same HpoCategory
    assertEquals(1, catlist.size());
    HpoCategory hpoCategory = catlist.get(0);
    assertEquals(1, hpoCategory.getNumberOfAnnotations());
    // Still annotated to INHERITANCE
    assertEquals(LIMBS_ID, hpoCategory.id());
    assertEquals(1, catlist.size());
    // The category should include all terms added to the map that are under INHERITANCE_ID
    List<TermId> annotatingTermsList = hpoCategory.getAnnotatingTermIds();
    assertEquals(1, annotatingTermsList.size());
    assertTrue(hpoCategory.hasAnnotation());
    assertEquals(arachnodactyly, annotatingTermsList.get(0));
  }

  /**
   * Try to add a term from the Frequency ontology. The desired behavor is that nothing happens, i.e.,
   * the term 'Very frequent' (HP:0040281) is simply skipped.
   */
  @Test
  public void testAddingInvalidModifierTerm() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId veryFrequent = TermId.of("HP:0040281");
    categoryMap.addAnnotatedTerm(veryFrequent, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // This term is invalid and should not be mapped
    assertEquals(0, catlist.size());
  }

  /**
   * Add terms from INHERITANCE and from LIMBS_ID
   */
  @Test
  public void testTwoCategories() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId arachnodactyly = TermId.of("HP:0001166");
    TermId autosomalDominant = TermId.of("HP:0000006");
    TermId autosomalRecessive = TermId.of("HP:0000007");
    categoryMap.addAnnotatedTerm(autosomalDominant, ontology);
    categoryMap.addAnnotatedTerm(autosomalRecessive, ontology);
    categoryMap.addAnnotatedTerm(arachnodactyly, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // The terms should go to two different HpoCategory objects
    assertEquals(2, catlist.size());
    // Check that we have both LIMBS and INHERITANCE
    catlist.forEach(System.err::println);
    assertTrue(catlist.stream().anyMatch(cat -> cat.id().equals(LIMBS_ID)));
    assertTrue(catlist.stream().anyMatch(cat -> cat.id().equals(INHERITANCE_ID)));
  }

  /**
   * Add terms from INHERITANCE and from LIMBS_ID and CATEGORY match
   */
  @Test
  public void testCategoryExact(){
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId arachnodactyly = TermId.of("HP:0001166");
    TermId limbs = TermId.of("HP:0040064");
    categoryMap.addAnnotatedTerm(arachnodactyly, ontology);
    categoryMap.addAnnotatedTerm(limbs, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // The terms should go to two different HpoCategory objects
    assertEquals(1, catlist.size());
    // Check that we LIMBS
    catlist.forEach(System.err::println);
    assertTrue(catlist.stream().anyMatch(cat -> cat.id().equals(LIMBS_ID)));
  }

  /**
   * Add an onset terms. It should map to the category CLINICAL_COURSE
   */
  @Test
  public void testOnsetTerm() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId antenatalOnset = TermId.of("HP:0030674");  //Antenatal onset HP:
    TermId arachnodactyly = TermId.of("HP:0001166");
    categoryMap.addAnnotatedTerm(arachnodactyly, ontology);
    categoryMap.addAnnotatedTerm(antenatalOnset, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // The terms should go to two different HpoCategory objects
    assertEquals(2, catlist.size());
    // Check that we have both LIMBS and CLINICAL_COURSE
    assertTrue(catlist.stream().anyMatch(cat -> cat.id().equals(LIMBS_ID)));
    assertTrue(catlist.stream().anyMatch(cat -> cat.id().equals(CLINICAL_COURSE_ID)));
  }

}
