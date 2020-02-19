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

  /** AN enumeration of the possible synonym types (note: most synonyms in the current HPO do
   * not have a specific type; we encode these as NONE.
   */
  enum SynonymType {
    NONE,ABBREVIATION, LAYPERSON_TERM, OBSOLETE_SYNONYM, PLURAL_FORM, UK_SPELLING
  }
  /** The kind of synonym (default, and by far the most common, is no specific synonym type --NONE). */
  SynonymType synonymType = SynonymType.NONE;

  /** Synonym type, e.g., layperson */

  /**
   * Constructor.
   *
   * @param value Synonym value.
   * @param scope Synonym scope.
   * @param synonymTypeName Optional synonym type name, <code>null</code> if missing.
   * @param termXrefs Optional dbxref list, <code>null</code> if missing.
   */
  public TermSynonym(String value, TermSynonymScope scope, String synonymTypeName, List<TermXref> termXrefs, String synType) {
    this.value = value;
    this.scope = scope;
    this.synonymTypeName = synonymTypeName;
    this.termXrefs = termXrefs;
    if (synType != null && ! synType.isEmpty()) {
      switch(synType) {
        case "abbreviation":
          this.synonymType = SynonymType.ABBREVIATION;
          break;
        case "layperson term":
          this.synonymType = SynonymType.LAYPERSON_TERM;
          break;
        case "obsolete synonym":
          this.synonymType = SynonymType.OBSOLETE_SYNONYM;
          break;
        case "plural form":
          this.synonymType = SynonymType.PLURAL_FORM;
          break;
        case "UK spelling":
          this.synonymType = SynonymType.UK_SPELLING;
          break;
        default:
          System.err.println("[ERROR] Did not recognize synonym type: " + synType);
      }
    }
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
    String synString = synonymTypeName;
    if (! synonymType.equals(SynonymType.NONE)) {
      switch (synonymType) {
        case PLURAL_FORM:
          synString += " [plural form]";
          break;
        case UK_SPELLING:
          synString += " [UK spelling]";
          break;
        case  OBSOLETE_SYNONYM:
          synString += " [obsolete synonym]";
          break;
        case LAYPERSON_TERM:
          synString += " [layperson term]";
          break;
        case ABBREVIATION:
          synString += " [abbreviation]";
          break;
      }
    }

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
