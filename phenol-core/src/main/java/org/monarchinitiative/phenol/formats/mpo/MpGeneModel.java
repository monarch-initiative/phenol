package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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
   * All of the models share the same gene (that's why they all belong to the same MpGeneModel).
   */
  private void mpgmConstructor(TermId markerId, MpoOntology mpo, MpSimpleModel[] models) {
    this.markerId = markerId;
    ImmutableList.Builder<TermId> genotypesBuilder = new ImmutableList.Builder<>();
    phenotypicAbnormalities = mergeSimpleModels(mpo, models, genotypesBuilder);
    genotypes = genotypesBuilder.build();
  }

  private List<MpAnnotation> mergeSimpleModels(MpoOntology mpo, MpSimpleModel[] models,
                                               ImmutableList.Builder<TermId> gbuilder) {
    // cannot use builder for annots because might need to remove something from set
    HashSet<MpAnnotation> annots = new HashSet<>();
    for (MpSimpleModel model : models) {
      gbuilder.add(model.getGenotypeId());
      mergeAnnotations(mpo, annots, model.getPhenotypicAbnormalities());
    }
    return ImmutableList.copyOf(annots);
  }

  private void mergeAnnotations(MpoOntology mpo, Set<MpAnnotation> alreadySeen, List<MpAnnotation> newAnnotations) {
    // to be added from newAnnotations to alreadySeen
    Set<MpAnnotation> toBeAdded = new HashSet<>();
    // to be removed from alreadySeen because some new annotation is better
    Set<MpAnnotation> toBeRemoved = new HashSet<>();
    // Filter out negated annotations
    Set<MpAnnotation> newAnnots = newAnnotations.stream()
      .filter(annot -> !annot.isNegated())
      .collect(Collectors.toCollection(HashSet<MpAnnotation>::new));

    // If no annotations seen previously, just keep everything that is left after filtering
    if (alreadySeen.isEmpty()) {
      alreadySeen.addAll(newAnnots);
    } else {
      // Compare the new annotations to what has already been seen, decide what to keep and what to toss
      for (MpAnnotation newAnnot : newAnnots) {
        TermId newTermId = newAnnot.getTermId();
        Set<TermId> ancestors = mpo.getAncestorTermIds(newTermId);
        ancestors.remove(newTermId);

        for (MpAnnotation existing : alreadySeen) {
          TermId existingTermId = existing.getTermId();
          if (existingTermId.equals(newTermId)) {
            // what to do if they are both sex specific, maybe for the same sex or maybe for different sexes?
            if (newAnnot.sexSpecific() && !existing.sexSpecific()) {
              toBeAdded.add(newAnnot);
              toBeRemoved.add(existing);
            }
          }
          else {
            // check whether existingTermId is an ancestor of newTermId; keep child and toss the ancestor
            if (ancestors.contains(existingTermId)) {
              toBeAdded.add(newAnnot);
              toBeRemoved.add(existing);
            }
          }
// also need to check whether newTermId is ancestor of existingTermId
        }
      }
      alreadySeen.removeAll(toBeRemoved);
      alreadySeen.addAll(toBeAdded);
    }
  }

  private void mergeAnnotation(Set alreadySeen, MpAnnotation newAnnotation) {
    boolean returnValue = false;
  }

  /**
   * TODO -- decide if List<MpSimpleModel> is better
   * TODO -- construct MpGeneModel by merging the MpModels
   * @param modlist
   * @return

  public static MpGeneModel fromMpModelList(MpSimpleModel... modlist) {
  return null;
  }
   */


}
