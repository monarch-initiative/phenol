package org.monarchinitiative.phenol.ontology.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class with static utility methods on term ids.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TermIds {

  private static final TermId OWL_THING = TermId.of("owl:Thing");

  public static TermId owlThing() {
    return OWL_THING;
  }

  private TermIds() {
  }

  /**
   * Augment list of term ids with all of their ancestors.
   *
   * @param ontology The ontology to use for augmenting with ancestors
   * @param termIds A mutable set of term ids to augment.
   * @param includeRoot Whether or not to include the root.
   * @return Augmented version of {@code termIds} (not a copy) with ancestors of all elements.
   */
  public static Set<TermId> augmentWithAncestors(Ontology ontology, Collection<TermId> termIds, boolean includeRoot) {
    Set<TermId> augmented = new HashSet<>(termIds);
    augmented.addAll(ontology.getAllAncestorTermIds(termIds, includeRoot));
    return augmented;
  }
}
