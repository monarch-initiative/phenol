package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ComparisonChain;

// TODO: obtain evidence code from ontology file?

/**
 * Annotation of a HPO term (via its {@link TermId}) with genes.
 *
 * <p>The <b>label</b> of this {@link TermAnnotation} is <code>"ENTREZ:${entrez_id}"</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoGeneAnnotation implements TermAnnotation {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** Numeric Entrez gene Id from annotation file. */
  private final int entrezGeneId;

  /** Entrez gene symbol from annotation file. */
  private final String entrezGeneSymbol;

  /** HPO term name from annotation file. */
  private final String hpoTermName;

  /** HPO {@link TermId} annotated with gene. */
  private final TermId hpoTermId;

  /**
   * Constructor.
   *
   * @param entrezGeneId Numeric Entrez gene Id.
   * @param entrezGeneSymbol Entrez gene symbol.
   * @param hpoTermName HPO term name from annotation file.
   * @param hpoTermId HPO {@link TermId} of annotated HPO term.
   */
  public HpoGeneAnnotation(
      int entrezGeneId, String entrezGeneSymbol, String hpoTermName, TermId hpoTermId) {
    this.entrezGeneId = entrezGeneId;
    this.entrezGeneSymbol = entrezGeneSymbol;
    this.hpoTermName = hpoTermName;
    this.hpoTermId = hpoTermId;
  }

  /** @return The numberic Entrez gene Id. */
  public int getEntrezGeneId() {
    return entrezGeneId;
  }

  /** @return The Entrez gene name. */
  public String getEntrezGeneSymbol() {
    return entrezGeneSymbol;
  }

  /** @return The HPO term name from the annotation. */
  public String getTermName() {
    return hpoTermName;
  }

  /** @return The annotated HPO term's {@link TermId}. */
  @Override
  public TermId getTermId() {
    return hpoTermId;
  }

  /** @return The term's Id as string including prefix. */
  @Override
  public String getLabel() {
    return "ENTREZ:" + entrezGeneId;
  }

  @Override
  public String toString() {
    return "HPOGeneAnnotation [entrezGeneId="
        + entrezGeneId
        + ", entrezGeneSymbol="
        + entrezGeneSymbol
        + ", termName="
        + hpoTermName
        + ", termId="
        + hpoTermId
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + entrezGeneId;
    result = prime * result + ((entrezGeneSymbol == null) ? 0 : entrezGeneSymbol.hashCode());
    result = prime * result + ((hpoTermId == null) ? 0 : hpoTermId.hashCode());
    result = prime * result + ((hpoTermName == null) ? 0 : hpoTermName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    HpoGeneAnnotation other = (HpoGeneAnnotation) obj;
    if (entrezGeneId != other.entrezGeneId) {
      return false;
    }
    if (entrezGeneSymbol == null) {
      if (other.entrezGeneSymbol != null) {
        return false;
      }
    } else if (!entrezGeneSymbol.equals(other.entrezGeneSymbol)) {
      return false;
    }
    if (hpoTermId == null) {
      if (other.hpoTermId != null) {
        return false;
      }
    } else if (!hpoTermId.equals(other.hpoTermId)) {
      return false;
    }
    if (hpoTermName == null) {
      if (other.hpoTermName != null) {
        return false;
      }
    } else if (!hpoTermName.equals(other.hpoTermName)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof HpoGeneAnnotation)) {
      throw new RuntimeException("Cannot compare " + o + " to a HpoGeneAnnotation");
    }

    final HpoGeneAnnotation that = (HpoGeneAnnotation) o;
    return ComparisonChain.start()
        .compare(this.entrezGeneId, that.entrezGeneId)
        .compare(this.entrezGeneSymbol, that.entrezGeneSymbol)
        .compare(this.hpoTermName, that.hpoTermName)
        .compare(this.hpoTermId, that.hpoTermId)
        .result();
  }
}
