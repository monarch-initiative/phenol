package de.charite.compbio.ontolib.formats.mpo;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermId;

/**
 * Implementation of the MPO as an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class MpoOntology extends ImmutableOntology<MpoTerm, MpoTermRelation> {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param graph Graph with the ontology's topology.
   * @param rootTermId {@link TermId} of the root term.
   * @param termMap Mapping from {@link TermId} to MPO term.
   * @param relationMap Mapping from numeric edge identifier to {@link MpoTermRelation}.
   */
  public MpoOntology(ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph, TermId rootTermId,
      ImmutableMap<TermId, MpoTerm> termMap, ImmutableMap<Integer, MpoTermRelation> relationMap) {
    super(graph, rootTermId, termMap, relationMap);
  }

  @Override
  public String toString() {
    return "MPOntology [getGraph()=" + getGraph() + ", getTermMap()=" + getTermMap()
        + ", getRelationMap()=" + getRelationMap() + ", getRootTermId()=" + getRootTermId()
        + ", getTermIds()=" + getTermIds() + ", getTerms()=" + getTerms() + "]";
  }

}
