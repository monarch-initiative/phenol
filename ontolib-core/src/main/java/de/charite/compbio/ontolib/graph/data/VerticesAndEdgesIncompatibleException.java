package de.charite.compbio.ontolib.graph.data;

/**
 * Exception raised when list of vertices and edges are incompatible on graph construction.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VerticesAndEdgesIncompatibleException extends RuntimeException {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct with message.
   *
   * @param message Message for the exception.
   */
  public VerticesAndEdgesIncompatibleException(final String message) {
    super(message);
  }

}
