package org.monarchinitiative.phenol.graph.algo;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.junit.Before;

/**
 * Base/prerequisite codes for testing graph algorithms.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GraphAlgoTestBase {

  protected DefaultDirectedGraph<Integer, IdLabeledEdge> simpleDag;
  protected DefaultDirectedGraph<Integer, IdLabeledEdge> simpleLine;

  @Before
  public void setUp() {
    simpleDag = new DefaultDirectedGraph<>(IdLabeledEdge.class);
    GraphUtil.addEdgeToGraph(simpleDag, 1, 2, 1);
    GraphUtil.addEdgeToGraph(simpleDag, 1, 3, 2);
    GraphUtil.addEdgeToGraph(simpleDag, 2, 4, 3);
    GraphUtil.addEdgeToGraph(simpleDag, 3, 4, 4);

    simpleLine = new DefaultDirectedGraph<>(IdLabeledEdge.class);
    GraphUtil.addEdgeToGraph(simpleLine, 1, 2, 1);
    GraphUtil.addEdgeToGraph(simpleLine, 2, 3, 2);
    GraphUtil.addEdgeToGraph(simpleLine, 3, 4, 3);
    GraphUtil.addEdgeToGraph(simpleLine, 4, 5, 4);
  }
}
