package de.charite.compbio.ontolib.ontology.data;

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
  private final List<TermXref> termXRefs;

  /**
   * Constructor.
   *
   * @param value Synonym value.
   * @param scope Synonym scope.
   * @param synonymTypeName Optional synonym type name, <code>null</code> if missing.
   * @param termXRefs Optional dbxref list, <code>null</code> if missing.
   */
  public ImmutableTermSynonym(String value, TermSynonymScope scope, String synonymTypeName,
      List<TermXref> termXRefs) {
    this.value = value;
    this.scope = scope;
    this.synonymTypeName = synonymTypeName;
    this.termXRefs = termXRefs;
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
  public List<TermXref> getTermXRefs() {
    return termXRefs;
  }

  @Override
  public String toString() {
    return "ImmutableTermSynonym [value=" + value + ", scope=" + scope + ", synonymTypeName="
        + synonymTypeName + ", termXRefs=" + termXRefs + "]";
  }

}
