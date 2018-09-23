package org.monarchinitiative.phenol.ontology.data;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

/**
 * Immutable  TermId.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public final class TermId implements Comparable<TermId>, Serializable {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 2L;

  /** Prefix of the TermIdI. */
  private final TermPrefix prefix;

  /** Identifier behind the prefix. */
  private final String id;

  /**
   * Construct from term ID including prefix.
   *
   * @param termIdString String with term Id to construct with.
   * @return Resulting {@link TermId}.
   * @throws PhenolRuntimeException if the string does not have a prefix
   */
  public static TermId constructWithPrefix(String termIdString) {
    final int pos = termIdString.indexOf(':');
    if (pos == -1) {
      throw new PhenolRuntimeException(
          "TermId construction error: \"" + termIdString + "\" does not have a prefix!");
    } else {
      return new TermId(
          new TermPrefix(termIdString.substring(0, pos)), termIdString.substring(pos + 1));
    }
  }


  /**
   * Construct from term ID including prefix. This function has a checked exception which makes it easier for
   * chains of errors to be propagated.
   *
   * @param termIdString String with term Id to construct with.
   * @return Resulting {@link TermId}.
   * @throws PhenolException if the string does not have a prefix
   */
  public static TermId constructWithPrefixInternal(String termIdString) throws PhenolException {
    final int pos = termIdString.indexOf(':');
    if (pos == -1) {
      throw new PhenolException(
        "TermId construction error: \"" + termIdString + "\" does not have a prefix!");
    } else {
      return new TermId(
        new TermPrefix(termIdString.substring(0, pos)), termIdString.substring(pos + 1));
    }
  }

  /**
   * Constructor.
   *
   * @param prefix Prefix to use.
   * @param id Identifier after the prefix.
   */
  public TermId(TermPrefix prefix, String id) {
    this.prefix = prefix;
    this.id = id;
  }

  @Override
  public int compareTo(TermId that) {
    return ComparisonChain.start()
        .compare(this.getPrefix(), that.getPrefix())
        .compare(this.getId(), that.getId())
        .result();
  }

  public TermPrefix getPrefix() {
    return prefix;
  }

  public String getId() {
    return id;
  }

  public String getIdWithPrefix() {
    return prefix.getValue() + ":" + id;
  }

  @Override
  public String toString() {
    return "TermId [prefix=" + prefix + ", id=" + id + "]";
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
    } else {
      return false;
    }
  }
}
