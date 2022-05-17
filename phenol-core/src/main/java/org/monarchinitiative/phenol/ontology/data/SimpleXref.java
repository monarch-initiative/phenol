package org.monarchinitiative.phenol.ontology.data;

import java.util.Objects;

/**
 * Class to represent the database_cross_reference such as "PMID:102212" or "HPO:skoehler" that
 * is used to represent the provenance of the Term definitions in the HPO.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class SimpleXref {

  enum Prefix {
    HPO("HPO"), MGI("MGI"), PMID("PMID"), ISBN("ISBN"),
    MONDO("MONDO"), NCIT("NCIT"), ORCID("ORCID"), MESH("MESH"),
    DOID("DOID"), ORPHANET("Orphanet"), EFO("EFO"), UNKNOWN("?");
    private final String name;

    Prefix(String n) {
      this.name = n;
    }

    @Override
    public String toString() {
      return name;
    }
  }


  private final Prefix prefix;

  private final String id;

  public SimpleXref(String xref) {
    int i = xref.indexOf(':');
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
        case "MONDO":
          prefix=Prefix.MONDO;
          break;
        case "NCIT":
          prefix=Prefix.NCIT;
          break;
        case "ORCID":
          prefix =Prefix.ORCID;
          break;
        case "MESH":
          prefix = Prefix.MESH;
          break;
        case "DOID":
          prefix=Prefix.DOID;
          break;
        case "ORPHANET":
          prefix=Prefix.ORPHANET;
          break;
        case "EFO":
          prefix=Prefix.EFO;
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
    return this.prefix.name+":"+id;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SimpleXref that = (SimpleXref) o;
    return prefix == that.prefix && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prefix, id);
  }

  @Override
  public String toString() {
   return getCurie();
  }
}
