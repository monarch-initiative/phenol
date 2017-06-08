package de.charite.compbio.ontolib.graph.algo;

/**
 * Raised when {@link DirectedGraph} is not a DAG.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GraphNotDAGException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Defalt constructor.
   */
  public GraphNotDAGException() {
    super();
  }

  /**
   * Construct graph with the given <code>message</code>
   * 
   * @param message for the exception
   */
  public GraphNotDAGException(String message) {
    super(message);
  }

}
