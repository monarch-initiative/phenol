package org.monarchinitiative.phenol.ontology.data;

import java.util.List;

/**
 * Immutable implementation of {@link TermSynonym}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableTermSynonym implements TermSynonym {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

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
  public ImmutableTermSynonym(
      String value, TermSynonymScope scope, String synonymTypeName, List<TermXref> termXrefs) {
    this.value = value;
    this.scope = scope;
    this.synonymTypeName = synonymTypeName;
    this.termXrefs = termXrefs;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public TermSynonymScope getScope() {
    return scope;
  }

  @Override
  public String getSynonymTypeName() {
    return synonymTypeName;
  }

  @Override
  public List<TermXref> getTermXrefs() {
    return termXrefs;
  }

  @Override
  public String toString() {
    return "ImmutableTermSynonym [value="
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
