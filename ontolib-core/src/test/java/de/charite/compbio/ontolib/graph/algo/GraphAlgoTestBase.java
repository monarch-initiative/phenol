package de.charite.compbio.ontolib.graph.algo;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import org.junit.Before;

public class GraphAlgoTestBase {

  protected DirectedGraph<Integer, ImmutableEdge<Integer>> simpleDag;
  protected DirectedGraph<Integer, ImmutableEdge<Integer>> simpleLine;

  @Before
  public void setUp() {
    simpleDag = ImmutableDirectedGraph.construct(
        ImmutableList.of(ImmutableEdge.construct(1, 2, 1), ImmutableEdge.construct(1, 3, 2),
            ImmutableEdge.construct(2, 4, 3), ImmutableEdge.construct(3, 4, 4)));
    simpleLine = ImmutableDirectedGraph.construct(
        ImmutableList.of(ImmutableEdge.construct(1, 2, 1), ImmutableEdge.construct(2, 3, 2),
            ImmutableEdge.construct(3, 4, 3), ImmutableEdge.construct(4, 5, 4)));
  }

}
