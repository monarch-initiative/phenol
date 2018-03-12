package org.monarchinitiative.phenol.graph.exc;

/**
 * Exception raised when there are problems with graph components such as incompatibility between
 * vertex and edge list or the graph is not simple.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GraphConstructionException extends RuntimeException {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct with message.
   *
   * @param message Message for the exception.
   */
  public GraphConstructionException(final String message) {
    super(message);
  }
}
