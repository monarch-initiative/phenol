package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Set;

/**
 * Instances of this class represent items (genes, represented by TermId objects) that are annotated to
 * the same GO/HPO/etc Term. The GO/HPO/etc Term itself is not represented in this object.
 *
 * @author Sebastian Bauer
 * @author Peter Robinson (refactor)
 */
public class DirectAndIndirectTermAnnotations {
  /**
   * List of directly annotated genes
   */
  private final Set<TermId> directAnnotated;

  /**
   * List of genes annotated in total (direct or via annotation propagation)
   */
  private final Set<TermId> totalAnnotated;

  /**
   * Constructs set of direct annotations (which is equivalent to the annotations
   * in ontologyIdSet), and the total (direct and indirect) annotations using
   * {@link OntologyAlgorithm#getAncestorTerms(Ontology, Set, boolean)}.
   * In addition to the direct annotation, the gene is also indirectly
   * annotated to all of the GO Term's ancestors -- whiach are calculated here.
   * @param ontologyIdSet set of directly annotation terms
   * @param ontology reference to relevant {@link Ontology} object
   */
  public DirectAndIndirectTermAnnotations(Set<TermId> ontologyIdSet, Ontology ontology) {
    directAnnotated = ontologyIdSet;
    totalAnnotated = OntologyAlgorithm.getAncestorTerms(ontology,
      ontologyIdSet,
      true);
  }

  public int directAnnotatedCount() {
    return directAnnotated.size();
  }

  public int totalAnnotatedCount() {
    return totalAnnotated.size();
  }

  public Set<TermId> getDirectAnnotated() {
    return directAnnotated;
  }

  /** @return Set of all genes directly or indirectly annotated to a GO/HPO term. */
  public Set<TermId> getTotalAnnotated() {
    return totalAnnotated;
  }
}

