package org.monarchinitiative.phenol.annotations.formats.hpo.maps;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The purpose of this class is the relate CEll Ontology terms to the closest HPO term.
 * This can be used to relate experimental data (e.g., FANTOM5 transcribed enhancers)
 * to phenotypic abnormalities that might be related to the cell type.
 * @author  Peter N Robinson
 */
public class CellOntologyMap extends HpoTermsForMap {
  /** A secretory cell that is grouped together with other cells of the same type to
   * form grape shaped clusters known as acini (singular acinus). Not mapped.
    */
  private static final TermId ACINAR_CELL = TermId.of("CL:0000622");
  private static final TermId AMNIOTIC_EPITHELIAL_CELL = TermId.of("CL:0002536");
  private static final TermId ABNORMAL_OF_PLACENTAL_MEMBRANES = TermId.of("HP:0011409");
  private static final TermId ASTROCYTE = TermId.of("CL:0000127");
  private static final TermId BASOPHIL = TermId.of("CL:0000767");

  private static final TermId BLOOD_VESSEL_ENDOTHELIAL_CELL = TermId.of("CL:0000071");
  private static final TermId BRONCHIAL_SMOOTH_MUSCLE_CELL = TermId.of("CL:0002598");
  private static final TermId ABNORMAL_BRONCHUS_MORPHOLOGY = TermId.of("HP:0025426");
  private static final TermId CARDIAC_FIBROBLAST = TermId.of("CL:0002548");
  private static final TermId CARDIAC_MYOCYTE = TermId.of("CL:0000746");
  private static final TermId ABNORMAL_HEART_MORPHOLOGY = TermId.of("HP:0001627");
  private static final TermId CHONDROCYTE = TermId.of("CL:0000138");
  private static final TermId ABNORMAL_CARTILAGE_MORPHOLOGY = TermId.of("HP:0002763");
  private static final TermId CILIATED_EPITHELIAL_CELL = TermId.of("CL:0000067");
  private static final TermId CORNEAL_EPITHELIAL_CELL = TermId.of("CL:0000575");
  private static final TermId ABNORMAL_CORNEAL_EPITHELIUM_MORPHOLOGY = TermId.of("HP:0011495");
  private static final TermId DENDRITIC_CELL = TermId.of("CL:0000451");
  private static final TermId ENDOTHELIAL_CELL_OF_HEPATIC_SINUSOID = TermId.of("CL:1000398");
  private static final TermId ENDOTHELIAL_CELL_OF_LYMPHATIC_VESSEL = TermId.of("CL:0002138");
  private static final TermId FIBROBLAST_OF_LYMPHATIC_VESSEL = TermId.of("CL:0002554");
  private static final TermId ABNORMAL_LYMPHATIC_VESSEL_MORPHOLOGY = TermId.of("HP:0100766");
  private static final TermId ENTERIC_SMOOTH_MUSCLE_CELL = TermId.of("CL:0002504");
  private static final TermId ABNORMAL_INTESTINAL_SMOOTH_MUSCLE_MORPHOLOGY = TermId.of("HP:0030935");
  private static final TermId EPITHELIAL_CELL_OF_ESOPHAGUS = TermId.of("CL:0002252");
  private static final TermId ABNORMAL_ESOPHAGUS_MORPHOLOGY = TermId.of("HP:0002031");
  private static final TermId EPITHELIAL_CELL_OF_MALASSEZ = TermId.of("CL:0002166");
  private static final TermId EPITHELIAL_CELL_OF_PROSTATE = TermId.of("CL:0002231");
  private static final TermId FAT_CELL = TermId.of("CL:0000136");
  private static final TermId FIBROBLAST_OF_CHOROID_PLEXUS = TermId.of("CL:0002549");
  private static final TermId FIBROBLAST_OF_GINGIVA = TermId.of("CL:0002552");
  private static final TermId FIBROBLAST_OF_PERIODONTIUM = TermId.of("CL:0002556");
  private static final TermId FIBROBLAST_OF_PULMONARY_ARTERY = TermId.of("CL:0002557");
  private static final TermId FIBROBLAST_OF_CONJUCTIVA = TermId.of("CL:0002550");
  private static final TermId FIBROBLAST_OF_TUNICA_ADVENTITIA_OF_ARTERY = TermId.of("CL:1000306");
  private static final TermId GINGIVAL_EPITHELIAL_CELL = TermId.of("CL:0002621");
  private static final TermId HAIR_FOLLICLE_CELL = TermId.of("CL:0002559");
  private static final TermId HEPATIC_STELLATE_CELL = TermId.of("CL:0000632");
  private static final TermId HEPATOCYTE = TermId.of("CL:0000182");
  private static final TermId INTESTINAL_EPITHELIAL_CELL = TermId.of("CL:0002563");
  private static final TermId IRIS_PIGMENT_EPITHELIAL_CELL = TermId.of("CL:0002565");
  private static final TermId KERATINOCYTE = TermId.of("CL:0000312");
  /** A keratocyte is a specialized fibroblastÂ residing in the cornea stroma */
  private static final TermId KERATOCYTE = TermId.of("CL:0002363");
  private static final TermId KIDNEY_EPITHELIAL_CELL = TermId.of("CL:0002518");
  private static final TermId LENS_EPITHELIAL_CELL = TermId.of("CL:0002224");
  private static final TermId LYMPHOCYTE_OF_B_LINEAGE = TermId.of("CL:0000945");
  private static final TermId MACROPHAGE = TermId.of("CL:0000235");
  private static final TermId MAMMARY_EPITHELIAL_CELL = TermId.of("CL:0002327");
  private static final TermId MAST_CELL = TermId.of("CL:0000097");
  private static final TermId MELANOCYTE = TermId.of("CL:0000148");
  private static final TermId MESENCHYMAL_CELL = TermId.of("CL:0000134");
  private static final TermId MESOTHELIAL_CELL = TermId.of("CL:0000077");
  private static final TermId MONOCYTE = TermId.of("CL:0000576");
  private static final TermId MYOBLAST = TermId.of("CL:0000056");
  private static final TermId NATURAL_KILLER_CELL = TermId.of("CL:0000623");
  private static final TermId NEURON = TermId.of("CL:0000540");
  private static final TermId NEURONAL_STEM_CELL = TermId.of("CL:0000047");
  private static final TermId NEUTROPHIL = TermId.of("CL:0000775");
  private static final TermId OSTEOBLAST = TermId.of("CL:0000062");
  private static final TermId PERICYTE_CELL = TermId.of("CL:0000669");
  private static final TermId PLACENTAL_EPITHELIAL_CELL = TermId.of("CL:0002577");
  private static final TermId PREADIPOCYTE = TermId.of("CL:0002334");
  private static final TermId RESPIRATORY_EPITHELIAL_CELL = TermId.of("CL:0002368");
  private static final TermId RETICULOCYTE = TermId.of("CL:0000558");
  private static final TermId RETINAL_PIGMENT_EPITHELIAL_CELL = TermId.of("CL:0002586");
  private static final TermId SENSORY_EPITHELIAL_CELL = TermId.of("CL:0000098");
  private static final TermId SKELETAL_MUSCLE_CELL = TermId.of("CL:0000188");
  private static final TermId SKIN_FIBROBLAST = TermId.of("CL:0002620");
  private static final TermId SMOOTH_MUSCLE_CELL_OF_PROSTATE = TermId.of("CL:1000487");
  private static final TermId SMOOTH_MUSCLE_CELL_OF_ESOPHAGUS = TermId.of("CL:0002599");
  private static final TermId SMOOTH_MUSCLE_CELL_OF_TRACHEA = TermId.of("CL:0002600");
  private static final TermId STROMAL_CELL = TermId.of("CL:0000499");
  private static final TermId T_CELL = TermId.of("CL:0000084");
  private static final TermId TENDON_CELL = TermId.of("CL:0000388");
  private static final TermId TRABECULAR_MESHWORK_CELL = TermId.of("CL:0002367");
  private static final TermId UROTHELIAL_CELL = TermId.of("CL:0000731");
  private static final TermId UTERINE_SMOOTH_MUSCLE_CELL = TermId.of("CL:0002601");
  private static final TermId VASCULAR_ASSOCIATED_SMOOTH_MUSCLE_CELL = TermId.of("CL:0000359");

  /** Key: An uberon term such as 'tonsil'; value: corresponding HPO abnormal morphology term. */
  private final Map<TermId, TermId> cellOntologyToHpoMap;

  public CellOntologyMap() {
    Map<TermId, TermId> cellOntologyMap = new HashMap<>();
    cellOntologyMap.put(AMNIOTIC_EPITHELIAL_CELL, ABNORMAL_OF_PLACENTAL_MEMBRANES);
    cellOntologyMap.put(ASTROCYTE, ABNORMAL_ASTROCYTE_MORPHOLOGY);
    cellOntologyMap.put(BASOPHIL, ABNORMAL_BASOPHIL_MORPHOLOGY);
    cellOntologyMap.put(BRONCHIAL_SMOOTH_MUSCLE_CELL, ABNORMAL_BRONCHUS_MORPHOLOGY);
    cellOntologyMap.put(CARDIAC_FIBROBLAST, ABNORMAL_HEART_MORPHOLOGY);
    cellOntologyMap.put(CARDIAC_MYOCYTE, ABNORMAL_HEART_MORPHOLOGY);
    cellOntologyMap.put(CHONDROCYTE, ABNORMAL_CARTILAGE_MORPHOLOGY);
    cellOntologyMap.put(CORNEAL_EPITHELIAL_CELL, ABNORMAL_CORNEAL_EPITHELIUM_MORPHOLOGY);
    cellOntologyMap.put(ENDOTHELIAL_CELL_OF_LYMPHATIC_VESSEL, ABNORMAL_LYMPHATIC_VESSEL_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_LYMPHATIC_VESSEL, ABNORMAL_LYMPHATIC_VESSEL_MORPHOLOGY);
    cellOntologyMap.put(ENTERIC_SMOOTH_MUSCLE_CELL, ABNORMAL_INTESTINAL_SMOOTH_MUSCLE_MORPHOLOGY);
    cellOntologyMap.put(EPITHELIAL_CELL_OF_ESOPHAGUS, ABNORMAL_ESOPHAGUS_MORPHOLOGY);
    cellOntologyMap.put(EPITHELIAL_CELL_OF_PROSTATE, ABNORMAL_PROSTATE_MORPHOLOGY);
    cellOntologyMap.put(SMOOTH_MUSCLE_CELL_OF_PROSTATE, ABNORMAL_PROSTATE_MORPHOLOGY);
    cellOntologyMap.put(PREADIPOCYTE, ABNORMAL_ADIPOSE_TISSUE_MORPHOLOGY);
    cellOntologyMap.put(FAT_CELL, ABNORMAL_ADIPOSE_TISSUE_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_CHOROID_PLEXUS, ABNORMAL_CHOROID_PLEXUS_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_GINGIVA, ABNORMAL_GINGIVA_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_PERIODONTIUM, ABNORMAL_PERIODONTIUM_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_PULMONARY_ARTERY, ABNORMAL_PULMONARY_ARTERY_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_CONJUCTIVA, ABNORMAL_CONJUNCTIVA_MORPHOLOGY);
    cellOntologyMap.put(FIBROBLAST_OF_TUNICA_ADVENTITIA_OF_ARTERY, ABNORMAL_SYSTEMIC_ARTERIAL_MORPHOLOGY);
    cellOntologyMap.put(GINGIVAL_EPITHELIAL_CELL, ABNORMAL_GINGIVA_MORPHOLOGY);
    cellOntologyMap.put(HAIR_FOLLICLE_CELL, ABNORMAL_HAIR_MORPHOLOGY);
    cellOntologyMap.put(HEPATIC_STELLATE_CELL, ABNORMALITY_LIVER);
    cellOntologyMap.put(HEPATOCYTE, ABNORMALITY_LIVER);
    cellOntologyMap.put(IRIS_PIGMENT_EPITHELIAL_CELL, ABNORMAL_IRIS_PIGMENTATION);
    cellOntologyMap.put(KERATOCYTE, ABNORMAL_CORNEAL_STROMA_MORPHOLOGY);
    cellOntologyMap.put(KIDNEY_EPITHELIAL_CELL, ABNORMALITY_KIDNEY);
    cellOntologyMap.put(LENS_EPITHELIAL_CELL, ABNORMAL_LENS_MORPHOLOGY);
    cellOntologyMap.put(LYMPHOCYTE_OF_B_LINEAGE, ABNORMAL_B_CELL_MORPHOLOGY);
    cellOntologyMap.put(MACROPHAGE, ABNORMAL_MACROPHAGE_MORPHOLOGY);
    cellOntologyMap.put(MAMMARY_EPITHELIAL_CELL, ABNORMALITY_BREAST);
    cellOntologyMap.put(MAST_CELL, ABNORMAL_MAST_CELL_MORPHOLOGY);
    cellOntologyMap.put(MONOCYTE, ABNORMAL_MONOCYTE_MORPHOLOGY);
    cellOntologyMap.put(MYOBLAST, ABNORMAL_MUSCULATURE);
    cellOntologyMap.put(NATURAL_KILLER_CELL, ABNORMAL_NATURAL_KILLER_CELL_MORPHOLOGY);
    cellOntologyMap.put(NEURON, ABNORMAL_NEURON_MORPHOLOGY);
    cellOntologyMap.put(NEURONAL_STEM_CELL, ABNORMAL_NEURON_MORPHOLOGY);
    cellOntologyMap.put(NEUTROPHIL, ABNORMAL_NEUTROPHIL_MORPHOLOGY);
    cellOntologyMap.put(RESPIRATORY_EPITHELIAL_CELL, ABNORMAL_RESPIRATORY_EPITHELIUM_MORPHOLOGY);
    cellOntologyMap.put(RETINAL_PIGMENT_EPITHELIAL_CELL, ABNORMAL_RETINAL_PIGMENTATION);
    cellOntologyMap.put(SKELETAL_MUSCLE_CELL, ABNORMAL_MUSCULATURE);
    cellOntologyMap.put(SKIN_FIBROBLAST, ABNORMALITY_SKIN);
    cellOntologyMap.put(SMOOTH_MUSCLE_CELL_OF_ESOPHAGUS, ABNORMAL_ESOPHAGUS_MORPHOLOGY);
    cellOntologyMap.put(SMOOTH_MUSCLE_CELL_OF_TRACHEA, ABNORMAL_TRACHEA_MORPHOLOGY);
    cellOntologyMap.put(T_CELL, ABNORMAL_T_CELL_MORPHOLOGY);
    cellOntologyMap.put(TENDON_CELL, ABNORMAL_TENDON_MORPHOLOGY);
    cellOntologyMap.put(TRABECULAR_MESHWORK_CELL, ABNORMAL_TRABECULAR_MESHWORK_MORPHOLOGY);
    cellOntologyMap.put(UTERINE_SMOOTH_MUSCLE_CELL, ABNORMALITY_UTERUS);
    cellOntologyMap.put(VASCULAR_ASSOCIATED_SMOOTH_MUSCLE_CELL, ABNORMALITY_VASCULATURE);

    this.cellOntologyToHpoMap = Collections.unmodifiableMap(cellOntologyMap);
  }

  public Map<TermId, TermId> getCellOntologyToHpoMap() {
    return cellOntologyToHpoMap;
  }
  public Map<TermId, TermId> getHpoToCellOntologyMap() {
      return  cellOntologyToHpoMap.entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
  }

}
