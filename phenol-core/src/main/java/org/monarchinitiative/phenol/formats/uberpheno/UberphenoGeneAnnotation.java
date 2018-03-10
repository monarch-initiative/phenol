package org.monarchinitiative.phenol.formats.uberpheno;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ComparisonChain;

import java.util.Optional;

/**
 * Record from Uberpheno gene annotation file.
 *
 * <p>The <b>label</b> of this {@link TermAnnotation} is the <code>"ENTREZ:${entrezGeneId}"</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class UberphenoGeneAnnotation implements TermAnnotation {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Entrez gene ID. */
  private final int entrezGeneId;

  /** Gene symbol. */
  private final String entrezGeneSymbol;

  /** Uberpheno term description. */
  private final String termDescription;

  /** Uberpheno term ID. */
  private final TermId termId;

  /** Evidence description. */
  private final String evidenceDescription;

  public UberphenoGeneAnnotation(
      int entrezGeneId,
      String entrezGeneSymbol,
      String termDescription,
      TermId uberphenoTermId,
      String evidenceDescription) {
    this.entrezGeneId = entrezGeneId;
    this.entrezGeneSymbol = entrezGeneSymbol;
    this.termDescription = termDescription;
    this.termId = uberphenoTermId;
    this.evidenceDescription = evidenceDescription;
  }

  /** @return Entrez gene ID. */
  public int getEntrezGeneId() {
    return entrezGeneId;
  }

  /** @return Gene symbol. */
  public String getGeneSymbol() {
    return entrezGeneSymbol;
  }

  /** @return Description of referred-to Uberpheno term. */
  public String getTermDescription() {
    return termDescription;
  }

  /** @return The annotated HPO term's {@link TermId}. */
  @Override
  public TermId getTermId() {
    return termId;
  }

  /** @return Entrez gene ID as "ENTREZ:${entrezGeneId}". */
  @Override
  public String getLabel() {
    return "ENTREZ:" + entrezGeneId;
  }

  /** @return The evidence description. */
  public String getEvidenceDescription() {
    return evidenceDescription;
  }

  @Override
  public Optional<String> getEvidenceCode() {
    return Optional.ofNullable(evidenceDescription);
  }

  @Override
  public String toString() {
    return "UberphenoGeneAnnotation [entrezGeneId="
        + entrezGeneId
        + ", entrezGeneSymbol="
        + entrezGeneSymbol
        + ", termDescription="
        + termDescription
        + ", uberphenoTermId="
        + termId
        + ", evidenceDescription="
        + evidenceDescription
        + "]";
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof UberphenoGeneAnnotation)) {
      throw new RuntimeException("Cannot compare " + o + " to a UberphenoGeneAnnotation");
    }

    final UberphenoGeneAnnotation that = (UberphenoGeneAnnotation) o;
    return ComparisonChain.start()
        .compare(this.entrezGeneId, that.entrezGeneId)
        .compare(this.termId, that.termId)
        .result();
  }
}
