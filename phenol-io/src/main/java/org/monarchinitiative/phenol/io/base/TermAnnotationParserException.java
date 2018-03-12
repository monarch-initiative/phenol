package org.monarchinitiative.phenol.io.base;

/**
 * Thrown in case of problems with parsing term annotation files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TermAnnotationParserException extends Exception {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param message Message for exception.
   * @param cause Causing underlying exception.
   */
  public TermAnnotationParserException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Message for exception.
   */
  public TermAnnotationParserException(String message) {
    super(message);
  }
}
