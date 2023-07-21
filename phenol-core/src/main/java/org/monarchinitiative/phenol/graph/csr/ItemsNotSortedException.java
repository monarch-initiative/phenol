package org.monarchinitiative.phenol.graph.csr;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

/**
 * {@linkplain ItemsNotSortedException} is thrown if the items of an array or a collection are not sorted.
 *
 */
public class ItemsNotSortedException extends PhenolRuntimeException {
  public ItemsNotSortedException() {
    super();
  }

  public ItemsNotSortedException(String message, Throwable cause) {
    super(message, cause);
  }

  public ItemsNotSortedException(String message) {
    super(message);
  }

  public ItemsNotSortedException(Throwable cause) {
    super(cause);
  }

  protected ItemsNotSortedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
