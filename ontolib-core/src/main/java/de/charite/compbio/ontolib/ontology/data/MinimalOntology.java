package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;

/**
 * Interface for ontologies without the all-ancestor-related convenience functions.
 * 
 * <p>
 * Most classes will want to work with {@link Ontology} instead of {@link MinimalOntology} as the
 * more interesting algorithms on ontologies need fast access to all ancestors of a term.
 * </p>
 *
 * <p>
 * Following the "composition over inheritance" paradigm, an <code>Ontology</code> is a composed of
 * a {@link DirectedGraph} and {@link Map}s assigning {@link TermId}s and edge ids to the
 * corresponding labels.
 * </p>
 *
 * <p>
 * The graph contains one vertex for each Term in the loaded ontology (corresponding to
 * <code>[Term]</code> stanzas in OBO). Terms that are just referred to are not represented in the
 * ontology.
 * </p>
 *
 * <p>
 * While {@link MinimalOntology} allows access to obsolete terms in the ontology, the obsolete terms
 * are excluded from the underlying graph structure.
 * </p>
 *
 * <h5>Terms vs. Term IDs</h5>
 *
 * <p>
 * OBO files contain lists of terms (besides other entry types). Each term has one primary
 * identifier and a (possibly empty) list of alternative IDs. Terms can also be marked as obsolete.
 * While appearing simple on the surface, this has several implications for this interface:
 * </p>
 *
 * <ul>
 * <li>The {@link Map}-returning function {@link #getTermMap()} returns a mapping from
 * {@link TermId} to the corresponding {@code T} ({@code extends Term}), regardless of whether the
 * key value is a primary or an alternative identifier and regardless of whether the value is an
 * obsolete or non-obsolete term.</li>
 * <li>The {@link Collection}-returning functions {@link #getNonObsoleteTermIds()},
 * {@link #getObsoleteTermIds()}, and {@link #getAllTermIds()} return the primary term identifiers
 * for non-obsolete, obsolete, and all term IDs. In the rare case that you need to obtain a list of
 * the alternative term ids, themselves, you have to iterate the values of these maps and also
 * evaluate the alternative term ids.</li>
 * </ul>
 *
 * <h5>Iterating</h5>
 *
 * <p>
 * For iterating over terms and term IDs <b>only</b> use the the functions {@link #getAllTermIds()},
 * {@link #getNonObsoleteTermIds()}, {@link #getObsoleteTermIds()}, and {@link #getTerms()}, and of
 * course {@link #getGraph()}. These functions will only return containers with elements from this
 * ontology. The term ID and relation maps might contain more elements in the case of creating sub
 * ontologies and might refer to elements that are not present in the term id sets or the graph.
 * </p>
 *
 * <h5>Invariants/Properties</h5>
 *
 * <ul>
 * <li>{@code MinimalOntology} instances have one root term (non-obsolete, having no incoming "is-a"
 * relations), thus the graph is connected.</li>
 * <li>The underlying graph is acyclic and simple (no two equal directed s-t edges).</li>
 * </ul>
 *
 * @param <T> {@link Term} sub class this <code>Ontology</code> uses
 * @param <R> {@link TermRelation} sub class this <code>Ontology</code> uses.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public interface MinimalOntology<T extends Term, R extends TermRelation> extends Serializable {

  /**
   * @return {@link Map} with ontology meta information, e.g., as loaded from file.
   */
  Map<String, String> getMetaInfo();

  /**
   * @return {@link DirectedGraph} describing the <code>Ontology</code>'s structure.
   */
  DirectedGraph<TermId, ? extends Edge<TermId>> getGraph();

  /**
   * Return term id to term map without obsolete terms (used internally, corresponds to vertex set).
   *
   * @return {@link Map} from {@link TermId} to corresponding value of {@link Term} sub class
   *         <code>T</code>.
   */
  Map<TermId, T> getTermMap();

  /**
   * @return {@link Map} from <code>Integer</code> edge Id to corresponding value of
   *         {@link TermRelation} sub class <code>R</code>.
   */
  Map<Integer, R> getRelationMap();

  /**
   * Convenience method to query whether {@code termId} is the root term.
   *
   * @param termId The {@link TermId} to check "rootness" of.
   * @return {@code true} if {@code termId} is the root node.
   */
  default boolean isRootTerm(TermId termId) {
    return getRootTermId().equals(termId);
  }

  /**
   * @return The root's {@link TermId}.
   */
  TermId getRootTermId();

  /**
   * @return {@link Collection} of <b>all</b> primary {@link TermId}s.
   */
  Collection<TermId> getAllTermIds();

  /**
   * @return {@link Collection} of the <b>non-obsolete</b>, primary {@link TermId}s.
   */
  Collection<TermId> getNonObsoleteTermIds();

  /**
   * @return {@link Collection} of the <b>obsolete</b>, primary {@link TermId}s.
   */
  Collection<TermId> getObsoleteTermIds();

  /**
   * @return {@link Collection} of all term ({@code T}) objects, including the obsolete ones.
   */
  Collection<T> getTerms();

  /**
   * @return The number of all terms in the ontology.
   */
  default int countTerms() {
    // TODO: rename to countAllTerms, remove from implementations?
    return getAllTermIds().size();
  }

  /**
   * @return The number of all terms in the ontology.
   */
  default int countObsoleteTerms() {
    return getObsoleteTermIds().size();
  }

  /**
   * @return The number of all terms in the ontology.
   */
  default int countNonObsoleteTerms() {
    return getNonObsoleteTermIds().size();
  }

}
