package com.github.phenomics.ontolib.ontology.data;

import com.github.phenomics.ontolib.base.OntoLibRuntimeException;
import com.google.common.collect.ComparisonChain;

/**
 * Implementation of an immutable {@link TermId}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableTermId implements TermId {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** Prefix of the TermId. */
  private final TermPrefix prefix;

  /** Identifier behind the prefix. */
  private final String id;

  /**
   * Construct from term ID including prefix.
   *
   * @param termIdString String with term Id to construct with.
   * @return Resulting {@link ImmutableTermId}.
   * @throws OntoLibRuntimeException if the string does not have a prefix
   */
  public static ImmutableTermId constructWithPrefix(String termIdString) {
    final int pos = termIdString.lastIndexOf(':');
    if (pos == -1) {
      throw new OntoLibRuntimeException(
          "Term ID string \"" + termIdString + "\" does not have a prefix!");
    } else {
      return new ImmutableTermId(new ImmutableTermPrefix(termIdString.substring(0, pos)),
          termIdString.substring(pos + 1));
    }
  }

  /**
   * Constructor.
   *
   * @param prefix Prefix to use.
   * @param id Identifier after the prefix.
   */
  public ImmutableTermId(TermPrefix prefix, String id) {
    this.prefix = prefix;
    this.id = id;
  }

  @Override
  public int compareTo(TermId that) {
    return ComparisonChain.start().compare(this.getPrefix(), that.getPrefix())
        .compare(this.getId(), that.getId()).result();
  }

  @Override
  public TermPrefix getPrefix() {
    return prefix;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getIdWithPrefix() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(prefix.getValue());
    stringBuilder.append(":");
    stringBuilder.append(id);
    return stringBuilder.toString();
  }

  @Override
  public String toString() {
    return "ImmutableTermId [prefix=" + prefix + ", id=" + id + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    // Manual short-cuts for the important special cases.
    if (this == obj) {
      return true;
    } else if (obj instanceof TermId) {
      final TermId that = (TermId) obj;
      return this.prefix.equals(that.getPrefix()) && (this.id.equals(that.getId()));
    }

    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ImmutableTermId other = (ImmutableTermId) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (prefix == null) {
      if (other.prefix != null) return false;
    } else if (!prefix.equals(other.prefix)) return false;
    return true;
  }

}
