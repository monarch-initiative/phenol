package de.charite.compbio.ontolib.ontology.data;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Interface for ontologies without the all-ancestor-related convenience functions.
 * 
 * <p>
 * Most classes will want to implement {@link Ontology} as the more interesting algorithms on
 * ontologies need fast access to all ancestors of a term.
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
   * Return term id to term map including obsolete terms.
   *
   * @return {@link Map} from {@link TermId} to corresponding value of {@link Term} sub class
   *         <code>T</code>.
   */
  Map<TermId, T> getObsoleteTermMap();

  /**
   * @return {@link Map} from <code>Integer</code> edge Id to corresponding value of
   *         {@link TermRelation} sub class <code>R</code>.
   */
  Map<Integer, R> getRelationMap();

  // TODO: consolidate naming
  
  // TODO: do we need this idiosyncratic?
  boolean isRootTerm(TermId termId);

  TermId getRootTermId();

  Collection<TermId> getAllTermIds();

  Collection<TermId> getNonObsoleteTermIds();

  Collection<TermId> getObsoleteTermIds();

  Collection<T> getTerms();

  int countTerms();

}
