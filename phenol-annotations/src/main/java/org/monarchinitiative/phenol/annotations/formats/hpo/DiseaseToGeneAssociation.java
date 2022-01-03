package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class represents an assertion that a gene or genes is(are) causally involved with a disease.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class DiseaseToGeneAssociation {

  /**
   * The CURIE representing a disease, e.g., OMIM:600100.
   */
  private final TermId diseaseId;
  private final List<GeneToAssociation> gene2assoc;

  public static DiseaseToGeneAssociation of(TermId diseaseId, List<GeneToAssociation> geneToAssociations) {
    return new DiseaseToGeneAssociation(diseaseId, geneToAssociations);
  }

  private DiseaseToGeneAssociation(TermId diseaseId, List<GeneToAssociation> geneAssociations) {
    this.diseaseId = Objects.requireNonNull(diseaseId, "Disease id must not be null");
    this.gene2assoc = Objects.requireNonNull(geneAssociations, "Gene associations must not be null");
  }

  public TermId diseaseId() {
    return diseaseId;
  }

  public List<GeneToAssociation> associations() {
    return gene2assoc;
  }

  /**
   * @return a list of all genes (regardless of the association type).
   */
  public List<GeneIdentifier> getGeneList() {
    return gene2assoc.stream()
      .map(GeneToAssociation::geneIdentifier)
      .collect(Collectors.toUnmodifiableList());
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiseaseToGeneAssociation that = (DiseaseToGeneAssociation) o;
    return Objects.equals(diseaseId, that.diseaseId) && Objects.equals(gene2assoc, that.gene2assoc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diseaseId, gene2assoc);
  }

  @Override
  public String toString() {
    return "DiseaseToGeneAssociation{" +
      "disease=" + diseaseId +
      ", gene2assoc=" + gene2assoc +
      '}';
  }
}
