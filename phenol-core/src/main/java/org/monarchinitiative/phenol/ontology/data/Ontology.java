package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.*;

import org.monarchinitiative.phenol.utils.Sets;

/**
 * Interface for most ontology implementations to implement.
 *
 * <p>This interface adds functions for easy access to ancestors to {@link MinimalOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public interface Ontology extends MinimalOntology, Serializable {

  long serialVersionUID = 2L;

  /**
   * Translate {@link TermId} to primary one (in case of alternative or deprecated term IDs).
   *
   * @param termId The {@link TermId} to translate.
   * @return Primary {@link TermId} (might be the same as <code>termId</code>), <code>null</code> if
   *     none could be found.
   */
  default TermId getPrimaryTermId(TermId termId) {
    return termForTermId(termId)
      .map(Term::id)
      .orElse(null);
  }

  default Optional<String> getTermLabel(TermId tid) {
    return termForTermId(tid).map(Term::getName);
  }

  /**
   * Return all the {@link TermId}s of all ancestors from {@code termId}.
   *
   * @param termId The {@link TermId} to query ancestor {@link TermId}s for.
   * @param includeRoot Whether or not to include the root.
   * @return {@link Set} of {@link TermId}s of the ancestors of {@code termId} (including itself),
   *     an empty {@link Set} if {@code termId} is not a valid term ID in the ontology.
   */
  default Set<TermId> getAncestorTermIds(TermId termId, boolean includeRoot) {
    TermId root = getRootTermId();
    Set<TermId> result = new HashSet<>();
    for (TermId ancestor : graph().getAncestors(termId, true)) {
      if (includeRoot || !ancestor.equals(root))
        result.add(ancestor);
    }

    return result;
  }

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
  default Set<TermId> getAllAncestorTermIds(Collection<TermId> termIds, boolean includeRoot) {
    TermId root = getRootTermId();
    Set<TermId> result = new HashSet<>();
    for (TermId termId : termIds) {
      for (TermId ancestor : graph().getAncestors(termId, true)) {
        if (includeRoot || !ancestor.equals(root))
          result.add(ancestor);
      }
    }

    return result;
  }

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

  default Set<TermId> getCommonAncestors(TermId a, TermId b) {
    Set<TermId> ancA = new HashSet<>();
    graph().getAncestors(a, false).forEach(ancA::add);

    Set<TermId> ancB = new HashSet<>();
    graph().getAncestors(b, false).forEach(ancB::add);

    return Sets.intersection(ancA, ancB);
  }

  default boolean containsTerm(TermId tid) {
    return termForTermId(tid).isPresent();
  }

  /**
   * Return the {@link TermId}s of the parents of {@code termId}.
   *
   * @param termId The ID of the term to query the parents for.
   * @return The resulting parent {@link TermId}s of {@code termId}.
   * @deprecated use {@link #graph()} to get an instance of {@link org.monarchinitiative.phenol.graph.OntologyGraph}
   * and then call {@link org.monarchinitiative.phenol.graph.OntologyGraph#getParents(Object, boolean)}.
   * The method will be removed in <em>2.0.2</em>.
   */
  @Deprecated(forRemoval = true, since = "2.0.2")
  default Set<TermId> getParentTermIds(TermId termId) {
    Set<TermId> parents = new HashSet<>();
    graph().getParents(termId, false).forEach(parents::add);
    return parents;
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
   * @deprecated the method's purpose is questionable, it is non-trivial to implement, and it is very likely that all
   * the intended functionality can be implemented by working with iterators and other methods of the {@linkplain MinimalOntology}.
   * The method will be removed <em>without replacement</em> in <em>3.0.0</em>.
   */
  // REMOVE(v3.0.0)
  @Deprecated(forRemoval = true, since = "2.0.2")
  Ontology subOntology(TermId subOntologyRoot);


}
