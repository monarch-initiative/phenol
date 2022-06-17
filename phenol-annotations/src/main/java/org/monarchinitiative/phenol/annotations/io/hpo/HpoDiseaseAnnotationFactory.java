package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

/**
 * Create {@link HpoDiseaseAnnotation} from {@link TermId}, {@link KnowsRatioAndMaybeTemporalRange}s, and {@link AnnotationReference}s.
 * <p>
 * The implementors implement one of multiple ways of integrating {@link KnowsRatioAndMaybeTemporalRange}s into {@link HpoDiseaseAnnotation}.
 */
public interface HpoDiseaseAnnotationFactory {

  static HpoDiseaseAnnotationFactory defaultInstance() {
    return HpoDiseaseAnnotationFactoryDefault.instance();
  }

  HpoDiseaseAnnotation create(TermId id,
                              Iterable<KnowsRatioAndMaybeTemporalRange> ratios,
                              List<AnnotationReference> annotationReferences);

}
