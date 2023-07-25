package org.monarchinitiative.phenol.io;

import java.util.Objects;

/**
 * A record-like class with options for parameterizing the ontology loader process.
 *
 * @see OntologyLoader
 * @see MinimalOntologyLoader
 */
public class OntologyLoaderOptions {

  private static final OntologyLoaderOptions DEFAULT_OPTIONS = new OntologyLoaderOptions(false);

  /**
   * @return options that keep non-propagating relationships
   * in the {@link org.monarchinitiative.phenol.ontology.data.Ontology} graph.
   */
  public static OntologyLoaderOptions defaultOptions() {
    return DEFAULT_OPTIONS;
  }

  public static OntologyLoaderOptions of(boolean discardNonPropagatingRelationships) {
    return new OntologyLoaderOptions(discardNonPropagatingRelationships);
  }

  private final boolean discardNonPropagatingRelationships;

  private OntologyLoaderOptions(boolean discardNonPropagatingRelationships) {
    this.discardNonPropagatingRelationships = discardNonPropagatingRelationships;
  }

  public boolean discardNonPropagatingRelationships() {
    return discardNonPropagatingRelationships;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OntologyLoaderOptions that = (OntologyLoaderOptions) o;
    return discardNonPropagatingRelationships == that.discardNonPropagatingRelationships;
  }

  @Override
  public int hashCode() {
    return Objects.hash(discardNonPropagatingRelationships);
  }

  @Override
  public String toString() {
    return "OntologyLoaderOptions{" +
      "discardNonPropagatingRelationships=" + discardNonPropagatingRelationships +
      '}';
  }
}
