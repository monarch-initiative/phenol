package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.OntologyGraph;

/**
 * Interface for ontologies without the all-ancestor-related convenience functions.
 *
 * <p>Most classes will want to work with {@link Ontology} instead of {@link MinimalOntology} as the
 * more interesting algorithms on ontologies need fast access to all ancestors of a term.
 *
 * <p>Following the "composition over inheritance" paradigm, an <code>Ontology</code> is composed
 * of a {@link OntologyGraph} and methods {@link #termForTermId(TermId)}s {@link #relationshipById(int)}
 * for assigning {@link TermId}s and edge ids to the corresponding labels.
 *
 * <p>The graph contains one vertex for each {@linkplain TermId} in the loaded ontology
 * (corresponding to <code>[TermI]</code> stanzas in OBO).
 * Terms that are just referred to are not represented in the ontology.
 *
 * <p>While {@link MinimalOntology} allows access to {@linkplain TermId}s of obsolete {@link Term}s
 * in the ontology, the obsolete {@linkplain Term}s are excluded from the underlying graph structure.
 *
 * <h5>Terms vs. Term IDs</h5>
 *
 * <p>OBO files contain lists of terms (besides other entry types). Each term has one primary
 * identifier and a (possibly empty) list of alternative IDs. Terms can also be marked as obsolete.
 * While appearing simple on the surface, this has several implications for this interface:
 *
 * <ul>
 *   <li>The {@link Term}-returning function {@link #termForTermId(TermId)} performs a mapping from
 *       {@link TermId} to the corresponding {@code Term}, regardless of whether the
 *       key value is a primary or an alternative identifier.
 *   <li>The {@link Iterable}-returning functions {@link #nonObsoleteTermIds()},
 *       {@link #obsoleteTermIds()}, and {@link #allTermIds()} return the primary term identifiers
 *       for non-obsolete, obsolete, and all term IDs.
 * </ul>
 *
 * <h5>Iterating</h5>
 *
 * <p>For iterating over terms and term IDs <b>only</b> use the functions {@link #getTerms()},
 * {@link #nonObsoleteTermIds()}, {@link #obsoleteTermIds()}, and {@link #getTerms()},
 * . These functions will only return containers with
 * elements from this ontology. The term ID and relation maps might contain more elements in the
 * case of creating sub ontologies and might refer to elements that are not present in the term id
 * sets or the graph.
 *
 * <h5>Ontology hierarchy</h5>
 *
 * <p>{@linkplain MinimalOntology} provides an {@link OntologyGraph} which in turn supports traversals
 * of the ontology hierarchy.</p>
 *
 * <h5>Invariants/Properties</h5>
 *
 * <ul>
 *   <li>{@code MinimalOntology} instances have one root term (non-obsolete, having no incoming
 *       "is-a" relations), thus the graph is connected.
 *   <li>The underlying {@link OntologyGraph} is acyclic and simple (no two equal directed s-t edges).
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public interface MinimalOntology extends Serializable, Versioned {
  long serialVersionUID = 2L;
  /** @return {@link Map} with ontology meta information, e.g., as loaded from file. */
  Map<String, String> getMetaInfo();

  /**
   * @return {@link DefaultDirectedGraph} describing the <code>Ontology</code>'s structure.
   * @deprecated we will be replacing the JGraphT library with internal {@link OntologyGraph} API.
   * Use {@link #graph()} instead. The {@code getGraph()} method will be removed in <em>v3.0.0</em>
   */
  // REMOVE(v3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  DefaultDirectedGraph<TermId, IdLabeledEdge> getGraph();

  /**
   * Get the {@linkplain OntologyGraph} to enable working with the ontology hierarchy.
   *
   * @return the graph.
   */
  OntologyGraph<TermId> graph();

  /**
   * Return term id to term map for all primary term IDs.
   *
   * @return {@link Map} from {@link TermId} to corresponding value of {@link Term}.
   * @deprecated use {@link #termForTermId(TermId)} instead. The method will be removed in <em>v3.0.0</em>.
   */
  // REMOVE(v3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  Map<TermId, Term> getTermMap();

  /**
   * Get a {@linkplain Term} corresponding to this {@code termId}.
   *
   * @param termId non-{@code null} {@code termId}
   * @return a {@linkplain Term} or an empty optional if the {@linkplain TermId} is not in the ontology.
   */
  Optional<Term> termForTermId(TermId termId);

  /**
   * @return {@link Map} from <code>Integer</code> edge Id to corresponding value of {@link
   *     Relationship} sub class <code>R</code>.
   * @deprecated use {@link #relationshipById(int)} instead. The method will be removed in <em>v3.0.0</em>.
   */
  // REMOVE(v3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  Map<Integer, Relationship> getRelationMap();

  /**
   * Get {@linkplain Relationship} with given identifier.
   * @param relationshipId relationship id.
   * @return the corresponding {@linkplain Relationship} or {@code null} if relationship for the given id does not exist.
   */
  Optional<Relationship> relationshipById(int relationshipId);

  /**
   * Convenience method to query whether {@code termId} is the root term.
   *
   * @param termId The {@link TermId} to check "rootness" of.
   * @return {@code true} if {@code termId} is the root node.
   */
  default boolean isRootTerm(TermId termId) {
    return getRootTermId().equals(termId);
  }

  /** @return The root's {@link TermId}. */
  TermId getRootTermId();

  /**
   * Get an {@linkplain Iterable} with all term IDs (obsolete and non-obsolete).
   *
   * @return the {@linkplain Iterable}.
   */
  Iterable<TermId> allTermIds();

  /**
   * Get a {@linkplain Stream} with all term IDs (obsolete and non-obsolete).
   *
   * @return the {@linkplain Stream}.
   */
  default Stream<TermId> allTermIdsStream() {
    return getSequentialStream(allTermIds().spliterator());
  }

  /**
   * @return {@link Collection} of <b>all</b> primary {@link TermId}s.
   * @deprecated use {@link #allTermIds()} instead. The method will be removed in <code>3.0.0</code>.
   */
  // REMOVE(3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  default Set<TermId> getAllTermIds() {
    return putIntoSet(allTermIds());
  }

  /**
   * Get an {@linkplain Iterable} with <em>non-obsolete</em> term IDs.
   *
   * @return the {@linkplain Iterable}.
   */
  Iterable<TermId> nonObsoleteTermIds();

  /**
   * Get a {@linkplain Stream} with <em>non-obsolete</em> term IDs.
   *
   * @return the {@linkplain Stream}.
   */
  default Stream<TermId> nonObsoleteTermIdsStream() {
    return getSequentialStream(nonObsoleteTermIds().spliterator());
  }

  /**
   * @return {@link Collection} of the <b>non-obsolete</b>, primary {@link TermId}s.
   * @deprecated use {@link #nonObsoleteTermIds()} instead. The method will be removed in <code>3.0.0</code>.
   */
  // REMOVE(3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  default Set<TermId> getNonObsoleteTermIds() {
    return putIntoSet(nonObsoleteTermIds());
  }

  /**
   * Get an {@linkplain Iterable} with <em>obsolete</em> term IDs.
   *
   * @return the {@linkplain Iterable}.
   */
  Iterable<TermId> obsoleteTermIds();

  /**
   * @return {@link Collection} of the <b>obsolete</b>, primary {@link TermId}s.
   * @deprecated use {@link #obsoleteTermIds()} instead. The method will be removed in <code>3.0.0</code>.
   */
  // REMOVE(3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  default Set<TermId> getObsoleteTermIds() {
    return putIntoSet(obsoleteTermIds());
  }

  /**
   * Get a {@linkplain Stream} with <em>obsolete</em> term IDs.
   *
   * @return the {@linkplain Stream}.
   */
  default Stream<TermId> obsoleteTermIdsStream() {
    return getSequentialStream(obsoleteTermIds().spliterator());
  }

  /** @return {@link Collection} of all term ({@code T}) objects, including the obsolete ones. */
  Collection<Term> getTerms();

  /**
   * @return The number of all non-obsolete terms in the ontology.
   */
  // REMOVE(3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  default int countAllTerms() {
    return getNonObsoleteTermIds().size();
  }

  /**
   * @return The number of obsolete TermIds (alt_id in the OBO file).
   */
  // REMOVE(3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  default int countAlternateTermIds() {
    return getObsoleteTermIds().size();
  }

  /**
   * @return The number of all primary term ids in the ontology (It is the equivalent to the number of non-obsolete terms).
   * @deprecated use {@link #countAllTerms()} instead. The method will be removed in <em>3.0.0</em>.
   */
  // REMOVE(v3.0.0)
  @Deprecated(forRemoval = true, since = "2.0.2")
  default int countNonObsoleteTerms() {
    return countAllTerms();
  }

  private static <T> Set<T> putIntoSet(Iterable<T> iterable) {
    if (iterable instanceof Set)
      return (Set<T>) iterable;
    else {
      Set<T> termIds = new HashSet<>();
      iterable.forEach(termIds::add);
      return termIds;
    }
  }

  private static <T> Stream<T> getSequentialStream(Spliterator<T> spliterator) {
    return StreamSupport.stream(spliterator, false);
  }
}
