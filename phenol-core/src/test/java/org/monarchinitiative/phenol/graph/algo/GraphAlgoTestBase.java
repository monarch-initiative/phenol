package org.monarchinitiative.phenol.graph.algo;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.GraphUtility;
import org.junit.Before;

public class GraphAlgoTestBase {

  protected DefaultDirectedGraph<Integer, IdLabeledEdge> simpleDag;
  protected DefaultDirectedGraph<Integer, IdLabeledEdge> simpleLine;

  @Before
  public void setUp() {
	simpleDag = new DefaultDirectedGraph<Integer, IdLabeledEdge>(IdLabeledEdge.class);
	GraphUtility.addEdgeToGraph(simpleDag, 1, 2, 1);  
	GraphUtility.addEdgeToGraph(simpleDag, 1, 3, 2);
	GraphUtility.addEdgeToGraph(simpleDag, 2, 4, 3);
	GraphUtility.addEdgeToGraph(simpleDag, 3, 4, 4);
	
	simpleLine = new DefaultDirectedGraph<Integer, IdLabeledEdge>(IdLabeledEdge.class);
	GraphUtility.addEdgeToGraph(simpleLine, 1, 2, 1);  
	GraphUtility.addEdgeToGraph(simpleLine, 2, 3, 2);
	GraphUtility.addEdgeToGraph(simpleLine, 3, 4, 3);
	GraphUtility.addEdgeToGraph(simpleLine, 4, 5, 4);	
  }

}
