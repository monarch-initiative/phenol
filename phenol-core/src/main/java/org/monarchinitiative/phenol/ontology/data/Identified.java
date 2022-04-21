package org.monarchinitiative.phenol.ontology.data;

/**
 * The interface implemented by entities that have an identifier.
 */
public interface Identified {

  TermId id();

  /**
   * @deprecated the getter will be removed in <code>v3.0.0</code>, use {@link #id()} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC2")
  default TermId getId() {
    return id();
  }
}
