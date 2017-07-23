package com.github.phenomics.ontolib.graph.algo;

import com.github.phenomics.ontolib.graph.data.DirectedGraph;

/**
 * Raised when {@link DirectedGraph} is not a DAG.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GraphNotDagException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Defalt constructor.
   */
  public GraphNotDagException() {
    super();
  }

  /**
   * Construct graph with the given <code>message</code>.
   *
   * @param message for the exception
   */
  public GraphNotDagException(String message) {
    super(message);
  }

}
