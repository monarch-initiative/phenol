package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

/**
 * Instances of this class represent items (genes, represented by TermId objects) that are annotated to
 * the same GO/HPO/etc Term. The GO/HPO/etc Term itself is not represented in this object.
 *
 * @author Sebastian Bauer
 * @author Peter Robinson (refactor)
 */
public class DirectAndIndirectTermAnnotations {
  /** Identifier of the ontology term id that annotates the items in {@link #directAnnotatedDomainItemSet}
   * and {@link #totalAnnotatedDomainItemSet}, for example, a Gene Ontology term id.
   */
  private final TermId ontologyId;
  /**
   * List of directly annotated genes (or in general domain items)
   */
  private final Set<TermId> directAnnotatedDomainItemSet;

  /**
   * List of genes (or in general domain items) annotated in total (direct or via annotation propagation)
   */
  private final Set<TermId> totalAnnotatedDomainItemSet;

  /**
   * Constructs set of direct annotations (which is equivalent to the annotations
   * in ontologyIdSet), and the total (direct and indirect) annotations using
   * {@link OntologyAlgorithm#getAncestorTerms(Ontology, Set, boolean)}.
   * In addition to the direct annotation, the gene is also indirectly
   * annotated to all of the GO Term's ancestors -- whiach are calculated here.
   */
  public DirectAndIndirectTermAnnotations(TermId ontologyTermId) {
    this.ontologyId = ontologyTermId;
    directAnnotatedDomainItemSet = new HashSet<>();
    totalAnnotatedDomainItemSet = new HashSet<>();
  }

  public void addDirectAnnotatedItem(TermId domainItemId) {
    this.directAnnotatedDomainItemSet.add(domainItemId);
    this.totalAnnotatedDomainItemSet.add(domainItemId);
  }

  public TermId getOntologyId() {
    return ontologyId;
  }

  public void addIndirectAnnotatedItem(TermId domainItemId) {
    this.totalAnnotatedDomainItemSet.add(domainItemId);
  }

  public int directAnnotatedCount() {
    return directAnnotatedDomainItemSet.size();
  }

  public int totalAnnotatedCount() {
    return totalAnnotatedDomainItemSet.size();
  }

  public Set<TermId> getDirectAnnotatedDomainItemSet() {
    return directAnnotatedDomainItemSet;
  }

  /** @return Set of all genes directly or indirectly annotated to a GO/HPO term. */
  public Set<TermId> getTotalAnnotatedDomainItemSet() {
    return totalAnnotatedDomainItemSet;
  }
}

