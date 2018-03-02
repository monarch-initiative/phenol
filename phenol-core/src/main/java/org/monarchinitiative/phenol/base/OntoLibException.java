package org.monarchinitiative.phenol.base;

/**
 * Base class for handled exceptions thrown by OntoLib.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OntoLibException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param message Message of exception.
   * @param cause Causing {@link Throwable}.
   */
  public OntoLibException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Message of exception.
   */
  public OntoLibException(String message) {
    super(message);
  }

}
