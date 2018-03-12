package org.monarchinitiative.phenol.ser;

import org.monarchinitiative.phenol.base.PhenolException;

/**
 * Thrown on problems with data serialization and deserialization.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class SerializationException extends PhenolException {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause Underlying {@link Throwable}.
   */
  public SerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public SerializationException(String message) {
    super(message);
  }
}
