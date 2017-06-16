package de.charite.compbio.ontolib.formats.hpo;

import com.google.common.collect.ComparisonChain;
import de.charite.compbio.ontolib.ontology.data.TermAnnotation;
import de.charite.compbio.ontolib.ontology.data.TermID;

/**
 * Annotation of a HPO term (via its {@link TermID}) with genes.
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
  private final String termName;

  /** HPO {@link TermID} annotated with gene. */
  private final TermID termID;

  /**
   * Constructor.
   * 
   * @param entrezGeneID Numeric Entrez gene ID.
   * @param entrezGeneSymbol Entrez gene symbol.
   * @param termName HPO term name from annotation file.
   * @param termID HPO {@link TermID} of annotated HPO term.
   */
  public HPOGeneAnnotation(int entrezGeneID, String entrezGeneSymbol, String termName,
      TermID hpoTermID) {
    this.entrezGeneID = entrezGeneID;
    this.entrezGeneSymbol = entrezGeneSymbol;
    this.termName = termName;
    this.termID = hpoTermID;
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
    return termName;
  }

  /**
   * @return The annotated HPO term's {@link TermID}.
   */
  @Override
  public TermID getTermID() {
    return termID;
  }

  /**
   * @return The term's ID as string including prefix.
   */
  @Override
  public String getLabel() {
    return termID.getIDWithPrefix();
  }

  @Override
  public String toString() {
    return "HPOGeneAnnotation [entrezGeneID=" + entrezGeneID + ", entrezGeneSymbol="
        + entrezGeneSymbol + ", termName=" + termName + ", termID=" + termID + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + entrezGeneID;
    result = prime * result + ((entrezGeneSymbol == null) ? 0 : entrezGeneSymbol.hashCode());
    result = prime * result + ((termID == null) ? 0 : termID.hashCode());
    result = prime * result + ((termName == null) ? 0 : termName.hashCode());
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
    if (termID == null) {
      if (other.termID != null) return false;
    } else if (!termID.equals(other.termID)) return false;
    if (termName == null) {
      if (other.termName != null) return false;
    } else if (!termName.equals(other.termName)) return false;
    return true;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof HPOGeneAnnotation)) {
      throw new RuntimeException("Cannot compare " + o + " to a HPOGeneAnnotation");
    }

    final HPOGeneAnnotation that = (HPOGeneAnnotation) o;
    return ComparisonChain.start().compare(this.entrezGeneID, that.entrezGeneID)
        .compare(this.entrezGeneSymbol, that.entrezGeneSymbol).compare(this.termName, that.termName)
        .compare(this.termID, that.termID).result();
  }

}
