package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

public class MpGeneModel extends MpModel {
  private boolean filterAncestors;
  private List<TermId> genotypes;

  private static final Logger logger = LogManager.getLogger();

  public MpGeneModel(TermId markerId, Ontology mpoOnt, boolean filterAncestors,
              MpSimpleModel... modelList) {
    mpgmConstructor(markerId, mpoOnt, filterAncestors, modelList);
  }

  public MpGeneModel(TermId markerId, Ontology mpoOnt, boolean filterAncestors,
              List<MpSimpleModel> modelList) {
    mpgmConstructor(markerId, mpoOnt, filterAncestors, modelList.toArray(new MpSimpleModel[] {}));
  }

  /** collapses a set of MpAnnotations that all share the same MPO TermId into a single MpAnnotation
   *
   * @param annots a set of MpAnnotations that all share the same mpId and are not negated
   * @return a single MpAnnotation that has the same mpId and collects all the modifiers and
   *         PMIDs of the annotations in the input
   */
  private MpAnnotation collapseSet(Set<MpAnnotation> annots) {
    MpAnnotation returnValue;
    if (annots.isEmpty()) {
      returnValue = null;
    }
    else {
      MpAnnotation[] annotArray = annots.toArray(new MpAnnotation[]{});
      returnValue = annotArray[0];

      if (annotArray.length > 1) {
        logger.info(String.format("Starting with MpAnnotation %s", annotArray[0]));
        try {
          for (int i = 1; i < annotArray.length; i++) {
            logger.info(String.format("Merging MpAnnotation %s with annotation %s",
              returnValue, annotArray[i]));
            returnValue = MpAnnotation.merge(returnValue, annotArray[i]);
          }
        } catch (PhenolException e) {
          // exception indicates mismatch on MpId or on negated
          e.printStackTrace();
        }
        logger.info(String.format("MpAnnotation resulting from collapseSet is %s", returnValue));
      }
    }
    return returnValue;
  }

  boolean isFilterAncestors() {
    return filterAncestors;
  }

  private List<MpAnnotation> mergeSimpleModels(Ontology mpo, MpSimpleModel[] models,
                                               ImmutableList.Builder<TermId> gbuilder) {
    HashMap<TermId, Set<MpAnnotation>> annotsByMpId = new HashMap<>();
    ImmutableList.Builder<MpAnnotation> annotsBuilder = new ImmutableList.Builder<>();

    for (MpSimpleModel model : models) {
      // remember genotype, record annotations according to their MpId
      gbuilder.add(model.getGenotypeId());
      for (MpAnnotation annot : model.getPhenotypicAbnormalities()) {
        // skip over any negated annotations
        if (!annot.isNegated()) {
          TermId mpId = annot.getTermId();
          annotsByMpId.putIfAbsent(mpId, new HashSet<>());
          annotsByMpId.get(mpId).add(annot);
        }
      }
    }

    if (filterAncestors) {
      filterAncs(mpo, annotsByMpId);
    }

    annotsByMpId.values().forEach(v -> annotsBuilder.add(collapseSet(v)));
    return annotsBuilder.build();
  }

  private void filterAncs(Ontology mpo, HashMap<TermId, Set<MpAnnotation>> annotsByMpId) {
    // Check mpIds for child-ancestor pairs; keep the child and eliminate the ancestor
    for (TermId mpId : annotsByMpId.keySet()) {
      for (TermId anc : getAncestorTerms(mpo, mpId,false)) {
        annotsByMpId.remove(anc);
      }
    }
  }

/*  private Set<MpAnnotation> mergeAnnotations(Ontology mpo,
                                             HashMap<TermId, Set<MpAnnotation>> annotsByMpId) {
    // Check mpIds for child-ancestor pairs;
    // keep the child and transfer ancestors' annotations to the child
    LinkedList<TermId> mpIds = new LinkedList<>(annotsByMpId.keySet());
    int index = 0;
    while (index < mpIds.size()) {
      TermId child = mpIds.get(index);
      Set<TermId> ancestors=getAncestorTerms(mpo,child,false);

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
*/
  /*
   * All of the models share the same gene markerId (that's why they all belong to the same MpGeneModel).
   */
  private void mpgmConstructor(TermId markerId, Ontology mpo, boolean filterAncestors,
                               MpSimpleModel[] models) {
    this.markerId = markerId;
    this.filterAncestors = filterAncestors;
    ImmutableList.Builder<TermId> genotypesBuilder = new ImmutableList.Builder<>();
    phenotypicAbnormalities = mergeSimpleModels(mpo, models, genotypesBuilder);
    genotypes = genotypesBuilder.build();
  }

  static List<MpGeneModel> createGeneModelList(List<MpSimpleModel> simpleModelList,
                                               MpoOntology ontology, boolean filterAncestors) {
    HashMap<TermId, List<MpSimpleModel>> modelsByGene = new HashMap<>();
    ImmutableList.Builder<MpGeneModel> returnListBuilder = new ImmutableList.Builder<>();

    for (MpSimpleModel model : simpleModelList) {
      TermId geneticMarker = model.getMarkerId();
      modelsByGene.putIfAbsent(geneticMarker, new ArrayList<>());
      List<MpSimpleModel> matchingModels = modelsByGene.get(geneticMarker);
      matchingModels.add(model);
    }

    modelsByGene.forEach((k, v) -> returnListBuilder.add(new MpGeneModel(k, ontology, filterAncestors, v)));
    return returnListBuilder.build();
  }
}
