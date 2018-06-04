package org.monarchinitiative.phenol.ontology.data;

import java.util.Set;

/**
 * Helper class with static utility methods on term ids.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TermIds {

  /**
   * Augment list of term ids with all of their ancestors.
   *
   * @param ontology The ontology to use for augmenting with ancestors
   * @param termIds The set of term ids to augment.
   * @param includeRoot Whether or not to include the root.
   * @return Augmented version of {@code termIds} (not a copy) with ancestors of all elements.
   */
  public static Set<TermId> augmentWithAncestors(
    ImmutableOntology ontology, Set<TermId> termIds, boolean includeRoot) {
    termIds.addAll(ontology.getAllAncestorTermIds(termIds, includeRoot));
    return termIds;
  }
}
