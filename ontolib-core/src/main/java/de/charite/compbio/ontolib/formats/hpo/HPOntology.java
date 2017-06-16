package de.charite.compbio.ontolib.formats.hpo;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermID;

/**
 * Implementation of the HPO as an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HPOntology extends ImmutableOntology<HPOTerm, HPOTermRelation> {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param graph Graph with the ontology's topology.
   * @param rootTermID {@link TermID} of the root term.
   * @param termMap Mapping from {@link TermID} to HPO term.
   * @param relationMap Mapping from numeric edge identifier to {@link HPOTermRelation}.
   */
  public HPOntology(ImmutableDirectedGraph<TermID, ImmutableEdge<TermID>> graph, TermID rootTermID,
      ImmutableMap<TermID, HPOTerm> termMap, ImmutableMap<Integer, HPOTermRelation> relationMap) {
    super(graph, rootTermID, termMap, relationMap);
  }

  @Override
  public String toString() {
    return "HPOntology [getGraph()=" + getGraph() + ", getTermMap()=" + getTermMap()
        + ", getRelationMap()=" + getRelationMap() + ", getRootTermID()=" + getRootTermID()
        + ", getTermIDs()=" + getTermIDs() + ", getTerms()=" + getTerms() + "]";
  }

}
