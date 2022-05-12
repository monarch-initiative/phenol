package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

public class MissingPhenolResourceException extends PhenolRuntimeException {

  public MissingPhenolResourceException() {
    super();
  }

  public MissingPhenolResourceException(String message) {
    super(message);
  }

  public MissingPhenolResourceException(Throwable cause) {
    super(cause);
  }

  public MissingPhenolResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  protected MissingPhenolResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
