package org.monarchinitiative.phenol.ontology.data;

/** An enumeration of the possible synonym types (note: most synonyms in the current HPO do
 * not have a specific type; we encode these as NONE.
 */
public enum SynonymType {



    NONE,ABBREVIATION, LAYPERSON_TERM, OBSOLETE_SYNONYM, PLURAL_FORM, UK_SPELLING, IUPAC_NAME, INN, BRAND_NAME, IN_PART,
    SYNONYM, BLAST_NAME, GENBANK_COMMON_NAME, COMMON_NAME;



    public static SynonymType fromString(String s) {
      if (s != null && ! s.isEmpty()) {
        switch (s) {
          case "abbreviation":
          case "http://purl.obolibrary.org/obo/HP_0045077":
            return SynonymType.ABBREVIATION;
          case "layperson term":
          case "http://purl.obolibrary.org/obo/hp#layperson":
            return SynonymType.LAYPERSON_TERM;
          case "http://purl.obolibrary.org/obo/HP_0031859":
          case "obsolete synonym":
            return SynonymType.OBSOLETE_SYNONYM;
          case "plural form":
          case "http://purl.obolibrary.org/obo/HP_0045078":
            return SynonymType.PLURAL_FORM;
          case "http://purl.obolibrary.org/obo/HP_0045076":
          case "UK spelling":
            return SynonymType.UK_SPELLING;
          case "http://purl.obolibrary.org/obo/ecto#IUPAC_NAME":
            return SynonymType.IUPAC_NAME;
          case "http://purl.obolibrary.org/obo/ecto#INN":
            return SynonymType.INN;
          case "http://purl.obolibrary.org/obo/ecto#BRAND_NAME":
            return SynonymType.BRAND_NAME;
          case "http://purl.obolibrary.org/obo/ecto#in_part":
            return SynonymType.IN_PART;
          case "http://purl.obolibrary.org/obo/ecto#synonym":
            return SynonymType.SYNONYM;
          case "http://purl.obolibrary.org/obo/ecto#blast_name":
            return SynonymType.BLAST_NAME;
          case "http://purl.obolibrary.org/obo/ecto#genbank_common_name":
            return SynonymType.GENBANK_COMMON_NAME;
          case "http://purl.obolibrary.org/obo/ecto#common_name":
            return SynonymType.COMMON_NAME;
          default:
            System.err.println("[ERROR] Did not recognize synonym type: \"" + s + "\"");
        }
      }
      return SynonymType.NONE;
    }


}
