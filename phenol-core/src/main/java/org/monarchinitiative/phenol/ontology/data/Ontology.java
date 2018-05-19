package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.monarchinitiative.phenol.graph.util.GraphUtil;

/**
 * Interface for most ontology implementations to implement.
 *
 * <p>This interface adds functions for easy access to ancestors to {@link MinimalOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public interface Ontology
    extends MinimalOntology, Serializable {

  long serialVersionUID = 2L;

  /**
   * Translate {@link TermId} to primary one (in case of alternative or deprecated term IDs).
   *
   * @param termId The {@link TermId} to translate.
   * @return Primary {@link TermId} (might be the same as <code>termId</code>), <code>null</code> if
   *     none could be found.
   */
  default TermId getPrimaryTermId(TermId termId) {
    final Term term = getTermMap().get(termId);
    if (term == null) {
      return null;
    } else {
      return term.getId();
    }
  }

  /**
   * Return all the {@link TermId}s of all ancestors from {@code termId}.
   *
   * @param termId The {@link TermId} to query ancestor {@link TermId}s for.
   * @param includeRoot Whether or not to include the root.
   * @return {@link Set} of {@link TermId}s of the ancestors of {@code termId} (including itself),
   *     an empty {@link Set} if {@code termId} is not a valid term ID in the ontology.
   */
  Set<TermId> getAncestorTermIds(TermId termId, boolean includeRoot);

  /**
   * Return all the {@link TermId}s of all ancestors from {@code termId}, including root.
   *
   * @param termId The {@link TermId} to query ancestor {@link TermId}s for.
   * @return {@link Set} of {@link TermId}s of the ancestors of {@code termId} (including itself),
   *     including root.
   */
  default Set<TermId> getAncestorTermIds(TermId termId) {
    return getAncestorTermIds(termId, true);
  }

  /**
   * Return all the {@link TermId}s of all ancestors from {@code termIds}.
   *
   * @param termIds {@link Collection} of {@link TermId}s to gather all parents except for the root.
   * @param includeRoot Whether or not to include the root's {@link TermId}
   * @return {@link Set} of {@link TermId}s including all {@link TermId}s from {@code termIds},
   *     including all ancestors.
   */
  Set<TermId> getAllAncestorTermIds(Collection<TermId> termIds, boolean includeRoot);

  /**
   * Return all the {@link TermId}s of all ancestors from {@code termIds}, including root.
   *
   * @param termIds {@link Collection} of {@link TermId}s to gather all parents except for the root.
   * @return {@link Set} of {@link TermId}s including all {@link TermId}s from {@code termIds},
   *     including all ancestors, including root.
   */
  default Set<TermId> getAllAncestorTermIds(Collection<TermId> termIds) {
    return getAllAncestorTermIds(termIds, true);
  }

  /**
   * Return the {@link TermId}s of the parents of {@code termId}.
   *
   * @param termId The ID of the term to query the parents for.
   * @return The resulting parent {@link TermId}s of {@code termId}.
   */
  default Set<TermId> getParentTermIds(TermId termId) {
    final Set<TermId> result = new HashSet<>();
    final Iterator<TermId> it = GraphUtil.viaOutEdgeIterator(getGraph(), termId);
    while (it.hasNext()) {
      result.add(it.next());
    }
    return result;
  }

  /**
   * Construct and return sub ontology, starting from {@code subOntologyRoot}.
   *
   * <h5>Sub Ontology Iteration Remark</h5>
   *
   * <p>The constructed sub ontology will use the same maps from {@link TermId} to {@code T} and
   * same edge relation maps as the original ontology. However, the functions {@link
   * Ontology#getAllTermIds()}, {@link Ontology#getNonObsoleteTermIds()}, {@link
   * Ontology#getObsoleteTermIds()}, and {@link Ontology#getTerms()} will only contain the ids of
   * the sub ontology. The term ID and relation maps might contain more elements in the case of
   * creating sub ontologies and might refer to elements that are not present in the term id sets or
   * the graph.
   *
   * @param subOntologyRoot {@link TermId} to use as root of sub ontology.
   * @return Freshly created sub ontology.
   */
  Ontology subOntology(TermId subOntologyRoot);
}
