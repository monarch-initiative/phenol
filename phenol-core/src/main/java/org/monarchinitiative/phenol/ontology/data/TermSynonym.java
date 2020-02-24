package org.monarchinitiative.phenol.ontology.data;

import javax.swing.plaf.synth.SynthOptionPaneUI;
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
    NONE,ABBREVIATION, LAYPERSON_TERM, OBSOLETE_SYNONYM, PLURAL_FORM, UK_SPELLING, IUPAC_NAME, INN, BRAND_NAME, IN_PART,
    SYNONYM, BLAST_NAME, GENBANK_COMMON_NAME, COMMON_NAME
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
        case "http://purl.obolibrary.org/obo/HP_0045077":
          this.synonymType = SynonymType.ABBREVIATION;
          break;
        case "layperson term":
        case "http://purl.obolibrary.org/obo/hp#layperson":
          this.synonymType = SynonymType.LAYPERSON_TERM;
          break;
        case "http://purl.obolibrary.org/obo/HP_0031859":
        case "obsolete synonym":
          this.synonymType = SynonymType.OBSOLETE_SYNONYM;
          break;
        case "plural form":
        case "http://purl.obolibrary.org/obo/HP_0045078":
          this.synonymType = SynonymType.PLURAL_FORM;
          break;
        case "http://purl.obolibrary.org/obo/HP_0045076":
        case "UK spelling":
          this.synonymType = SynonymType.UK_SPELLING;
          break;
        case "http://purl.obolibrary.org/obo/ecto#IUPAC_NAME":
          this.synonymType = SynonymType.IUPAC_NAME;
          break;
        case "http://purl.obolibrary.org/obo/ecto#INN":
          this.synonymType = SynonymType.INN;
          break;
        case "http://purl.obolibrary.org/obo/ecto#BRAND_NAME":
          this.synonymType = SynonymType.BRAND_NAME;
          break;
        case "http://purl.obolibrary.org/obo/ecto#in_part":
          this.synonymType = SynonymType.IN_PART;
          break;
        case "http://purl.obolibrary.org/obo/ecto#synonym":
          this.synonymType = SynonymType.SYNONYM;
          break;
        case "http://purl.obolibrary.org/obo/ecto#blast_name":
          this.synonymType = SynonymType.BLAST_NAME;
          break;
        case "http://purl.obolibrary.org/obo/ecto#genbank_common_name":
          this.synonymType = SynonymType.GENBANK_COMMON_NAME;
          break;
        case "http://purl.obolibrary.org/obo/ecto#common_name":
          this.synonymType = SynonymType.COMMON_NAME;
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
