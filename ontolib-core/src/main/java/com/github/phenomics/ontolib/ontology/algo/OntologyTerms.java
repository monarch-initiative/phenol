package com.github.phenomics.ontolib.ontology.algo;

import java.util.HashSet;
import java.util.Set;

import com.github.phenomics.ontolib.graph.algo.BreadthFirstSearch;
import com.github.phenomics.ontolib.graph.algo.VertexVisitor;
import com.github.phenomics.ontolib.graph.data.DirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermVisitor;

// TODO: Maybe change the name to OntologyTermOperations or OntologyTermQueries?
// TODO: test me!
// TODO: implement BreadFirstSeach-like classes with instance methods for this to equalize with graph.algo

/**
 * Helper class with static methods for navigating Ontology terms.
 *
 * <h5>Implementation Note</h5>
 *
 * <p>
 * The methods simply reduce the ontology visiting to visiting vertices int he underlying graphs via
 * BFS.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class OntologyTerms {

  /**
   * Visit all children terms of {@code termId} in {@code ontology} using {@code termVisitor}.
   *
   * @param termId The root of the sub ontology DAG to query for.
   * @param ontology The {@link Ontology} to iterate in.
   *
   * @param <O> {@link Ontology} specialization to use.
   */
  public static <O extends Ontology<?, ?>> void visitChildrenOf(TermId termId, O ontology,
      TermVisitor<O> termVisitor) {
    // Setup BFS for visiting the termds.
    BreadthFirstSearch<TermId, ImmutableEdge<TermId>> bfs = new BreadthFirstSearch<>();
    // TODO: Is there a more elegant solution to this problem?
    @SuppressWarnings("unchecked")
    DirectedGraph<TermId, ImmutableEdge<TermId>> graph =
        (DirectedGraph<TermId, ImmutableEdge<TermId>>) ontology.getGraph();

    // Perform BFS.
    bfs.startFromReverse(graph, termId, new VertexVisitor<TermId, ImmutableEdge<TermId>>() {
      @Override
      public boolean visit(DirectedGraph<TermId, ImmutableEdge<TermId>> g, TermId v) {
        return termVisitor.visit(ontology, v);
      }
    });
  }

  /**
   * Return set of all children of {@code termId} in {@code ontology}.
   *
   * @param termId The root of the sub ontology DAG to query for.
   * @param ontology The {@link Ontology} to iterate in.
   * @return Newly created {@link Set} with {@link TermId}s of children of the term corresponding to
   *         {@link @termId} (including {@link termId}).
   *
   * @param <O> {@link Ontology} specialization to use.
   */
  public static <O extends Ontology<?, ?>> Set<TermId> childrenOf(TermId termId, O ontology) {
    Set<TermId> result = new HashSet<>();

    visitChildrenOf(termId, ontology, new TermVisitor<O>() {
      @Override
      public boolean visit(O ontology, TermId termId) {
        result.add(termId);
        return true;
      }
    });

    return result;
  }

  /**
   * Visit all parets terms of {@code termId} in {@code ontology} using {@code termVisitor}.
   *
   * @param termId The {@link TermId} of the term to visit the parents of.
   * @param ontology The {@link Ontology} to iterate in.
   *
   * @param <O> {@link Ontology} specialization to use.
   */
  public static <O extends Ontology<?, ?>> void visitParentsOf(TermId termId, O ontology,
      TermVisitor<O> termVisitor) {
    // Setup BFS for visiting the termds.
    BreadthFirstSearch<TermId, ImmutableEdge<TermId>> bfs = new BreadthFirstSearch<>();
    // TODO: Is there a more elegant solution to this problem?
    @SuppressWarnings("unchecked")
    DirectedGraph<TermId, ImmutableEdge<TermId>> graph =
        (DirectedGraph<TermId, ImmutableEdge<TermId>>) ontology.getGraph();

    // Perform BFS.
    bfs.startFromForward(graph, termId, new VertexVisitor<TermId, ImmutableEdge<TermId>>() {
      @Override
      public boolean visit(DirectedGraph<TermId, ImmutableEdge<TermId>> g, TermId v) {
        return termVisitor.visit(ontology, v);
      }
    });
  }

  /**
   * Return set of all praents of {@code termId} in {@code ontology}.
   *
   * @param termId The {@link TermId} of the term to visit the parents of.
   * @param ontology The {@link Ontology} to iterate in.
   * @return Newly created {@link Set} with {@link TermId}s of parents of the term corresponding to
   *         {@link @termId} (including {@link termId}).
   *
   * @param <O> {@link Ontology} specialization to use.
   */
  public static <O extends Ontology<?, ?>> Set<TermId> parentsOf(TermId termId, O ontology) {
    Set<TermId> result = new HashSet<>();

    visitParentsOf(termId, ontology, new TermVisitor<O>() {
      @Override
      public boolean visit(O ontology, TermId termId) {
        result.add(termId);
        return true;
      }
    });

    return result;
  }

}
