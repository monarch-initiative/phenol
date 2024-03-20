package org.monarchinitiative.phenol.annotations.formats.hpo.category;


import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Model of the upper-level HPO classes. This allows a canonical view of HPO Annotation per disease
 * according to categories such as Heart, Eye, Brain etc.
 * <p>
 * <p>
 * The main function takes a list of HPO terms and returns a compatible list but sorted according to the
 * categories. Client code should use {@link #addAnnotatedTerms(List, org.monarchinitiative.phenol.ontology.data.MinimalOntology)}  to initialize the
 * Category map. The function {@link #getActiveCategoryList()} returns an immutable list of categories
 * that include at least one HPO term used for annotation.
 * </p>
 *
 * @deprecated targeted for removal in <em>3.0.0</em>, use {@link HpoCategoryLookup} instead.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.7
 */
@Deprecated(since = "2.0.6", forRemoval = true)
public class HpoCategoryMap {
  Logger LOGGER = LoggerFactory.getLogger(HpoCategoryMap.class);
  /**
   * An array containing all of the {@link TermId} objects that correspond to the categories.
   */
  private final TermId[] termIdList;
  /**
   * Key: the HPO TermId, value: HpoCategory for this term. For instance, the HPO TermId HP:0001626
   * (Abnormality of the cardiovascular system) would correspond to the category Cardiovascular and
   * several subcategories.
   */
  private final Map<TermId, HpoCategory> categorymap;

  private static final TermId INHERITANCE_ID = TermId.of("HP:0000005");
  /** Clinical course includes ONSET, Mortality, temporal, and pace of progression. */
  private static final TermId CLINICAL_COURSE_ID = TermId.of("HP:0031797");
  private static final TermId ABNORMAL_CELLULAR_ID = TermId.of("HP:0025354");
  private static final TermId BLOOD_ID = TermId.of("HP:0001871");
  private static final TermId CONNECTIVE_TISSUE_ID = TermId.of("HP:0003549");
  private static final TermId HEAD_AND_NECK_ID = TermId.of("HP:0000152");
  private static final TermId LIMBS_ID = TermId.of("HP:0040064");
  private static final TermId METABOLISM_ID = TermId.of("HP:0001939");
  private static final TermId PRENATAL_ID = TermId.of("HP:0001197");
  private static final TermId BREAST_ID = TermId.of("HP:0000769");
  private static final TermId CARDIOVASCULAR_ID = TermId.of("HP:0001626");
  private static final TermId DIGESTIVE_ID = TermId.of("HP:0025031");
  private static final TermId EAR_ID = TermId.of("HP:0000598");
  private static final TermId ENDOCRINE_ID = TermId.of("HP:0000818");
  private static final TermId EYE_ID = TermId.of("HP:0000478");
  private static final TermId GENITOURINARY_ID = TermId.of("HP:0000119");
  private static final TermId IMMUNOLOGY_ID = TermId.of("HP:0002715");
  private static final TermId INTEGUMENT_ID = TermId.of("HP:0001574");
  private static final TermId MUSCLE_ID = TermId.of("HP:0003011");
  private static final TermId NERVOUS_SYSTEM_ID = TermId.of("HP:0000707");
  private static final TermId RESPIRATORY_ID = TermId.of("HP:0002086");
  private static final TermId SKELETAL_ID = TermId.of("HP:0000924");
  private static final TermId THORACIC_CAVITY_ID = TermId.of("HP:0045027");
  private static final TermId VOICE_ID = TermId.of("HP:0001608");
  private static final TermId CONSTITUTIONAL_ID = TermId.of("HP:0025142");
  private static final TermId GROWTH_ID = TermId.of("HP:0001507");
  private static final TermId NEOPLASM_ID = TermId.of("HP:0002664");

  public HpoCategoryMap() {
    termIdList = new TermId[]{INHERITANCE_ID, ABNORMAL_CELLULAR_ID, BLOOD_ID, CONNECTIVE_TISSUE_ID, HEAD_AND_NECK_ID,
      LIMBS_ID, METABOLISM_ID, PRENATAL_ID, BREAST_ID, CARDIOVASCULAR_ID, DIGESTIVE_ID,CLINICAL_COURSE_ID,
      EAR_ID, ENDOCRINE_ID, EYE_ID, GENITOURINARY_ID, IMMUNOLOGY_ID, INTEGUMENT_ID,
      MUSCLE_ID, NERVOUS_SYSTEM_ID, RESPIRATORY_ID, SKELETAL_ID, THORACIC_CAVITY_ID,
      VOICE_ID, GROWTH_ID, CONSTITUTIONAL_ID, NEOPLASM_ID};
    Map<TermId, HpoCategory> mapbuilder = new HashMap<>();
    // Inheritance
    HpoCategory inheritanceCategory = new HpoCategory(INHERITANCE_ID, "Inheritance");
    mapbuilder.put(INHERITANCE_ID, inheritanceCategory);
    // Abn cellular phenotype
    HpoCategory abnCellCategory = new HpoCategory(ABNORMAL_CELLULAR_ID, "Cellular phenotype");
    mapbuilder.put(ABNORMAL_CELLULAR_ID, abnCellCategory);
    // Blood
    HpoCategory abnBlood = new HpoCategory(BLOOD_ID, "Blood and blood-forming tissues");
    mapbuilder.put(abnBlood.id(), abnBlood);
    // Connective tissue
    HpoCategory abnConnTiss = new HpoCategory(CONNECTIVE_TISSUE_ID, "Connective tissue");
    mapbuilder.put(abnConnTiss.id(), abnConnTiss);
    // head or neck
    HpoCategory headNeckCat = new HpoCategory(HEAD_AND_NECK_ID, "Head and neck");
    mapbuilder.put(HEAD_AND_NECK_ID, headNeckCat);
    // limbs
    HpoCategory limbCat = new HpoCategory(LIMBS_ID, "Limbs");
    mapbuilder.put(LIMBS_ID, limbCat);
    // metabolism
    HpoCategory metabolismCat = new HpoCategory(METABOLISM_ID, "Metabolism/Laboratory abnormality");
    mapbuilder.put(METABOLISM_ID, metabolismCat);
    //prenatal
    HpoCategory prenatalCat = new HpoCategory(PRENATAL_ID, "Prenatal and Birth");
    mapbuilder.put(PRENATAL_ID, prenatalCat);
    //breast
    HpoCategory breastCat = new HpoCategory(BREAST_ID, "Breast");
    mapbuilder.put(BREAST_ID, breastCat);
    //cardiovascular
    HpoCategory cardiovascularCat = new HpoCategory(CARDIOVASCULAR_ID, "Cardiovascular");
    mapbuilder.put(CARDIOVASCULAR_ID, cardiovascularCat);
    //digestive
    HpoCategory digestiveCat = new HpoCategory(DIGESTIVE_ID, "Digestive System");
    mapbuilder.put(DIGESTIVE_ID, digestiveCat);
    // ear
    HpoCategory earCat = new HpoCategory(EAR_ID, "Ear");
    mapbuilder.put(EAR_ID, earCat);
    //endocrine
    HpoCategory endocrineCat = new HpoCategory(ENDOCRINE_ID, "Endocrine");
    mapbuilder.put(ENDOCRINE_ID, endocrineCat);
    // eye
    HpoCategory eyeCat = new HpoCategory(EYE_ID, "Eye");
    mapbuilder.put(EYE_ID, eyeCat);
    //genitourinary
    HpoCategory guCat = new HpoCategory(GENITOURINARY_ID, "Genitourinary system");
    mapbuilder.put(GENITOURINARY_ID, guCat);
    // Immune
    HpoCategory immuneCat = new HpoCategory(IMMUNOLOGY_ID, "Immunology");
    mapbuilder.put(IMMUNOLOGY_ID, immuneCat);
    //integument
    HpoCategory integumentCat = new HpoCategory(INTEGUMENT_ID, "Skin, Hair, and Nails");
    mapbuilder.put(INTEGUMENT_ID, integumentCat);
    //muscle
    HpoCategory muscleCat = new HpoCategory(MUSCLE_ID, "Musculature");
    mapbuilder.put(MUSCLE_ID, muscleCat);
    //Nervous system
    HpoCategory nervousCat = new HpoCategory(NERVOUS_SYSTEM_ID, "Nervous System");
    mapbuilder.put(NERVOUS_SYSTEM_ID, nervousCat);
    //respiratory system
    HpoCategory respiratoryCat = new HpoCategory(RESPIRATORY_ID, "Respiratory System");
    mapbuilder.put(RESPIRATORY_ID, respiratoryCat);
    // skeletal
    HpoCategory skeletalCat = new HpoCategory(SKELETAL_ID, "Skeletal system");
    mapbuilder.put(SKELETAL_ID, skeletalCat);
    //thoracic cavity
    HpoCategory thoracicCat = new HpoCategory(THORACIC_CAVITY_ID, "Thoracic cavity");
    mapbuilder.put(THORACIC_CAVITY_ID, thoracicCat);
    //voice
    HpoCategory voiceCat = new HpoCategory(VOICE_ID, "Voice");
    mapbuilder.put(VOICE_ID, voiceCat);
    //consistutiotnal symtpom
    HpoCategory constitutionalCat = new HpoCategory(CONSTITUTIONAL_ID, "Constitutional Symptom");
    mapbuilder.put(CONSTITUTIONAL_ID, constitutionalCat);
    // growth
    HpoCategory growthCat = new HpoCategory(GROWTH_ID, "Growth");
    mapbuilder.put(GROWTH_ID, growthCat);
    // neoplasm
    HpoCategory neoplasmCat = new HpoCategory(NEOPLASM_ID, "Neoplasm");
    mapbuilder.put(NEOPLASM_ID, neoplasmCat);

    HpoCategory clinicalCourseCat = new HpoCategory(CLINICAL_COURSE_ID, "Clinical course");
    mapbuilder.put(CLINICAL_COURSE_ID, clinicalCourseCat);
    // Finally, build the map!
    categorymap = Map.copyOf(mapbuilder);
  }

  /**
   * Client code uses this method to add a specific term to the corresponding {@link HpoCategory} object (i.e., the
   * term is a subclass of the TermId corresponding to the HpoCategory). For instance, client code might want to
   * add all of the terms that annotate a disease or a patient so that these terms can be displayed according to the
   * categories.
   *
   * @param tid      HPO term to be added
   * @param ontology reference to HPO ontology object
   * @throws PhenolRuntimeException if we try to add an invalid term (i.e., that does not correspond to a category).
   */
  public void addAnnotatedTerm(TermId tid, MinimalOntology ontology) throws PhenolRuntimeException {
      HpoCategory cat = getCategory(tid, ontology);
      if (cat == null) {
        LOGGER.warn("Could not get upper level HPO category for "
          + ontology.termForTermId(tid).map(Term::getName).orElse(null));
        return;
      }
      cat.addAnnotatedTerm(tid);
  }

  /**
   * Add a list of {@link TermId} objects using the method {@link #addAnnotatedTerm(TermId, MinimalOntology)}
   *
   * @param tidlist  list of HPO terms to be added
   * @param ontology reference to HPO ontology object
   */
  public void addAnnotatedTerms(List<TermId> tidlist, MinimalOntology ontology) {
    for (TermId tid : tidlist) {
      addAnnotatedTerm(tid, ontology);
    }
  }

  /**
   * Calculate all the categories of which this term (childTermId) is a descendant.
   *
   * @param ontology    reference to HPO Ontology
   * @param childTermId TermId for which we will find the category or categories
   * @return an immutable Set of all category ids for childTermId
   */
  private Set<TermId> getAncestorCategories(MinimalOntology ontology, TermId childTermId) {
    Set<TermId> builder = new HashSet<>();
    Deque<TermId> stack = new ArrayDeque<>();
    stack.push(childTermId);
    while (!stack.isEmpty()) {
      TermId tid = stack.pop();
      for (TermId p : ontology.graph().getParents(tid, false)) {
        if (categorymap.containsKey(p)) {
          builder.add(p);
        } else {
          stack.add(p);
        }
      }
    }
    return Set.copyOf(builder);
  }


  /**
   * Identify the category that best matches the term id (usually just
   * find the category that represents an ancestor term).
   */
  private HpoCategory getCategory(TermId tid, MinimalOntology ontology) {
    if (tid == null) {
      LOGGER.warn("Trying to get HPO Category but input TermId was null...");
      return null;
    }
    Set<TermId> ancs = getAncestorCategories(ontology, tid);
    return getPrioritizedCategory(ancs);
  }

  /**
   * This method is intended to be used by views that show HPO terms according to top level
   * categories such as "Skeletal", "Cardiovascular", etc. Because of the multiple parentage of the
   * HPO, some HPO may have more than one upper level category. For instance, Uveal melanoma would
   * have both Neoplasm, and Ophthalmology. We prioritize Neoplasm first. For other terms, if there
   * are multiple parents, we choose one at random for the display. The great majority of terms
   * will have just one parent, as so we check this first.
   * The input argument catlist represents the top level categories that some term is  annotated
   * to. It should never be empty, and will usually just contain one term. The entry/entries in catlist
   * should all be members of {@link #termIdList}.
   * We assume that catlist is never empty, which is justified for this private methods because of the
   * calling code.
   *
   * @param catlist A list of HPO TermIds that correspond to the top level categories (see
   * @return The highest-priority {@link HpoCategory}
   */
  private HpoCategory getPrioritizedCategory(Set<TermId> catlist) {
    if (catlist.size() == 1) {
      return categorymap.get(catlist.iterator().next());
    } else if (catlist.stream().anyMatch(t -> t.equals(NEOPLASM_ID))) {
      return categorymap.get(NEOPLASM_ID); // top priority if a term is mapped to >1 category
    }
    if (catlist.size() == 0) {
      LOGGER.warn("Could not find category for {}", catlist);
      return null;
    }
    // if we get here, there are multiple categories. We do not care which
    // and so we choose first one (should rarely happen).
    TermId categoryId = catlist.iterator().next();
    return categorymap.get(categoryId);
  }


  /**
   * Return neoplasm with higher priority than the other categories.
   *
   * @return the primary termId if we have more than one.
   */
  private TermId getPrioritizedTid(Set<TermId> ancestors) {
    for (TermId id : ancestors) {
      if (id.equals(NEOPLASM_ID)) {
        return NEOPLASM_ID; // we always want this
      }
    }
    return ancestors.iterator().next();
  }


  /**
   * For some purposes, we just want to display categories that have at least one HPO term. This method returns
   * a list of active categories in the order defined by {@link #termIdList}.
   *
   * @return List of all categories that have at least one HPO Term annotated
   */
  public List<HpoCategory> getActiveCategoryList() {
    List<HpoCategory> builder = new ArrayList<>(termIdList.length);
    for (TermId tid : termIdList) {
      HpoCategory cat = categorymap.get(tid);
      if (cat.hasAnnotation()) {
        builder.add(cat);
      }
    }
    return Collections.unmodifiableList(builder);
  }


}
