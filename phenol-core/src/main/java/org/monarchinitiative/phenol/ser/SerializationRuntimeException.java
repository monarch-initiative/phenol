package org.monarchinitiative.phenol.ser;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

/**
 * Unchecked exception thrown on problems with data serialization and deserialization.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class SerializationRuntimeException extends PhenolRuntimeException {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param message Exception message.
   * @param cause Underlying {@link Throwable}.
   */
  public SerializationRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor.
   *
   * @param message Exception message.
   */
  public SerializationRuntimeException(String message) {
    super(message);
  }
}
