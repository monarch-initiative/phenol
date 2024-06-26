package org.monarchinitiative.phenol.ontology.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.scoredist.SimilarityScoreSampling;

/**
 * Helper class with static convenience functions.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class TermAnnotations {

  private TermAnnotations() {
  }

  /**
   * Construct mapping from {@link TermId} to {@link Collection} of "world object" labels from
   * {@link Collection} of {@link TermAnnotation}s.
   *
   * <p>An {@link Ontology} will be used for the implicit assignment of annotations to ancestors.
   *
   * <p>Use this function for converting results from parsing term annotations to the appropriate
   * mapping for {@link InformationContentComputation}, for example.
   *
   * @param ontology {@link Ontology} to use for computing implicit annotations.
   * @param annotations {@link Collection} of {@link TermAnnotation}s to convert.
   * @return Constructed {@link Map} from {@link TermId} to {@link Collection} of "world object"
   *     labels.
   */
  public static Map<TermId, Collection<TermId>> constructTermAnnotationToLabelsMap(MinimalOntology ontology,
                                                                                   Collection<? extends TermAnnotation> annotations) {
    Map<TermId, Collection<TermId>> result = new HashMap<>();

    for (TermAnnotation anno : annotations) {
      for (TermId termId : ontology.graph().getAncestors(anno.id(), true)) {
        result.computeIfAbsent(termId, k -> new HashSet<>())
          .add(anno.getItemId());
      }
    }

    return result;
  }

  /**
   * Construct mapping from "world object" label to {@link Collection} of {@link TermId}s from
   * {@link Collection} of {@link TermAnnotation}s.
   *
   * <p>An {@link Ontology} will be used for the implicit assignment of annotations to ancestors.
   *
   * <p>Use this function for converting results from parsing term annotations to the appropriate
   * mapping for {@link SimilarityScoreSampling}, for example.
   *
   * @param ontology {@link Ontology} to use for computing implicit annotations.
   * @param annotations {@link Collection} of {@link TermAnnotation}s to convert.
   * @return Constructed {@link Map} from {@link TermId} to {@link Collection} of "world object"
   *     labels.
   */
  public static Map<TermId, Collection<TermId>> constructTermLabelToAnnotationsMap(MinimalOntology ontology,
                                                                                   Collection<? extends TermAnnotation> annotations) {
    Map<TermId, Collection<TermId>> result = new HashMap<>();

    for (TermAnnotation anno : annotations) {
      for (TermId termId : ontology.graph().getAncestors(anno.id(), true)) {
        result.computeIfAbsent(anno.getItemId(), key -> new HashSet<>())
          .add(termId);
      }
    }

    return result;
  }
}
