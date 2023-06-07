package org.monarchinitiative.phenol.ontology.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The interface implemented by entities that have an identifier.
 */
public interface Identified {

  @JsonGetter
  TermId id();

  /**
   * @deprecated the getter will be removed in <code>v3.0.0</code>, use {@link #id()} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC2")
  @JsonIgnore
  default TermId getId() {
    return id();
  }
}
