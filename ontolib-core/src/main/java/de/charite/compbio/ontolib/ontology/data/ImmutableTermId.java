package de.charite.compbio.ontolib.ontology.data;

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

  /** Term identifier behind the prefix. */
  private final String id;

  /**
   * Construct from term Id including prefix.
   *
   * @param termIdString String with term Id to construct with.
   * @return Resulting {@link ImmutableTermId}.
   */
  public static ImmutableTermId constructWithPrefix(String termIdString) {
    final String[] arr = termIdString.split(":", 2);
    if (arr.length != 2) {
      throw new RuntimeException("Term Id string \"" + termIdString + "\"does not have a prefix!");
    } else {
      return new ImmutableTermId(new ImmutableTermPrefix(arr[0]), arr[1]);
    }
  }

  /**
   * Constructor.
   *
   * @param prefix Prefix to use.
   * @param id Term identifier after the prefix.
   */
  public ImmutableTermId(TermPrefix prefix, String id) {
    this.prefix = prefix;
    this.id = id;
  }

  @Override
  public int compareTo(TermId o) {
    if (this == o) {
      return 0;
    }
    final int tmp = prefix.compareTo(o.getPrefix());
    if (tmp == 0) {
      return id.compareTo(o.getId());
    } else {
      return tmp;
    }
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
    stringBuilder.append(prefix);
    stringBuilder.append(":");
    stringBuilder.append(id);
    return stringBuilder.toString();
  }

  @Override
  public String toString() {
    return "ImmutableTermId [prefix=" + prefix + ", id=" + id + "]";
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
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
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ImmutableTermId other = (ImmutableTermId) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
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
