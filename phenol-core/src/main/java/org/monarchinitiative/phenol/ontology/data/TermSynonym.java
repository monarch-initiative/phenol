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

  /** The kind of synonym (default, and by far the most common, is no specific synonym type --NONE). */
  SynonymType synonymType = SynonymType.NONE;



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
    this.synonymType = SynonymType.fromString(synType);
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


  public boolean hasSynonymType() {
    return (! this.synonymType.equals(SynonymType.NONE));
  }

  public boolean isLayperson() {
    return this.synonymType.equals(SynonymType.LAYPERSON_TERM);
  }

  public boolean isAbbreviation() {
    return this.synonymType.equals(SynonymType.ABBREVIATION);
  }

  public boolean isUKspelling() {
    return this.synonymType.equals(SynonymType.UK_SPELLING);
  }

  public boolean isPluralForm() {
    return this.synonymType.equals(SynonymType.PLURAL_FORM);
  }

  public boolean isObsoleteSynonym() {
    return this.synonymType.equals(SynonymType.OBSOLETE_SYNONYM);
  }




}
