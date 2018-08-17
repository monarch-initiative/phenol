package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.SortedSet;

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


}
