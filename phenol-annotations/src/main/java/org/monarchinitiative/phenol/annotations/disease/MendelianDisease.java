package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Set;

public interface MendelianDisease extends Disease {

  static MendelianDisease of(TermId id,
                             String name,
                             Set<DiseaseFeature> features,
                             List<TermId> modesOfInheritance) {
    return MendelianDiseaseDefault.of(id, name, features, modesOfInheritance);
  }

  List<TermId> modesOfInheritance();

}
