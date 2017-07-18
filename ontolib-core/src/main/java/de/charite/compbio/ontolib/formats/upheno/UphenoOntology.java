package de.charite.compbio.ontolib.formats.upheno;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermId;

/**
 * Implementation of uPheno as an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class UphenoOntology extends ImmutableOntology<UphenoTerm, UphenoTermRelation> {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param metaInfo {@link ImmutableSortedFMap} with meta information.
   * @param graph Graph with the ontology's topology.
   * @param rootTermId {@link TermId} of the root term.
   * @param nonObsoleteTermIds {@link Collection} of {@link TermId}s of non-obsolete terms.
   * @param obsoleteTermIds {@link Collection} of {@link TermId}s of obsolete terms.
   * @param termMap Mapping from {@link TermId} to Uberpheno term.
   * @param relationMap Mapping from numeric edge identifier to {@link UphenoTermRelation}.
   */
  public UphenoOntology(ImmutableSortedMap<String, String> metaInfo,
      ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph, TermId rootTermId,
      Collection<TermId> nonObsoleteTermIds, Collection<TermId> obsoleteTermIds,
      ImmutableMap<TermId, UphenoTerm> termMap,
      ImmutableMap<Integer, UphenoTermRelation> relationMap) {
    super(metaInfo, graph, rootTermId, nonObsoleteTermIds, obsoleteTermIds, termMap, relationMap);
  }

  @Override
  public String toString() {
    return "UphenoOntology [getMetaInfo()=" + getMetaInfo() + ", getGraph()=" + getGraph()
        + ", getTermMap()=" + getTermMap() + ", getRelationMap()=" + getRelationMap()
        + ", getRootTermId()=" + getRootTermId() + ", getAllTermIds()=" + getAllTermIds()
        + ", getTerms()=" + getTerms() + ", getNonObsoleteTermIds()=" + getNonObsoleteTermIds()
        + ", getObsoleteTermIds()=" + getObsoleteTermIds() + "]";
  }

}
