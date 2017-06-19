package de.charite.compbio.ontolib.ontology.data;

import java.util.HashSet;
import java.util.Set;

import de.charite.compbio.ontolib.formats.hpo.HpoOntology;
import de.charite.compbio.ontolib.graph.algo.BreadthFirstSearch;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;

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
  public static Set<TermId> augmentWithAncestors(ImmutableOntology<?, ?> ontology,
      Set<TermId> termIds, boolean includeRoot) {
    termIds.addAll(ontology.getAllAncestorTermIds(termIds, includeRoot));
    return termIds;
  }

}
