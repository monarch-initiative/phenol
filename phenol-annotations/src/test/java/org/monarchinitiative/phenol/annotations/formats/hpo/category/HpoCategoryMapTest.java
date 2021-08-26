package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HpoCategoryMapTest {

  private static final Path hpOboPath = Paths.get("src","test","resources","hp_head.obo");
  private static final Ontology ontology = OntologyLoader.loadOntology(hpOboPath.toFile());
  private static final TermId INHERITANCE_ID = TermId.of("HP:0000005");
  private static final TermId EYE_ID = TermId.of("HP:0000478");
  private static final TermId CLINICAL_COURSE_ID = TermId.of("HP:0031797");
  /**
   * If we add the term for autosomal dominant, there should be only one category
   * INHERITANCE_ID
   */
  @Test
  void testSingleInheritanceTerm() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId autosomalDominant = TermId.of("HP:0000006");
    categoryMap.addAnnotatedTerm(autosomalDominant, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    HpoCategory hpoCategory = catlist.get(0);
    assertEquals(1, hpoCategory.getNumberOfAnnotations());
    assertEquals(INHERITANCE_ID, hpoCategory.getTid());
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
  void testTwoInheritanceTerms() {
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
    assertEquals(INHERITANCE_ID, hpoCategory.getTid());
    assertTrue(hpoCategory.hasAnnotation());
    List<TermId> annotatingTermsList = hpoCategory.getAnnotatingTermIds();
    assertEquals(2, annotatingTermsList.size());
    assertTrue(annotatingTermsList.contains(autosomalDominant));
    assertTrue(annotatingTermsList.contains(autosomalRecessive));
  }

  /**
   * Abnormality of globe size (HP:0100887) -- this is from EYE_ID
   */
  @Test
  void testAbnormalityGlobeSize(){
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId abnGlobeSize = TermId.of("HP:0100887");
    categoryMap.addAnnotatedTerm(abnGlobeSize, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // Both terms should go to the same HpoCategory
    assertEquals(1, catlist.size());
    HpoCategory hpoCategory = catlist.get(0);
    assertEquals(1, hpoCategory.getNumberOfAnnotations());
    // Still annotated to INHERITANCE
    assertEquals(EYE_ID, hpoCategory.getTid());
    assertEquals(1, catlist.size());
    // The category should include all terms added to the map that are under INHERITANCE_ID
    List<TermId> annotatingTermsList = hpoCategory.getAnnotatingTermIds();
    assertEquals(1, annotatingTermsList.size());
    assertTrue(hpoCategory.hasAnnotation());
    assertEquals(abnGlobeSize, annotatingTermsList.get(0));
  }

  /**
   * Try to add a term from the Frequency ontology. The desired behavor is that nothing happens, i.e.,
   * the term 'Very frequent' (HP:0040281) is simply skipped.
   */
  @Test
  void testAddingInvalidModifierTerm() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId veryFrequent = TermId.of("HP:0040281");
    categoryMap.addAnnotatedTerm(veryFrequent, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // This term is invalid and should not be mapped
    assertEquals(0, catlist.size());
  }

  /**
   * Add terms from INHERITANCE and from EYE
   */
  @Test
  void testTwoCategories() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId abnGlobeSize = TermId.of("HP:0100887");
    TermId autosomalDominant = TermId.of("HP:0000006");
    TermId autosomalRecessive = TermId.of("HP:0000007");
    categoryMap.addAnnotatedTerm(autosomalDominant, ontology);
    categoryMap.addAnnotatedTerm(autosomalRecessive, ontology);
    categoryMap.addAnnotatedTerm(abnGlobeSize, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // The terms should go to two different HpoCategory objects
    assertEquals(2, catlist.size());
    // Check that we have both EYE and INHERITANCE
    assertTrue(catlist.stream().anyMatch(cat -> cat.getTid().equals(EYE_ID)));
    assertTrue(catlist.stream().anyMatch(cat -> cat.getTid().equals(INHERITANCE_ID)));
  }

  /**
   * Add an onset terms. It should map to the category CLINICAL_COURSE
   */
  @Test
  void testOnsetTerm() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId antenatalOnset = TermId.of("HP:0030674");  //Antenatal onset HP:
    TermId abnGlobeSize = TermId.of("HP:0100887");
    categoryMap.addAnnotatedTerm(abnGlobeSize, ontology);
    categoryMap.addAnnotatedTerm(antenatalOnset, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    // The terms should go to two different HpoCategory objects
    assertEquals(2, catlist.size());
    // Check that we have both EYE and CLINICAL_COURSE
    assertTrue(catlist.stream().anyMatch(cat -> cat.getTid().equals(EYE_ID)));
    assertTrue(catlist.stream().anyMatch(cat -> cat.getTid().equals(CLINICAL_COURSE_ID)));
  }




  @Test
  public void testConnectiveTissue() {
    HpoCategoryMap categoryMap = new HpoCategoryMap();
    TermId abnConnectiveTissue = TermId.of("HP:0003549");  //Abnormality of connective tissue
    TermId increasedConnectiveTissue = TermId.of("HP:0009025"); //Increased connective tissue
    categoryMap.addAnnotatedTerm(increasedConnectiveTissue, ontology);
    List<HpoCategory> catlist = categoryMap.getActiveCategoryList();
    assertEquals(1, catlist.size());
    assertTrue(catlist.stream().anyMatch(cat -> cat.getTid().equals(abnConnectiveTissue)));
  }


}
