package org.monarchinitiative.phenol.annotations.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;

public class MpSimpleModel extends MpModel {

  /** An object representing the mutant (and in rarer cases wildtype) alleles of this model. */
  private final MpAllelicComposition allelicComposition;
  /** An object representing the mouse strain that was mutated in this model. */
  private final MpStrain strain;
  /** Genotype id read from {@code  MGI_Pheno_Sex.rpt}. */
  private final TermId genotypeId;


  private final TermId alleleId;
  private final String alleleSymbol;



  public MpSimpleModel(TermId genoId,
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

  public TermId getAlleleId() {
    return alleleId;
  }

  public String getAlleleSymbol() {
    return alleleSymbol;
  }

  @Override
  public String toString() {
    String abn=phenotypicAbnormalities.stream().
      map(MpAnnotation::toString).
      collect(Collectors.joining("\n"));
    return String.format("[MpSimpleModel: %s] %s %s(%s): %s / %s \n%s", this.genotypeId.getValue(),
      this.markerId.getValue(),this.alleleSymbol,this.alleleId.getValue() ,
      this.allelicComposition,this.strain, abn);
  }


}
