package org.monarchinitiative.phenol.graph;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

/**
 * An exception thrown when the {@link OntologyGraph} is queried with a node that does not exist in the graph.
 */
public class NodeNotPresentInGraphException extends PhenolRuntimeException {

  public NodeNotPresentInGraphException() {
    super();
  }

  public NodeNotPresentInGraphException(String message) {
    super(message);
  }

  public NodeNotPresentInGraphException(String message, Throwable cause) {
    super(message, cause);
  }

  public NodeNotPresentInGraphException(Throwable cause) {
    super(cause);
  }

  protected NodeNotPresentInGraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
