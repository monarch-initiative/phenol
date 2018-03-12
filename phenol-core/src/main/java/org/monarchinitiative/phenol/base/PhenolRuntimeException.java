package org.monarchinitiative.phenol.base;

/**
 * Base class for unhandled exceptions thrown by OntoLib.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PhenolRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param message Message of exception.
   * @param cause Causing {@link Throwable}.
   */
  public PhenolRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Message of exception.
   */
  public PhenolRuntimeException(String message) {
    super(message);
  }
}
