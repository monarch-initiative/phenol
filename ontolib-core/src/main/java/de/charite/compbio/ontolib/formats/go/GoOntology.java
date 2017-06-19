package de.charite.compbio.ontolib.formats.go;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.ontolib.formats.hpo.HpoTermRelation;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermId;

/**
 * Implementation of the GO as an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class GoOntology extends ImmutableOntology<GoTerm, GoTermRelation> {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param graph Graph with the ontology's topology.
   * @param rootTermId {@link TermId} of the root term.
   * @param termMap Mapping from {@link TermId} to HPO term.
   * @param relationMap Mapping from numeric edge identifier to {@link HpoTermRelation}.
   */
  public GoOntology(ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph, TermId rootTermId,
      ImmutableMap<TermId, GoTerm> termMap, ImmutableMap<Integer, GoTermRelation> relationMap) {
    super(graph, rootTermId, termMap, relationMap);
  }

  @Override
  public String toString() {
    return "GoOntology [getGraph()=" + getGraph() + ", getTermMap()=" + getTermMap()
        + ", getRelationMap()=" + getRelationMap() + ", getRootTermId()=" + getRootTermId()
        + ", getTermIds()=" + getTermIds() + ", getTerms()=" + getTerms() + ", countTerms()="
        + countTerms() + "]";
  }

}
