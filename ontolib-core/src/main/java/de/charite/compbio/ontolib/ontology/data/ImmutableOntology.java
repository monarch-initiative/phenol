package de.charite.compbio.ontolib.ontology.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;

/**
 * Implementation of an immutable {@link Ontology}.
 *
 * @param <T> Type to use for terms.
 * @param <R> Type to use for term relations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableOntology<T extends Term, R extends TermRelation> implements Ontology<T, R> {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** The graph storing the ontology's structure. */
  private final ImmutableDirectedGraph<TermID, ImmutableEdge<TermID>> graph;

  /** ID of the root term. */
  private final TermID rootTermID;

  /** The mapping from TermID to Term. */
  private final ImmutableMap<TermID, T> termMap;

  /** The mapping from edge ID to TermRelation. */
  private final ImmutableMap<Integer, R> relationMap;

  /**
   * Constructor.
   *
   * @param graph Graph to use for underlying structure.
   * @param rootTermID Root node's {@link TermID}.
   * @param termMap Mapping from {@link TermID} to <code>T</code>.
   * @param relationMap Mapping from numeric edge ID to <code>R</code>.
   */
  public ImmutableOntology(ImmutableDirectedGraph<TermID, ImmutableEdge<TermID>> graph,
      TermID rootTermID, ImmutableMap<TermID, T> termMap, ImmutableMap<Integer, R> relationMap) {
    this.graph = graph;
    this.rootTermID = rootTermID;
    this.termMap = termMap;
    this.relationMap = relationMap;
  }

  @Override
  public DirectedGraph<TermID, ? extends Edge<TermID>> getGraph() {
    return graph;
  }

  @Override
  public Map<TermID, T> getTermMap() {
    return termMap;
  }

  @Override
  public Map<Integer, R> getRelationMap() {
    return relationMap;
  }

  @Override
  public boolean isRootTerm(TermID tID) {
    return tID.equals(rootTermID);
  }

  @Override
  public Collection<TermID> getAncestors(TermID tID) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<TermID> getAllAncestorTermIDs(Collection<TermID> termIDs, boolean includeRoot) {
    final Set<TermID> result = new HashSet<>();
    for (TermID termID : termIDs) {
      result.add(termID);
      for (TermID ancestorID : getAncestors(termID)) {
        if (includeRoot || !isRootTerm(ancestorID)) {
          result.add(ancestorID);
        }
      }
    }
    return result;
  }

  @Override
  public TermID getRootTermID() {
    return rootTermID;
  }

  @Override
  public Collection<TermID> getTermIDs() {
    return termMap.keySet();
  }

  @Override
  public Collection<T> getTerms() {
    return termMap.values();
  }

  @Override
  public int countTerms() {
    return termMap.size();
  }

}
