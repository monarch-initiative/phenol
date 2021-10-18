package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;
import org.monarchinitiative.phenol.annotations.formats.Sex;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;

@InProgress
public interface HpoDiseaseAnnotationMetadata {

  static HpoDiseaseAnnotationMetadata of(HpoOnset onset, FrequencyBin frequency, Collection<TermId> modifiers, Sex sex) {
    return HpoDiseaseAnnotationMetadataDefault.of(onset, frequency, modifiers, sex);
  }

  HpoOnset onset();

  DiseaseAnnotationFrequency frequency();

  Collection<TermId> modifiers();

  Sex sex();

}
