package org.monarchinitiative.phenol.io;

import java.util.Objects;

/**
 * A record-like class with options for parameterizing the ontology loader process.
 *
 * @see OntologyLoader
 * @see MinimalOntologyLoader
 */
public class OntologyLoaderOptions {

  private static final OntologyLoaderOptions DEFAULT_OPTIONS = new Builder().build();

  /**
   * @return options that keep non-propagating relationships
   * in the {@link org.monarchinitiative.phenol.ontology.data.Ontology} graph.
   */
  public static OntologyLoaderOptions defaultOptions() {
    return DEFAULT_OPTIONS;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * @deprecated use {@link #builder()} instead. The method will be removed in <em>3.0.0</em>.
   */
  // REMOVE(3.0.0)
  @Deprecated(forRemoval = true, since = "2.0.3")
  public static OntologyLoaderOptions of(boolean discardNonPropagatingRelationships) {
    return new Builder().discardNonPropagatingRelationships(discardNonPropagatingRelationships).build();
  }

  private final boolean discardNonPropagatingRelationships;
  private final boolean discardDuplicatedRelationships;

  private OntologyLoaderOptions(Builder builder) {
    this.discardNonPropagatingRelationships = builder.discardNonPropagatingRelationships;
    this.discardDuplicatedRelationships = builder.discardDuplicatedRelationships;
  }

  public boolean discardNonPropagatingRelationships() {
    return discardNonPropagatingRelationships;
  }

  public boolean discardDuplicatedRelationships() {
    return discardDuplicatedRelationships;
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

  /**
   * A builder for {@linkplain OntologyLoaderOptions}.
   */
  public static class Builder {
    private boolean discardNonPropagatingRelationships = false;
    private boolean discardDuplicatedRelationships = false;

    private Builder(){}

    public Builder discardNonPropagatingRelationships(boolean value) {
      this.discardNonPropagatingRelationships = value;
      return this;
    }

    public Builder discardDuplicatedRelationships(boolean value) {
      this.discardDuplicatedRelationships = value;
      return this;
    }

    public OntologyLoaderOptions build() {
      return new OntologyLoaderOptions(this);
    }
  }
}
