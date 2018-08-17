package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.SortedSet;

public class MpModel {

  /** An object representing the mutant (and in rarer cases wildtype) alleles of this model. */
  MpAllelicComposition allelicComposition;
  /** An object representing the mouse strain that was mutated in this model. */
  MpStrain strain;
  /** Genotype id read from {@code  MGI_Pheno_Sex.rpt}. */
  String genotypeId;
  /** Set of MPO term ids associated with this combination of allelic composition and strain
   *  in the {@code  MGI_PhenoGenoMP.rpt} file. */
  SortedSet<MpAnnotation> phenotypicAbnormalities;

  List<MpGene> mgiGenes;

}
