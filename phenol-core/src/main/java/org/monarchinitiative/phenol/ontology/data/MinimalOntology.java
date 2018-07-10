package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

/**
 * Interface for ontologies without the all-ancestor-related convenience functions.
 *
 * <p>Most classes will want to work with {@link Ontology} instead of {@link MinimalOntology} as the
 * more interesting algorithms on ontologies need fast access to all ancestors of a term.
 *
 * <p>Following the "composition over inheritance" paradigm, an <code>Ontology</code> is a composed
 * of a {@link DefaultDirectedGraph} and {@link Map}s assigning {@link TermId}s and edge ids to the
 * corresponding labels.
 *
 * <p>The graph contains one vertex for each TermI in the loaded ontology (corresponding to <code>
 * [TermI]</code> stanzas in OBO). Terms that are just referred to are not represented in the
 * ontology.
 *
 * <p>While {@link MinimalOntology} allows access to obsolete terms in the ontology, the obsolete
 * terms are excluded from the underlying graph structure.
 *
 * <h5>Terms vs. TermI IDs</h5>
 *
 * <p>OBO files contain lists of terms (besides other entry types). Each term has one primary
 * identifier and a (possibly empty) list of alternative IDs. Terms can also be marked as obsolete.
 * While appearing simple on the surface, this has several implications for this interface:
 *
 * <ul>
 *   <li>The {@link Map}-returning function {@link #getTermMap()} returns a mapping from {@link
 *       TermId} to the corresponding {@code T} ({@code extends TermI}), regardless of whether the
 *       key value is a primary or an alternative identifier and regardless of whether the value is
 *       an obsolete or non-obsolete term.
 *   <li>The {@link Collection}-returning functions {@link #getNonObsoleteTermIds()}, {@link
 *       #getObsoleteTermIds()}, and {@link #getAllTermIds()} return the primary term identifiers
 *       for non-obsolete, obsolete, and all term IDs. In the rare case that you need to obtain a
 *       list of the alternative term ids, themselves, you have to iterate the values of these maps
 *       and also evaluate the alternative term ids.
 * </ul>
 *
 * <h5>Iterating</h5>
 *
 * <p>For iterating over terms and term IDs <b>only</b> use the the functions {@link
 * #getAllTermIds()}, {@link #getNonObsoleteTermIds()}, {@link #getObsoleteTermIds()}, and {@link
 * #getTerms()}, and of course {@link #getGraph()}. These functions will only return containers with
 * elements from this ontology. The term ID and relation maps might contain more elements in the
 * case of creating sub ontologies and might refer to elements that are not present in the term id
 * sets or the graph.
 *
 * <h5>Invariants/Properties</h5>
 *
 * <ul>
 *   <li>{@code MinimalOntology} instances have one root term (non-obsolete, having no incoming
 *       "is-a" relations), thus the graph is connected.
 *   <li>The underlying graph is acyclic and simple (no two equal directed s-t edges).
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public interface MinimalOntology extends Serializable {
  long serialVersionUID = 2L;
  /** @return {@link Map} with ontology meta information, e.g., as loaded from file. */
  Map<String, String> getMetaInfo();

  /** @return {@link DefaultDirectedGraph} describing the <code>Ontology</code>'s structure. */
  DefaultDirectedGraph<TermId, IdLabeledEdge> getGraph();

  /**
   * Return term id to term map for all primary term IDs.
   *
   * @return {@link Map} from {@link TermId} to corresponding value of {@link Term}.
   */
  Map<TermId, Term> getTermMap();

  /**
   * @return {@link Map} from <code>Integer</code> edge Id to corresponding value of {@link
   *     Relationship} sub class <code>R</code>.
   */
  Map<Integer, Relationship> getRelationMap();

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

  /** @return {@link Collection} of <b>all</b> primary {@link TermId}s. */
  Set<TermId> getAllTermIds();

  /** @return {@link Collection} of the <b>non-obsolete</b>, primary {@link TermId}s. */
  Set<TermId> getNonObsoleteTermIds();

  /** @return {@link Collection} of the <b>obsolete</b>, primary {@link TermId}s. */
  Set<TermId> getObsoleteTermIds();

  /** @return {@link Collection} of all term ({@code T}) objects, including the obsolete ones. */
  Collection<Term> getTerms();

  /** @return The number of all non-obsolete terms in the ontology. */
  default int countAllTerms() {
    return getNonObsoleteTermIds().size();
  }

  /** @return The number of all terms in the ontology. */
  default int countObsoleteTerms() {
    return getObsoleteTermIds().size();
  }

  /** @return The number of all terms in the ontology. */
  default int countNonObsoleteTerms() {
    return getNonObsoleteTermIds().size();
  }
}
