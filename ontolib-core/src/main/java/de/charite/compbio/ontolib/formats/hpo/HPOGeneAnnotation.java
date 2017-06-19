package de.charite.compbio.ontolib.formats.hpo;

import com.google.common.collect.ComparisonChain;
import de.charite.compbio.ontolib.ontology.data.TermAnnotation;
import de.charite.compbio.ontolib.ontology.data.TermID;

/**
 * Annotation of a HPO term (via its {@link TermID}) with genes.
 *
 * <p>
 * The <b>label</b> of this {@link TermAnnotation} is <code>"ENTREZ:${entrez_id}"</code>.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HPOGeneAnnotation implements TermAnnotation {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Numeric Entrez gene ID from annotation file. */
  private final int entrezGeneID;

  /** Entrez gene symbol from annotation file. */
  private final String entrezGeneSymbol;

  /** HPO term name from annotation file. */
  private final String hpoTermName;

  /** HPO {@link TermID} annotated with gene. */
  private final TermID hpoTermID;

  /**
   * Constructor.
   * 
   * @param entrezGeneID Numeric Entrez gene ID.
   * @param entrezGeneSymbol Entrez gene symbol.
   * @param hpoTermName HPO term name from annotation file.
   * @param hpoTermID HPO {@link TermID} of annotated HPO term.
   */
  public HPOGeneAnnotation(int entrezGeneID, String entrezGeneSymbol, String hpoTermName,
      TermID hpoTermID) {
    this.entrezGeneID = entrezGeneID;
    this.entrezGeneSymbol = entrezGeneSymbol;
    this.hpoTermName = hpoTermName;
    this.hpoTermID = hpoTermID;
  }

  /**
   * @return The numberic Entrez gene ID.
   */
  public int getEntrezGeneID() {
    return entrezGeneID;
  }

  /**
   * @return The Entrez gene name.
   */
  public String getEntrezGeneSymbol() {
    return entrezGeneSymbol;
  }

  /**
   * @return The HPO term name from the annotation.
   */
  public String getTermName() {
    return hpoTermName;
  }

  /**
   * @return The annotated HPO term's {@link TermID}.
   */
  @Override
  public TermID getTermID() {
    return hpoTermID;
  }

  /**
   * @return The term's ID as string including prefix.
   */
  @Override
  public String getLabel() {
    return "ENTREZ:" + entrezGeneID;
  }

  @Override
  public String toString() {
    return "HPOGeneAnnotation [entrezGeneID=" + entrezGeneID + ", entrezGeneSymbol="
        + entrezGeneSymbol + ", termName=" + hpoTermName + ", termID=" + hpoTermID + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + entrezGeneID;
    result = prime * result + ((entrezGeneSymbol == null) ? 0 : entrezGeneSymbol.hashCode());
    result = prime * result + ((hpoTermID == null) ? 0 : hpoTermID.hashCode());
    result = prime * result + ((hpoTermName == null) ? 0 : hpoTermName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    HPOGeneAnnotation other = (HPOGeneAnnotation) obj;
    if (entrezGeneID != other.entrezGeneID) return false;
    if (entrezGeneSymbol == null) {
      if (other.entrezGeneSymbol != null) return false;
    } else if (!entrezGeneSymbol.equals(other.entrezGeneSymbol)) return false;
    if (hpoTermID == null) {
      if (other.hpoTermID != null) return false;
    } else if (!hpoTermID.equals(other.hpoTermID)) return false;
    if (hpoTermName == null) {
      if (other.hpoTermName != null) return false;
    } else if (!hpoTermName.equals(other.hpoTermName)) return false;
    return true;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof HPOGeneAnnotation)) {
      throw new RuntimeException("Cannot compare " + o + " to a HPOGeneAnnotation");
    }

    final HPOGeneAnnotation that = (HPOGeneAnnotation) o;
    return ComparisonChain.start().compare(this.entrezGeneID, that.entrezGeneID)
        .compare(this.entrezGeneSymbol, that.entrezGeneSymbol).compare(this.hpoTermName, that.hpoTermName)
        .compare(this.hpoTermID, that.hpoTermID).result();
  }

}
