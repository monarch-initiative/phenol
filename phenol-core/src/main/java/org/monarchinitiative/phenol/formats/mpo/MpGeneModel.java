package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

class MpGeneModel extends MpModel {
  private List<TermId> genotypes;

  MpGeneModel(TermId markerId, MpoOntology mpoOnt,
              MpSimpleModel... modelList) {
    mpgmConstructor(markerId, mpoOnt, modelList);
  }

  MpGeneModel(TermId markerId, MpoOntology mpoOnt,
              List<MpSimpleModel> modelList) {
    mpgmConstructor(markerId, mpoOnt, modelList.toArray(new MpSimpleModel[] {}));
  }

  /*
   * All of the models share the same gene markerId (that's why they all belong to the same MpGeneModel).
   */
  private void mpgmConstructor(TermId markerId, MpoOntology mpo, MpSimpleModel[] models) {
    this.markerId = markerId;
    ImmutableList.Builder<TermId> genotypesBuilder = new ImmutableList.Builder<>();
    phenotypicAbnormalities = mergeSimpleModels(mpo, models, genotypesBuilder);
    genotypes = genotypesBuilder.build();
  }

  private List<MpAnnotation> mergeSimpleModels(MpoOntology mpo, MpSimpleModel[] models,
                                               ImmutableList.Builder<TermId> gbuilder) {
    HashMap<TermId, Set<MpAnnotation>> annotsByMpId = new HashMap<>();

    for (MpSimpleModel model : models) {
      // remember genotype, record annotations according to their MpId
      gbuilder.add(model.getGenotypeId());
      for (MpAnnotation annot : model.getPhenotypicAbnormalities()) {
        // skip over any negated annotations
        if (!annot.isNegated()) {
          TermId mpId = annot.getTermId();
          Set<MpAnnotation> annotSet = annotsByMpId.get(mpId);
          if (annotSet == null) {
            annotSet = new HashSet<>();
            annotSet.add(annot);                   // need to write equals, hashcode functions for MpAnnotation
            annotsByMpId.put(mpId, annotSet);      // or maybe merge together all MpAnnotations for the same MpId?
          } else {
            annotSet.add(annot);
          }
        }
      }
    }
    Set<MpAnnotation> annots = mergeAnnotations(mpo, annotsByMpId);
    return ImmutableList.copyOf(annots);
  }

  private Set<MpAnnotation> mergeAnnotations(MpoOntology mpo, HashMap<TermId, Set<MpAnnotation>> annotsByMpId) {
    // Check mpIds for child-ancestor pairs; keep the child and transfer the parents' annotations to the child
    LinkedList<TermId> mpIds = new LinkedList<>(annotsByMpId.keySet());
    int index = 0;
    while (index < mpIds.size()) {
      TermId child = mpIds.get(index);
      Set<TermId> ancestors = mpo.getAncestorTermIds(child, false);
      // getAncestorTermIds method includes the starting vertex in the set of ancestors
      ancestors.remove(child);
      for (TermId anc : ancestors) {
        int ancIndex = mpIds.indexOf(anc);
        if (ancIndex > -1) {
          // ancestor is among the mpIds associated with this gene
          // merge ancestor's annotations into the child's
          Set<MpAnnotation> ancAnnots = annotsByMpId.get(anc);
          annotsByMpId.get(child).addAll(ancAnnots);
          // and remove the ancestor from further consideration
          mpIds.remove(ancIndex);
        }
      }
      index++;
    }
    // mpIds now contains the list of termIds for which we want to retain annotations. This will be smaller
    // than the keySet of annotsByMpId if some ancestor termIds were eliminated.
    Set<MpAnnotation> returnSet = new HashSet<>();
    for (TermId mpId : mpIds) {
      returnSet.addAll(annotsByMpId.get(mpId));
    }
    return returnSet;
  }

  /*
  public static MpGeneModel fromMpModelList(MpSimpleModel... modlist) {
  return null;
  }
   */
}
