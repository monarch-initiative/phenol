package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl.RatioAndTemporalIntervalAware;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Optional;

public interface HpoDiseaseAnnotationRecord extends RatioAndTemporalIntervalAware {

  static HpoDiseaseAnnotationRecord of(Ratio ratio,
                                       TemporalInterval temporalInterval,
                                       Collection<AnnotationReference> references,
                                       Sex sex,
                                       Collection<TermId> modifiers) {
    return new HpoDiseaseAnnotationRecordDefault(ratio, temporalInterval, references, sex, modifiers);
  }

  /**
   * @return sources of the annotation metadata.
   */
  Collection<AnnotationReference> references();

  Collection<TermId> modifiers();

  Optional<Sex> sex();

}
