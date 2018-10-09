package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class MpGeneModel extends MpModel {
  private List<TermId> genotypes;

  MpGeneModel(TermId markerId, MpoOntology mpoOnt,
              MpSimpleModel... modelList) {
    mpgmConstructor(markerId, mpoOnt, modelList);
  }

  public MpGeneModel(TermId markerId, Ontology mpoOnt,
              List<MpSimpleModel> modelList) {
    mpgmConstructor(markerId, mpoOnt, modelList.toArray(new MpSimpleModel[] {}));
  }

  /*
   * All of the models share the same gene markerId (that's why they all belong to the same MpGeneModel).
   */
  private void mpgmConstructor(TermId markerId, Ontology mpo, MpSimpleModel[] models) {
    this.markerId = markerId;
    ImmutableList.Builder<TermId> genotypesBuilder = new ImmutableList.Builder<>();
    phenotypicAbnormalities = mergeSimpleModels(mpo, models, genotypesBuilder);
    genotypes = genotypesBuilder.build();
  }

  private List<MpAnnotation> mergeSimpleModels(Ontology mpo, MpSimpleModel[] models,
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
            annotSet.add(annot);
            annotsByMpId.put(mpId, annotSet);
          } else {
            annotSet.add(annot);
          }
        }
      }
    }
    Set<MpAnnotation> annots = mergeAnnotations(mpo, annotsByMpId);
    return ImmutableList.copyOf(annots);
  }

  private Set<MpAnnotation> mergeAnnotations(Ontology mpo,
                                             HashMap<TermId, Set<MpAnnotation>> annotsByMpId) {
    // Check mpIds for child-ancestor pairs;
    // keep the child and transfer ancestors' annotations to the child
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
    // mpIds now contains the list of termIds for which we want to retain annotations. This will be
    // smaller than the keySet of annotsByMpId if some ancestor termIds were eliminated.
    Set<MpAnnotation> returnSet = new HashSet<>();
    for (TermId mpId : mpIds) {
      // call to collapseSet will combine all the MpAnnotations for a given MpId into one
      // MpAnnotation object, which is then added to result set
      returnSet.add(collapseSet(annotsByMpId.get(mpId)));
    }
    return returnSet;
  }

  /** collapses a set of MpAnnotations into a single MpAnnotation
   *
   * @param annots a set of MpAnnotations that all share the same MpId and are not negated
   * @return a single MpAnnotation that has the same MpId and collects all the modifiers and
   *         PMIDs of the annotations in the input
   */
  private MpAnnotation collapseSet(Set<MpAnnotation> annots) {
    MpAnnotation[] annotArray  = annots.toArray(new MpAnnotation[] {});
    MpAnnotation returnValue = annotArray[0];

    try {
      for (int i = 1; i < annotArray.length; i++) {
        returnValue = MpAnnotation.merge(returnValue, annotArray[i]);
      }
    } catch (PhenolException e) {
      // exception won't occur provided all the annotations in the set share the same MpId and
      // are not negated
      e.printStackTrace();
    }
    return returnValue;
  }

  static List<MpGeneModel> createGeneModelList(List<MpSimpleModel> simpleModelList,
                                               MpoOntology ontology) {
    HashMap<TermId, List<MpSimpleModel>> modelsByGene = new HashMap<>();
    ImmutableList.Builder<MpGeneModel> returnListBuilder = new ImmutableList.Builder<>();

    for (MpSimpleModel model : simpleModelList) {
      TermId geneticMarker = model.getMarkerId();
      List<MpSimpleModel> matchingModels = modelsByGene.get(geneticMarker);
      if (matchingModels == null) {
        matchingModels = new ArrayList<>();
        matchingModels.add(model);
        modelsByGene.put(geneticMarker, matchingModels);
      } else {
        matchingModels.add(model);
      }
    }

    for (Map.Entry<TermId, List<MpSimpleModel>> entry : modelsByGene.entrySet()) {
      returnListBuilder.add(new MpGeneModel(entry.getKey(), ontology, entry.getValue()));
    }

    return returnListBuilder.build();
  }
}
