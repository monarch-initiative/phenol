package de.charite.compbio.ontolib.ontology.data;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

// TODO: allow accessing file-wide meta information

/**
 * Interface for ontologies to implement.
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
 * @param <T> {@link Term} sub class this <code>Ontology</code> uses
 * @param <R> {@link TermRelation} sub class this <code>Ontology</code> uses.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public interface Ontology<T extends Term, R extends TermRelation> extends Serializable {

  /**
   * @return {@link DirectedGraph} describing the <code>Ontology</code>'s structure.
   */
  DirectedGraph<TermId, ? extends Edge<TermId>> getGraph();

  /**
   * @return {@link Map} from {@link TermId} to corresponding value of {@link Term} sub class
   *         <code>T</code>.
   */
  Map<TermId, T> getTermMap();

  /**
   * @return {@link Map} from <code>Integer</code> edge Id to corresponding value of
   *         {@link TermRelation} sub class <code>R</code>.
   */
  Map<Integer, R> getRelationMap();

  // TODO: do we need this idiosyncratic?
  boolean isRootTerm(TermId termId);

  // TODO: should not be part of Ontology class!
  Collection<TermId> getAncestors(TermId termId);

  TermId getRootTermId();

  Collection<TermId> getTermIds();

  Collection<T> getTerms();

  int countTerms();

  // TODO: naming is bogus, should not be part of ontology class
  /**
   * Return all the terms including all ancestors terms.
   *
   * @param termIds {@link Collection} of {@link TermId}s to gather all parents except for the root.
   * @param includeRoot Whether or not to include the root's {@link TermId}
   * @return {@link Set} of {@link Term}s including all {@link Term}s from <code>terms</code>,
   *         including all ancestors, except for the root.
   */
  public Set<TermId> getAllAncestorTermIds(Collection<TermId> termIds, boolean includeRoot);

}
