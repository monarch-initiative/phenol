package org.monarchinitiative.phenol.annotations.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MpGeneModel extends MpModel {
  private static final Logger logger = LoggerFactory.getLogger(MpGeneModel.class);


  /** The gene model combines MPO phenotype terms from one or multiple {@link MpSimpleModel}. These are the genotypes of the merged models */
  private List<TermId> genotypes;

  public MpGeneModel(TermId markerId, List<MpSimpleModel> modelList) {
    init(markerId, modelList.toArray(new MpSimpleModel[] {}));
  }

  /**
   * All of the models share the same gene markerId (that's why they all belong to the same MpGeneModel).
   *
   * @param markerId The id of the marker (usually a gene)
   * @param models List of models that correspond to the gene
   */
  private void init(TermId markerId, MpSimpleModel[] models) {
    this.markerId = markerId;
    mergeGenotypesAndPhenotypes(models);
  }


  private void mergeGenotypesAndPhenotypes(MpSimpleModel[] models) {
    HashMap<TermId, Set<MpAnnotation>> annotsByMpId = new HashMap<>();
    List<TermId> genotypesBuilder = new ArrayList<>(models.length);
    for (MpSimpleModel model : models) {
      // remember genotype, record annotations according to their MpId
      genotypesBuilder.add(model.getGenotypeId());
      for (MpAnnotation annot : model.getPhenotypicAbnormalities()) {
        TermId mpId = annot.getTermId();
        annotsByMpId.putIfAbsent(mpId, new HashSet<>());
        annotsByMpId.get(mpId).add(annot);
      }
    }
    // now each MP id has a set of annotations associated with it in at least one model
    List<MpAnnotation> listbuilder = new ArrayList<>(annotsByMpId.size());
    for (TermId tid : annotsByMpId.keySet()) {
      Set<String> pmids = new HashSet<>();
      Set<MpModifier> modifierSet = new HashSet<>();

      Set<MpAnnotation> setOfMap = annotsByMpId.get(tid);
      for (MpAnnotation map : setOfMap) {
        pmids.addAll(map.getPmidSet());
        modifierSet.addAll(map.getModifiers());
      }
      MpAnnotation mpa = new MpAnnotation.Builder(tid,pmids).modifiers(modifierSet).build();
      listbuilder.add(mpa);
    }
    this.phenotypicAbnormalities = Collections.unmodifiableList(listbuilder);
    this.genotypes = Collections.unmodifiableList(genotypesBuilder);
  }


  static List<MpGeneModel> createGeneModelList(List<MpSimpleModel> simpleModelList) {
    HashMap<TermId, List<MpSimpleModel>> modelsByGene = new HashMap<>();

    for (MpSimpleModel model : simpleModelList) {
      TermId geneticMarker = model.getMarkerId();
      modelsByGene.putIfAbsent(geneticMarker, new ArrayList<>());
      List<MpSimpleModel> matchingModels = modelsByGene.get(geneticMarker);
      matchingModels.add(model);
    }

    List<MpGeneModel> returnListBuilder = new ArrayList<>(modelsByGene.size());
    modelsByGene.forEach((k, v) -> returnListBuilder.add(new MpGeneModel(k, v)));
    return Collections.unmodifiableList(returnListBuilder);
  }

  public List<TermId> getGenotypes() {
    return genotypes;
  }

  public int getGenotypesCount() {
    return genotypes.size();
  }
}
