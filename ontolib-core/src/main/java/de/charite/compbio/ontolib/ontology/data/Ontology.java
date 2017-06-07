package de.charite.compbio.ontolib.ontology.data;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import java.util.Map;

/**
 * Interface for ontologies to implement.
 *
 * <p>
 * Following the "composition over inheritance" paradigm, an
 * <code>Ontology</code> is a composed of a {@link DirectedGraph} and
 * {@link Map}s assigning {@link TermID}s and edge ids to the corresponding
 * labels.
 * </p>
 *
 * @param <T>
 *          {@link Term} sub class this <code>Ontology</code> uses
 * @param <R>
 *          {@link TermRelation} sub class this <code>Ontology</code> uses.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Ontology<T extends Term, R extends TermRelation> {

  /**
   * @return {@link DirectedGraph} describing the <code>Ontology</code>'s
   *         structure
   */
  DirectedGraph<TermID, ? extends Edge<TermID>> getGraph();

  /**
   * @return {@link Map} from {@link TermID} to corresponding value of
   *         {@link Term} sub class <code>T</code>.
   */
  Map<TermID, T> getTermMap();

  /**
   * @return {@link Map} from <code>Integer</code> edge ID to corresponding
   *         value of {@link TermRelation} sub class <code>R</code>.
   */
  Map<Integer, R> getEdgeMap();

}
