package org.monarchinitiative.phenol.ontology.data;

import java.util.Optional;

/**
 * Implementors have an optional version.
 */
public interface Versioned {

  /**
   * @return {@link Optional} version {@link String} or an empty {@link Optional} if the version is not available.
   */
  Optional<String> version();

}
