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
      .filter(a ->!a.isNegated())
      .anyMatch(MpAnnotation::maleSpecific);
  }

  /**  @return a list of male-specific MpTerm ids, and excludes sex-specific normal terms. */
  public List<TermId> getMaleSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::maleSpecific)
      .filter(a ->!a.isNegated())
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  /** @return true if this model has one or more abnormal female-specific phenotype. */
  public boolean hasFemaleSpecificAnnotation() {
    return phenotypicAbnormalities.stream()
      .filter(a ->!a.isNegated())
      .anyMatch(MpAnnotation::femaleSpecific);
  }

  /**  @return a list of sex-specific MpTerm ids, and excludes sex-specific normal terms. */
  public List<TermId> getFemaleSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::femaleSpecific)
      .filter(a ->!a.isNegated())
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  /** @return true if this model has one or more abnormal sex-specific phenotype. */
  public boolean hasSexSpecificAnnotation() {
    return phenotypicAbnormalities.stream().
      filter(a->!a.isNegated()).
      anyMatch(MpAnnotation::sexSpecific);
  }

  /**  @return a list of sex-specific MpTerm ids, and excludes sex-specific normal terms. */
  public List<TermId> getSexSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::sexSpecific)
      .filter(a ->!a.isNegated())
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  public int getTotalAnnotationCount() {
    int neg = (int)phenotypicAbnormalities.stream().filter(MpAnnotation::isNegated).count();
    return phenotypicAbnormalities.size()-neg;
  }

}
