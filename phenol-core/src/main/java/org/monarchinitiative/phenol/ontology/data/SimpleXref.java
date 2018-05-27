package org.monarchinitiative.phenol.ontology.data;

/**
 * Class to represent the database_cross_reference such as "PMID:102212" or "HPO:skoehler" that
 * is used to represent the provenance of the Term definitions in the HPO.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class SimpleXref {

  enum Prefix {HPO, MGI, PMID,ISBN, UNKNOWN}


  private final Prefix prefix;

  private final String id;

  public SimpleXref(String xref) {
    int i = xref.indexOf(":");
    // if there is no semicolon then i<0
    // if the semicolon is at the first or last position, then the xref is also invalid
    if (i<1 || i==xref.length()-1) {
      prefix=Prefix.UNKNOWN;
      id=xref;
    } else  {
      String pre = xref.substring(0,i);
      id=xref.substring(i+1);
      switch (pre.toUpperCase()) {
        case "HPO":
          prefix=Prefix.HPO;
          break;
        case "PMID":
          prefix=Prefix.PMID;
          break;
        case "MGI":
          prefix=Prefix.MGI;
          break;
        case "ISBN":
          prefix=Prefix.ISBN;
          break;
        default:
          prefix=Prefix.UNKNOWN;
      }
    }
  }

  public boolean isPmid() {
    return prefix.equals(Prefix.PMID);
  }

  public boolean isIsbn() {
    return prefix.equals(Prefix.ISBN);
  }

  public boolean isHpo() {
    return prefix.equals(Prefix.HPO);
  }

  public boolean isMgi() {
    return prefix.equals(Prefix.MGI);
  }

  public boolean isValid() {
    return (! prefix.equals(Prefix.UNKNOWN));
  }

  public String getCurie() {
    switch (prefix) {
      case HPO: return "HPO:"+id;
      case PMID: return "PMID:"+id;
      case ISBN: return "ISBN:"+id;
      case MGI: return "MGI:"+id;
      case UNKNOWN:
      default:
        return "?:"+id;
    }
  }

  /**
   * Return the prefix of the Xref, e.g., "PMID:9778510" would return PMID
   * @return prefix of the Xref
   */
  public String getPrefix() {
    return prefix.toString();
  }

  /**
   * Return the id of the Xref, e.g., "PMID:9778510" would return 9778510
   * @return id of the Xref
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
   return getCurie();
  }
}
