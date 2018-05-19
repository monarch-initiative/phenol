package org.monarchinitiative.phenol.formats.hpo;

import java.util.Collection;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Implementation of the HPO as an {@link ImmutableOntology}.
 *
 * <p>Similarity computations should be performed with the "phenotypic abnormality" sub ontology
 * that can be retrieved through {@link #getPhenotypicAbnormalitySubOntology()}.
 *
 * @see HpoFrequency
 * @see HpoFrequencyTermIds
 * @see HpoModeOfInheritanceTermIds
 * @see HpoSubOntologyRootTermIds
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoOntology extends ImmutableOntology {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** "Phenotypic abnormality" sub ontology. */
  private final ImmutableOntology phenotypicAbnormalitySubOntology;

  /**
   * Constructor.
   *
   * @param metaInfo {@link ImmutableSortedMap} with meta information.
   * @param graph Graph with the ontology's topology.
   * @param rootTermId {@link TermId} of the root term.
   * @param nonObsoleteTermIds {@link Collection} of {@link TermId}s of non-obsolete terms.
   * @param obsoleteTermIds {@link Collection} of {@link TermId}s of obsolete terms.
   * @param termMap Mapping from {@link TermId} to HPO term.
   * @param relationMap Mapping from numeric edge identifier to {@link Relationship}.
   */
  public HpoOntology(
      ImmutableSortedMap<String, String> metaInfo,
      DefaultDirectedGraph<TermId, IdLabeledEdge> graph,
      TermId rootTermId,
      Collection<TermId> nonObsoleteTermIds,
      Collection<TermId> obsoleteTermIds,
      ImmutableMap<TermId, Term> termMap,
      ImmutableMap<Integer, Relationship> relationMap) {
    super(metaInfo, graph, rootTermId, nonObsoleteTermIds, obsoleteTermIds, termMap, relationMap);
    // Construct "phenotypic abnormality sub ontology" on construction.
    this.phenotypicAbnormalitySubOntology =
        (ImmutableOntology)
            super.subOntology(HpoSubOntologyRootTermIds.PHENOTYPIC_ABNORMALITY);
  }

  /** @return "Phenotypic abnormality" sub ontology. */
  public ImmutableOntology getPhenotypicAbnormalitySubOntology() {
    return phenotypicAbnormalitySubOntology;
  }

  @Override
  public String toString() {
    return "HpoOntology [getMetaInfo()="
        + getMetaInfo()
        + ", getGraph()="
        + getGraph()
        + ", getTermMap()="
        + getTermMap()
        + ", getRelationMap()="
        + getRelationMap()
        + ", getRootTermId()="
        + getRootTermId()
        + ", getAllTermIds()="
        + getAllTermIds()
        + ", getTerms()="
        + getTerms()
        + ", getNonObsoleteTermIds()="
        + getNonObsoleteTermIds()
        + ", getObsoleteTermIds()="
        + getObsoleteTermIds()
        + "]";
  }
}
