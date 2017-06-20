package de.charite.compbio.ontolib.ontology.data;

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

  /** Numeric identifier behind the prefix. */
  private final int id;

  /**
   * Construct from term ID including prefix.
   *
   * @param termIdString String with term Id to construct with.
   * @return Resulting {@link ImmutableTermId}.
   */
  public static ImmutableTermId constructWithPrefix(String termIdString) {
    final String[] arr = termIdString.split(":", 2);
    if (arr.length != 2) {
      throw new RuntimeException("Term ID string \"" + termIdString + "\" does not have a prefix!");
    } else {
      return new ImmutableTermId(new ImmutableTermPrefix(arr[0]), Integer.parseInt(arr[1]));
    }
  }

  /**
   * Constructor.
   *
   * @param prefix Prefix to use.
   * @param id Numeric term identifier after the prefix.
   */
  public ImmutableTermId(TermPrefix prefix, int id) {
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
  public int getId() {
    return id;
  }

  @Override
  public String getIdWithPrefix() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(prefix);
    stringBuilder.append(":");
    stringBuilder.append(String.format("%07d", id));
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
    result = prime * result + prefix.hashCode();
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    // Manual short-cuts for the important special cases.
    if (this == obj) {
      return true;
    } else if (obj instanceof TermId) {
      final TermId that = (TermId) obj;
      return this.prefix.equals(that.getPrefix()) && (this.id == that.getId());
    }

    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ImmutableTermId other = (ImmutableTermId) obj;
    if (id != other.id) {
      return false;
    }
    if (prefix == null) {
      if (other.prefix != null) {
        return false;
      }
    } else if (!prefix.equals(other.prefix)) {
      return false;
    }
    return true;
  }

}
