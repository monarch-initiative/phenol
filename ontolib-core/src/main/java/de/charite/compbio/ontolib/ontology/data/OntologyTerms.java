package de.charite.compbio.ontolib.ontology.data;

import java.util.HashSet;
import java.util.Set;

import de.charite.compbio.ontolib.graph.algo.BreadthFirstSearch;
import de.charite.compbio.ontolib.graph.algo.VertexVisitor;
import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;

/**
 * Helper class with static functions for navigating Ontology terms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class OntologyTerms {

  // TODO: do we really want static functions here or not rather graph algorithm like structures?

  /**
   * Return set of all children of {@code termId} in {@code ontology}.
   *
   * @param termId The root of the sub ontology DAG to query for.
   * @param ontology The {@link ImmutableOntology} to iterate in.
   * @return Newly created {@link Set} with {@link TermId}s of children of the term corresponding to
   *         {@link @termId} (including {@link termId}).
   */
  public static Set<TermId> childrenOf(TermId termId, ImmutableOntology<?, ?> ontology) {
    Set<TermId> result = new HashSet<>();

    // Setup BFS for building resulting set of TermId objects.
    BreadthFirstSearch<TermId, ImmutableEdge<TermId>> bfs = new BreadthFirstSearch<>();
    // TODO: Is there a more elegant solution to this problem?
    @SuppressWarnings("unchecked")
    DirectedGraph<TermId, ImmutableEdge<TermId>> graph =
        (DirectedGraph<TermId, ImmutableEdge<TermId>>) ontology.getGraph();

    // Perform BFS.
    bfs.startFromReverse(graph, termId, new VertexVisitor<TermId, ImmutableEdge<TermId>>() {
      @Override
      public boolean visit(DirectedGraph<TermId, ImmutableEdge<TermId>> g, TermId v) {
        result.add(v);
        return true;
      }
    });

    return result;
  }

}
