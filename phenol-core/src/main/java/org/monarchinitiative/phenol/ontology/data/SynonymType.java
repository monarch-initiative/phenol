package org.monarchinitiative.phenol.ontology.data;

/** An enumeration of the possible synonym types (note: most synonyms in the current HPO do
 * not have a specific type; we encode these as NONE.
 */
public enum SynonymType {



    NONE,ABBREVIATION, LAYPERSON_TERM, OBSOLETE_SYNONYM, PLURAL_FORM, UK_SPELLING, IUPAC_NAME, INN, BRAND_NAME, IN_PART,
    SYNONYM, BLAST_NAME, GENBANK_COMMON_NAME, COMMON_NAME;


  public static SynonymType fromString(String s) {
    if (s == null || s.isEmpty()) {
      return SynonymType.NONE;
    }
    if (s.startsWith("http://purl.obolibrary.org/obo/") && s.length() > 31) {
      s = s.substring(31);
    }
    if (s.toLowerCase().startsWith("hp")) {
      int c = s.indexOf("#");
      if (c > 0) {
        s = s.substring(c + 1);
      }
      switch (s) {
        case "layperson":
        case "layperson term":
          return SynonymType.LAYPERSON_TERM;
        case "HP_0045077":
        case "abbreviation":
          return SynonymType.ABBREVIATION;
        case "HP_0045076":
        case "UK spelling":
          return SynonymType.UK_SPELLING;
        case "HP_0031859":
        case "obsolete synonym":
          return SynonymType.OBSOLETE_SYNONYM;
        case "HP_0045078":
        case "plural form":
          return SynonymType.PLURAL_FORM;
      }
    }
    switch (s) {
      case "ecto#IUPAC_NAME":
        return SynonymType.IUPAC_NAME;
      case "ecto#INN":
        return SynonymType.INN;
      case "ecto#BRAND_NAME":
        return SynonymType.BRAND_NAME;
      case "ecto#in_part":
        return SynonymType.IN_PART;
      case "ecto#synonym":
        return SynonymType.SYNONYM;
      case "ecto#blast_name":
        return SynonymType.BLAST_NAME;
      case "ecto#genbank_common_name":
        return SynonymType.GENBANK_COMMON_NAME;
      case "ecto#common_name":
        return SynonymType.COMMON_NAME;
        // return NONE if we did not recognize anything up to here.
      //default:
      //  System.err.println("[ERROR] Did not recognize synonym type: \"" + s + "\"");
    }

    return SynonymType.NONE;
  }


}
