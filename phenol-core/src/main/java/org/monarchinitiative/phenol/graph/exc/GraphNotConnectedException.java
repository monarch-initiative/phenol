package org.monarchinitiative.phenol.graph.exc;

/**
 * {@linkplain GraphNotConnectedException} is thrown if the vertices and edges represent a disconnected graph -
 * a graph consisting of >1 connected components.
 *
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class GraphNotConnectedException extends GraphConstructionException {

  /**
   * Construct with message.
   *
   * @param message Message for the exception.
   */
  public GraphNotConnectedException(String message) {
    super(message);
  }

}
