package org.monarchinitiative.phenol.annotations.formats.hpo.uberon;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UberonMap {



  private static final TermId ADIPOSE_TISSUE = TermId.of("UBERON:0001013");
  private static final TermId ABNORMAL_ADIPOSE_TISSUE_MORPHOLOGY = TermId.of("HP:0009124");
  private static final TermId BLOOD = TermId.of("UBERON:0000178");
  private static final TermId ABNORMALITY_BLOOD_AND_BLOOD_FORMING_TISSUES = TermId.of("HP:0001871");
  private static final TermId BLOOD_VESSEL = TermId.of("UBERON:0001981");
  private static final TermId ABNORMAL_VASCULAR_MORPHOLOGY = TermId.of("HP:0025015");
  private static final TermId BRAIN = TermId.of("UBERON:0000955");
  private static final TermId ABNORMAL_BRAIN_MORPHOLOGY = TermId.of("HP:0012443");
  private static final TermId ESOPHAGUS = TermId.of("UBERON:0001043");
  private static final TermId ABNORMAL_ESOPHAGUS_MORPHOLOGY = TermId.of("HP:0002031");
  private static final TermId EYE = TermId.of("UBERON:0000970");
  private static final TermId ABNORMAL_EYE_MORPHOLOGY = TermId.of("HP:0012372");
  private static final TermId FEMALE_GONAD = TermId.of("UBERON:0000992");
  private static final TermId ABNORMAL_OVARIAN_MORPHOLOGY = TermId.of("HP:0031065");
  private static final TermId GALLBLADDER = TermId.of("UBERON:0002110");
  private static final TermId ABNORMAL_GALLBLADDER_MORPHOLOGY = TermId.of("HP:0012437");
  private static final TermId HEART = TermId.of("UBERON:0000948");
  private static final TermId ABNORMAL_HEART_MORPHOLOGY = TermId.of("HP:0001627");
  private static final TermId INTERNAL_MALE_GENITALIA = TermId.of("UBERON:0004054");
  private static final TermId ABNORMAL_INTERNAL_MALE_GENITALIA_MORPHOLOGY = TermId.of("HP:0000022");
  private static final TermId KIDNEY = TermId.of("UBERON:0002113");
  private static final TermId ABNORMAL_RENAL_MORPHOLOGY = TermId.of("HP:0012210");
  private static final TermId LARGE_INTESTINE = TermId.of("UBERON:0000059");
  private static final TermId ABNORMAL_LARGE_INTESTINE_MORPHOLOGY = TermId.of("HP:0002250");
  private static final TermId LIVER = TermId.of("UBERON:0002107");
  private static final TermId ABNORMAL_LIVER_MORPHOLOGY = TermId.of("HP:0410042");
  private static final TermId LUNG = TermId.of("UBERON:0002048");
  private static final TermId ABNORMAL_LUNG_MORPHOLOGY = TermId.of("HP:0002088");
  private static final TermId LYMPH_NODE = TermId.of("UBERON:0000029");
  private static final TermId ABNORMAL_LYMPH_NODE_MORPHOLOGY = TermId.of("HP:0002733");
  private static final TermId MENINX = TermId.of("UBERON:0002360");
  private static final TermId ABNORMAL_MENINGEAL_MORPHOLOGY = TermId.of("HP:0010651");
  private static final TermId OLFACTORY_REGION = TermId.of("UBERON:0003112");
  private static final TermId ABNORMAL_OLFACTORY_LOBE_MORPHOLOGY = TermId.of("HP:0025057");
  private static final TermId PANCREAS = TermId.of("UBERON:0001264");
  private static final TermId ABNORMAL_PANCREAS_MORPHOLOGY = TermId.of("HP:0012090");
  private static final TermId PAROTID_GLAND = TermId.of("UBERON:0001831");
  private static final TermId ABNORMAL_PAROTID_GLAND_MORPHOLOGY = TermId.of("HP:0000197");
  private static final TermId PENIS = TermId.of("UBERON:0000989");
  private static final TermId ABNORMAL_PENIS_MORPHOLOGY = TermId.of("HP:0000036");
  private static final TermId PLACENTA = TermId.of("UBERON:0001987");
  private static final TermId ABNORMAL_PLACENTA_MORPHOLOGY = TermId.of("HP:0100767");
  private static final TermId PROSTATE_GLAND = TermId.of("UBERON:0002367");
  private static final TermId ABNORMAL_PROSTATE_GLAND_MORPHOLOGY = TermId.of("HP:0008775");
  private static final TermId SALIVARY_GLAND = TermId.of("UBERON:0001044");
  private static final TermId ABNORMAL_SALIVARY_GLAND_MORPHOLOGY = TermId.of("HP:0010286");
  private static final TermId SKELETAL_MUSCLE_TISSUE = TermId.of("UBERON:0001134");
  private static final TermId ABNORMAL_SKELETAL_MUSCLE_MORPHOLOGY = TermId.of("HP:0011805");
  private static final TermId SKIN_OF_BODY = TermId.of("UBERON:0002097");
  private static final TermId ABNORMAL_SKIN_MORPHOLOGY = TermId.of("HP:0011121");
  private static final TermId SMALL_INTESTINE = TermId.of("UBERON:0002108");
  private static final TermId ABNORMAL_SMALL_INTESTINE_MORPHOLOGY = TermId.of("HP:0002244");
  private static final TermId SMOOTH_MUSCLE_TISSUE = TermId.of("UBERON:0001135");
  //Closest available match
  private static final TermId ABNORMAL_INTESTINAL_SMOOTH_MUSCLE_MORPHOLOGY = TermId.of("HP:0030935");
  private static final TermId SPINAL_CORD = TermId.of("UBERON:0002240");
  private static final TermId ABNORMAL_SPINAL_CORD_MORPHOLOGY = TermId.of("HP:0002143");
  private static final TermId SPLEEN = TermId.of("UBERON:0002106");
  private static final TermId ABNORMAL_SPLEEN_MORPHOLOGY = TermId.of("HP:0025408");
  private static final TermId STOMACH = TermId.of("UBERON:0000945");
  private static final TermId ABNORMAL_STOMACH_MORPHOLOGY = TermId.of("HP:0002577");
  private static final TermId SUBMANDIBULAR_GLAND = TermId.of("UBERON:0001736");
  private static final TermId ABNORMAL_SUBMANDIBULAR_GLAND_MORPHOLOGY = TermId.of("HP:0010287");
  private static final TermId TESTIS = TermId.of("UBERON:0000473");
  private static final TermId ABNORMAL_TESTIS_MORPHOLOGY = TermId.of("HP:0000035");
  private static final TermId THROAT = TermId.of("UBERON:0000341");
  private static final TermId ABNORMAL_PHARYNX_MORPHOLOGY = TermId.of("HP:0000600");
  private static final TermId THYMUS = TermId.of("UBERON:0002370");
  private static final TermId ABNORMAL_THYMUS_MORPHOLOGY = TermId.of("HP:0000777");
  private static final TermId THYROID_GLAND = TermId.of("UBERON:0002046");
  private static final TermId ABNORMAL_THYROID_MORPHOLOGY = TermId.of("HP:0011772");
  private static final TermId TONGUE = TermId.of("UBERON:0001723");
  private static final TermId ABNORMAL_TONGUE_MORPHOLOGY = TermId.of("HP:0030809");
  private static final TermId TONSIL = TermId.of("UBERON:0002372");
  private static final TermId ABNORMAL_TONSIL_MORPHOLOGY = TermId.of("HP:0100765");
  private static final TermId UMBILICAL_CORD = TermId.of("UBERON:0002331");
  private static final TermId ABNORMAL_UMBILICAL_CORD_MORPHOLOGY = TermId.of("HP:0010881");
  private static final TermId URINARY_BLADDER = TermId.of("UBERON:0001255");
  private static final TermId ABNORMAL_BLADDER_MORPHOLOGY = TermId.of("HP:0025487");

  private static final TermId UTERUS = TermId.of("UBERON:0000995");
  private static final TermId ABNORMAL_UTERUS_MORPHOLOGY = TermId.of("HP:0031105");
  private static final TermId VAGINA = TermId.of("UBERON:0000996");
  private static final TermId ABNORMAL_VAGINA_MORPHOLOGY = TermId.of("HP:0000142");

  /** Key: An uberon term such as 'tonsil'; value: corresponding HPO abnormal morphology term. */
  private final Map<TermId, TermId> uberonToHpoMorphologyMap;

  public UberonMap() {
    Map<TermId, TermId> uberonMap = new HashMap<>();
    uberonMap.put(ADIPOSE_TISSUE, ABNORMAL_ADIPOSE_TISSUE_MORPHOLOGY);
    uberonMap.put(BLOOD, ABNORMALITY_BLOOD_AND_BLOOD_FORMING_TISSUES);
    uberonMap.put(BLOOD_VESSEL, ABNORMAL_VASCULAR_MORPHOLOGY);
    uberonMap.put(BRAIN, ABNORMAL_BRAIN_MORPHOLOGY);
    uberonMap.put(ESOPHAGUS, ABNORMAL_ESOPHAGUS_MORPHOLOGY);
    uberonMap.put(EYE, ABNORMAL_EYE_MORPHOLOGY);
    uberonMap.put(FEMALE_GONAD, ABNORMAL_OVARIAN_MORPHOLOGY);
    uberonMap.put(GALLBLADDER, ABNORMAL_GALLBLADDER_MORPHOLOGY);
    uberonMap.put(HEART, ABNORMAL_HEART_MORPHOLOGY);
    uberonMap.put(INTERNAL_MALE_GENITALIA, ABNORMAL_INTERNAL_MALE_GENITALIA_MORPHOLOGY);
    uberonMap.put(KIDNEY, ABNORMAL_RENAL_MORPHOLOGY);
    uberonMap.put(LARGE_INTESTINE, ABNORMAL_LARGE_INTESTINE_MORPHOLOGY);
    uberonMap.put(LIVER, ABNORMAL_LIVER_MORPHOLOGY);
    uberonMap.put(LUNG, ABNORMAL_LUNG_MORPHOLOGY);
    uberonMap.put(LYMPH_NODE, ABNORMAL_LYMPH_NODE_MORPHOLOGY);
    uberonMap.put(MENINX, ABNORMAL_MENINGEAL_MORPHOLOGY);
    uberonMap.put(OLFACTORY_REGION, ABNORMAL_OLFACTORY_LOBE_MORPHOLOGY);
    uberonMap.put(PANCREAS, ABNORMAL_PANCREAS_MORPHOLOGY);
    uberonMap.put(PAROTID_GLAND, ABNORMAL_PAROTID_GLAND_MORPHOLOGY);
    uberonMap.put(PENIS, ABNORMAL_PENIS_MORPHOLOGY);
    uberonMap.put(PLACENTA, ABNORMAL_PLACENTA_MORPHOLOGY);
    uberonMap.put(PROSTATE_GLAND, ABNORMAL_PROSTATE_GLAND_MORPHOLOGY);
    uberonMap.put(SALIVARY_GLAND, ABNORMAL_SALIVARY_GLAND_MORPHOLOGY);
    uberonMap.put(SKELETAL_MUSCLE_TISSUE, ABNORMAL_SKELETAL_MUSCLE_MORPHOLOGY);
    uberonMap.put(SKIN_OF_BODY, ABNORMAL_SKIN_MORPHOLOGY);
    uberonMap.put(SMALL_INTESTINE, ABNORMAL_SMALL_INTESTINE_MORPHOLOGY);
    uberonMap.put(SMOOTH_MUSCLE_TISSUE, ABNORMAL_INTESTINAL_SMOOTH_MUSCLE_MORPHOLOGY);
    uberonMap.put(SPINAL_CORD, ABNORMAL_SPINAL_CORD_MORPHOLOGY);
    uberonMap.put(SPLEEN, ABNORMAL_SPLEEN_MORPHOLOGY);
    uberonMap.put(STOMACH, ABNORMAL_STOMACH_MORPHOLOGY);
    uberonMap.put(SUBMANDIBULAR_GLAND, ABNORMAL_SUBMANDIBULAR_GLAND_MORPHOLOGY);
    uberonMap.put(TESTIS, ABNORMAL_TESTIS_MORPHOLOGY);
    uberonMap.put(THROAT, ABNORMAL_PHARYNX_MORPHOLOGY);
    uberonMap.put(THYMUS, ABNORMAL_THYMUS_MORPHOLOGY);
    uberonMap.put(THYROID_GLAND, ABNORMAL_THYROID_MORPHOLOGY);
    uberonMap.put(TONGUE, ABNORMAL_TONGUE_MORPHOLOGY);
    uberonMap.put(TONSIL, ABNORMAL_TONSIL_MORPHOLOGY);
    uberonMap.put(UMBILICAL_CORD, ABNORMAL_UMBILICAL_CORD_MORPHOLOGY);
    uberonMap.put(URINARY_BLADDER, ABNORMAL_BLADDER_MORPHOLOGY);
    uberonMap.put(UTERUS, ABNORMAL_UTERUS_MORPHOLOGY);
    uberonMap.put(VAGINA, ABNORMAL_VAGINA_MORPHOLOGY);
    this.uberonToHpoMorphologyMap = Collections.unmodifiableMap(uberonMap);
  }

  /**
   * @return map with Key: An uberon term id; value: corresponding HPO abnormal morphology term id.
   */
  public Map<TermId, TermId> getUberonToHpoMorphologyMap() {
    return uberonToHpoMorphologyMap;
  }

  /**
   * @return map with Key:  HPO abnormal morphology term id; value: corresponding uberon term id.
   */
  public Map<TermId, TermId>  getHpoMorphologyToUberonMap() {
    return  uberonToHpoMorphologyMap.entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }
}
