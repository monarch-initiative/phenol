package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.formats.Gene;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents an assertion that a gene or genes is(are) causally involved with a disease.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class Disease2GeneAssociation {



  /** The CURIE representing a disease, e.g., OMIM:600100. */
  private final TermId disease;


  private final List<Gene2Association> gene2assoc;

  public Disease2GeneAssociation(TermId id, List<Gene2Association> genes) {
    this.gene2assoc=genes;
    this.disease=id;

  }


  public TermId getDiseaseId() {
    return disease;
  }

  /** @return a list of all genes (regardless of association type). */
  public List<Gene> getGeneList() {
    return gene2assoc.stream().map(Gene2Association::getGene).collect(Collectors.toList());
  }

  public List<Gene2Association> getAssociations() {
    return gene2assoc;
  }

  @Override
  public String toString() {
    return String.format("%s: %s",
      disease.getIdWithPrefix(),
      gene2assoc.stream().
        map(Gene2Association::getGene).
        map(Gene::toString).
        collect(Collectors.joining(";"))
      );
  }

}
