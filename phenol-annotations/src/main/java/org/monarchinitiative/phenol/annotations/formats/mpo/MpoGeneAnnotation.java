package org.monarchinitiative.phenol.annotations.formats.mpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;


  /**
   * Annotation of a HPO term (via its {@link TermId}) with genes.
   *
   * <p>The <b>label</b> of this {@link TermAnnotation} is <code>"ENTREZ:${entrez_id}"</code>.
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
   */
  public final class MpoGeneAnnotation implements TermAnnotation {

    /** Serial UId for serialization. */
    private static final long serialVersionUID = 2L;

    /** Numeric Entrez gene Id from annotation file. */
    private final TermId geneId;

    /** Entrez gene symbol from annotation file. */
    private final String entrezGeneSymbol;

    /** HPO term name from annotation file. */
    private final String hpoTermName;

    /** HPO {@link TermId} annotated with gene. */
    private final TermId hpoTermId;

    /**
     * Constructor.
     *
     * @param gene Numeric Entrez gene Id.
     * @param entrezGeneSymbol Entrez gene symbol.
     * @param hpoTermName HPO term name from annotation file.
     * @param hpoTermId HPO {@link TermId} of annotated HPO term.
     */
    public MpoGeneAnnotation(
      TermId gene, String entrezGeneSymbol, String hpoTermName, TermId hpoTermId) {
      this.geneId = gene;
      this.entrezGeneSymbol = entrezGeneSymbol;
      this.hpoTermName = hpoTermName;
      this.hpoTermId = hpoTermId;
    }

    /** @return The numberic Entrez gene Id. */
    public TermId getGeneId() {
      return geneId;
    }

    /** @return The Entrez gene name. */
    public String getGeneSymbol() {
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
    public TermId getItemId() {
      return geneId;
    }

    @Override
    public String toString() {
      return "MPOGeneAnnotation [geneId="
        + geneId.getValue()
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
      result = prime * result + geneId.hashCode();
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
      MpoGeneAnnotation other = (MpoGeneAnnotation) obj;
      if (geneId != other.geneId) {
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

      final MpoGeneAnnotation that = (MpoGeneAnnotation) o;

      int result = geneId.compareTo(that.geneId);
      if (result != 0) return result;
      result = entrezGeneSymbol.compareTo(that.entrezGeneSymbol);
      if (result != 0) return result;
      result = hpoTermName.compareTo(that.hpoTermName);
      if (result != 0) return result;
      return hpoTermId.compareTo(that.hpoTermId);
    }
  }

