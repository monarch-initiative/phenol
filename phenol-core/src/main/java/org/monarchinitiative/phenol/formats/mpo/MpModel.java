package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is the abstract superclass of MpSimpleModel (for mouse models from MGI) and MpGeneModel (summary of
 * phenotypes at the gene level for one or more mouse models). It implements methods for accessing the set
 * of phenotypes.
 */
abstract class MpModel {
  /** Genetic marker id */
  protected TermId markerId;

  /** List of MPO term ids */
  protected List<MpAnnotation> phenotypicAbnormalities;

  public TermId getMarkerId() {
    return markerId;
  }

  public List<MpAnnotation> getPhenotypicAbnormalities() {
    return phenotypicAbnormalities;
  }

  /** @return true if this model has one or more abnormal male-specific phenotype. */
  public boolean hasMaleSpecificAnnotation() {
    return phenotypicAbnormalities.stream()
      .anyMatch(MpAnnotation::maleSpecificAbnormal);
  }

  /**  @return a list of male-specific MpTerm ids, and excludes sexSpecific-specific normal terms. */
  public List<TermId> getMaleSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::maleSpecificAbnormal)
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  /** @return true if this model has one or more abnormal female-specific phenotype. */
  public boolean hasFemaleSpecificAnnotation() {
    return phenotypicAbnormalities.stream()
      .anyMatch(MpAnnotation::femaleSpecificAbnormal);
  }

  /**  @return a list of sexSpecific-specific MpTerm ids, and excludes sexSpecific-specific normal terms. */
  public List<TermId> getFemaleSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::femaleSpecificAbnormal)
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  /** @return true if this model has one or more abnormal sexSpecific-specific phenotype. */
  public boolean hasSexSpecificAnnotation() {
    return phenotypicAbnormalities.stream().
      anyMatch(MpAnnotation::sexSpecific);
  }

  /**
   * This returns a list of TermIds for which there is at least one sex-specific annotation (regardless of whether
   * all annotations are sex-specific or not).
   * @return a list of sexSpecific-specific MpTerm ids, . */
  public List<TermId> getSexSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::sexSpecific)
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  public int getTotalAnnotationCount() {
    return phenotypicAbnormalities.size();
  }

}
