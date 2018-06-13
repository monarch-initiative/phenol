package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.formats.Gene;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents an assertion that a gene or genes is(are) causally involved with a disease.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class Gene2Phenotype {
  public enum AssociationType {MENDELIAN, DIGENIC, POLYGENIC}

  private final AssociationType association;
  /** The CURIE representing a disease, e.g., OMIM:600100. */
  private final TermId disease;

  private final List<Gene> geneList;

  public Gene2Phenotype(TermId id, List<Gene> genes, AssociationType assoc) {
    this.association=assoc;
    this.disease=id;
    this.geneList=genes;
  }



  @Override
  public String toString() {
    return String.format("%s: %s [%s]",
      disease.getIdWithPrefix(),
      geneList.stream().map(Gene::toString).collect(Collectors.joining(";")),
      association
      );
  }

}
