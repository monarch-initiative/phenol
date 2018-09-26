package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class MpGeneModel extends MpModel {
  private List<TermId> genotypes;

  public MpGeneModel(TermId markerId, MpoOntology mpoOnt,
                     MpSimpleModel... modelList) throws PhenolException {
    mpgmConstructor(markerId, mpoOnt, modelList);
  }

  public MpGeneModel(TermId markerId, MpoOntology mpoOnt,
                     List<MpSimpleModel> modelList) throws PhenolException {
    mpgmConstructor(markerId, mpoOnt, modelList.toArray(new MpSimpleModel[] {}));
  }

  private void mpgmConstructor(TermId markerId, MpoOntology mpo, MpSimpleModel[] models) throws PhenolException {
    this.markerId = markerId;
    this.phenotypicAbnormalities = mergeSimpleModels(mpo, models);
    // TODO: initialize genotypes builder ;
  }

  private List<MpAnnotation> mergeSimpleModels(MpoOntology mpo, MpSimpleModel[] models) throws PhenolException {
    HashSet<MpAnnotation> annots = new HashSet<>();
    for (MpSimpleModel model : models) {
      if (model.getMarkerId() != markerId) {
        throw new PhenolException(String.format(
          "Cannot create MpGeneModel with genetic marker %s from component with genetic marker %s",
          markerId.getIdWithPrefix(), model.getMarkerId().getIdWithPrefix()));
      }
      // TODO: add this simple model's genotype to the genotype builder
      mergeAnnotations(mpo, annots, model.getPhenotypicAbnormalities());
    }
    ImmutableList.Builder<MpAnnotation> annotBuilder = new ImmutableList.Builder<>();
    annotBuilder.addAll(annots);
    return annotBuilder.build();
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

  private boolean compatible(Set alreadySeen, MpAnnotation newAnnotation) {
    boolean returnValue = false;
    return returnValue;
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
