package org.monarchinitiative.phenol.ontology.data;

import java.util.List;

/**
 * Immutable implementation.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TermSynonym {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 2L;

  /** The synonym value. */
  private final String value;

  /** The synonym scope. */
  private final TermSynonymScope scope;

  /** Optional synonym type name, <code>null</code> if missing. */
  private final String synonymTypeName;

  /** List of term xRefs, <code>null</code> if missing. */
  private final List<TermXref> termXrefs;

  /**
   * Constructor.
   *
   * @param value Synonym value.
   * @param scope Synonym scope.
   * @param synonymTypeName Optional synonym type name, <code>null</code> if missing.
   * @param termXrefs Optional dbxref list, <code>null</code> if missing.
   */
  public TermSynonym(
      String value, TermSynonymScope scope, String synonymTypeName, List<TermXref> termXrefs) {
    this.value = value;
    this.scope = scope;
    this.synonymTypeName = synonymTypeName;
    this.termXrefs = termXrefs;
  }

  public String getValue() {
    return value;
  }

  public TermSynonymScope getScope() {
    return scope;
  }

  public String getSynonymTypeName() {
    return synonymTypeName;
  }
  
  public List<TermXref> getTermXrefs() {
    return termXrefs;
  }

  @Override
  public String toString() {
    return "TermSynonym [value="
        + value
        + ", scope="
        + scope
        + ", synonymTypeName="
        + synonymTypeName
        + ", termXrefs="
        + termXrefs
        + "]";
  }
}
