package org.monarchinitiative.phenol.graph.csr;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

/**
 * {@linkplain InsufficientWidthException} is thrown if the edges imply presence of more edge types than
 * a {@link DataIndexer} can support.
 *
 * @see DataIndexer
 */
public class InsufficientWidthException extends PhenolRuntimeException {
  public InsufficientWidthException() {
    super();
  }

  public InsufficientWidthException(String message, Throwable cause) {
    super(message, cause);
  }

  public InsufficientWidthException(String message) {
    super(message);
  }

  public InsufficientWidthException(Throwable cause) {
    super(cause);
  }

  protected InsufficientWidthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
