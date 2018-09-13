package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public class MpGeneModel extends MpModel {

 public MpGeneModel(TermId genoId,
                    MpStrain background,
                    MpAllelicComposition allelicComp,
                    TermId alleleId,
                    String alleleSymbol,
                    TermId markerId,
                    List<MpAnnotation> annots){
   super(genoId,background,allelicComp,alleleId,alleleSymbol,markerId,annots);
 }


  /**
   * TODO -- decide if List<MpModel> is better
   * TODO -- construct MpGeneModel by merging the MpModels
   * @param modlist
   * @return
   */
 public static MpGeneModel fromMpModelList(MpModel... modlist) {
   return null;
 }



}
