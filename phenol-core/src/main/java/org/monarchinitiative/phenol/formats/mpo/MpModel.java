package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;

public class MpModel {

  /** An object representing the mutant (and in rarer cases wildtype) alleles of this model. */
  private final MpAllelicComposition allelicComposition;
  /** An object representing the mouse strain that was mutated in this model. */
  private final MpStrain strain;
  /** Genotype id read from {@code  MGI_Pheno_Sex.rpt}. */
  private final TermId genotypeId;
  /** Set of MPO term ids associated with this combination of allelic composition and strain
   *  in the {@code  MGI_PhenoGenoMP.rpt} file. */
  private final List<MpAnnotation> phenotypicAbnormalities;

  private final TermId alleleId;
  private final String alleleSymbol;
  private final TermId markerId;



  public MpModel(TermId genoId,
    MpStrain background,
    MpAllelicComposition allelicComp,
    TermId alleleId,
    String alleleSymbol,
    TermId markerId,
    List<MpAnnotation> annots) {
    this.genotypeId=genoId;
    this.strain=background;
    this.allelicComposition=allelicComp;
    this.phenotypicAbnormalities=annots;
    this.alleleId=alleleId;
    this.alleleSymbol=alleleSymbol;
    this.markerId=markerId;

  }

  public MpAllelicComposition getAllelicComposition() {
    return allelicComposition;
  }

  public MpStrain getStrain() {
    return strain;
  }

  public TermId getGenotypeId() {
    return genotypeId;
  }

  public List<MpAnnotation> getPhenotypicAbnormalities() {
    return phenotypicAbnormalities;
  }

  public TermId getAlleleId() {
    return alleleId;
  }

  public String getAlleleSymbol() {
    return alleleSymbol;
  }

  public TermId getMarkerId() {
    return markerId;
  }

  public boolean hasMaleSpecificAnnotation() {
    return phenotypicAbnormalities.stream().anyMatch(MpAnnotation::maleSpecific);
  }

  public List<TermId> getMaleSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::maleSpecific)
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  public boolean hasFemaleSpecificAnnotation() {
    return phenotypicAbnormalities.stream().anyMatch(MpAnnotation::femaleSpecific);
  }

  public List<TermId> getFemaleSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::femaleSpecific)
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  public boolean hasSexSpecificAnnotation() {
    return phenotypicAbnormalities.stream().anyMatch(MpAnnotation::sexSpecific);
  }

  public List<TermId> getSexSpecificMpTermIds() {
    return phenotypicAbnormalities.stream()
      .filter(MpAnnotation::sexSpecific)
      .map(MpAnnotation::getTermId)
      .collect(Collectors.toList());
  }

  public int getTotalAnnotationCount() {
    int neg = (int)phenotypicAbnormalities.stream().filter(MpAnnotation::isNegated).count();
    return phenotypicAbnormalities.size()-neg;
  }

  @Override
  public String toString() {
    String abn=phenotypicAbnormalities.stream().
      map(MpAnnotation::toString).
      collect(Collectors.joining("\n"));
    return String.format("[MpModel: %s] %s %s(%s): %s / %s \n%s", this.genotypeId.getIdWithPrefix(),
      this.markerId.getIdWithPrefix(),this.alleleSymbol,this.alleleId.getIdWithPrefix() ,
      this.allelicComposition,this.strain, abn);
  }


}
