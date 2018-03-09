package org.monarchinitiative.phenol.graph.exc;

/**
 * Exception raised when the graph is not simple, i.e., there exist duplicate edges.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GraphNotSimpleException extends GraphConstructionException {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct with message.
   *
   * @param message Message for the exception.
   */
  public GraphNotSimpleException(final String message) {
    super(message);
  }
}
