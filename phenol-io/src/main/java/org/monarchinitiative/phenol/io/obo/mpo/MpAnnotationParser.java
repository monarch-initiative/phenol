package org.monarchinitiative.phenol.io.obo.mpo;

import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

/**
 * Class to parse a number of files from the MGI site.
 * 1.  MGI_Strain.rpt
 * 2.  MGI_Nonstandard_Strain.rpt
 * 3.  MRK_List2.rpt  (Markers, i.e., mainly genes)
 * 4.  MGI_GenePheno.rpt (for now, omit  MGI_PhenoGenoMP.rpt).
 * 5.  MGI_Pheno_Sex.rpt
 * // Plan to have a parallel parser for http://www.mousephenotype.org/ data.
 * // The MpAnnotation class needs to be flexible enough to work with both
 * // c.f. HpAnnotation and HpDisease
 */
public class MpAnnotationParser {

  /** key. an MGI Gene id; value-corresponding MpGene object. */
  private Map<TermId, MpGene> mpGenes;

  /**
   * TODO -- pass paths of files to CTOR
   */
  public MpAnnotationParser() {
    // 1. parse mpGenes with MpGeneParser
    // 2. parse MGI_PhenoGeno.rpt file and connect the model to the gene
  }







}
