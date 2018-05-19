package org.monarchinitiative.phenol.ontology.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.scoredist.SimilarityScoreSampling;
import com.google.common.collect.Sets;

/**
 * Helper class with static convenience functions.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class TermAnnotations {

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
  public static Map<TermId, Collection<String>> constructTermAnnotationToLabelsMap(
      Ontology ontology, Collection<? extends TermAnnotation> annotations) {
    final Map<TermId, Collection<String>> result = new HashMap<>();

    for (TermAnnotation anno : annotations) {
      for (TermId termId : ontology.getAncestorTermIds(anno.getTermId(), true)) {
        if (!result.containsKey(termId)) {
          result.put(termId, Sets.newHashSet(anno.getLabel()));
        } else {
          result.get(termId).add(anno.getLabel());
        }
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
  public static Map<String, Collection<TermId>> constructTermLabelToAnnotationsMap(
      Ontology ontology, Collection<? extends TermAnnotation> annotations) {
    final Map<String, Collection<TermId>> result = new HashMap<>();

    for (TermAnnotation anno : annotations) {
      for (TermId termId : ontology.getAncestorTermIds(anno.getTermId(), true)) {
        if (!result.containsKey(anno.getLabel())) {
          result.put(anno.getLabel(), Sets.newHashSet(termId));
        } else {
          result.get(anno.getLabel()).add(termId);
        }
      }
    }

    return result;
  }
}
