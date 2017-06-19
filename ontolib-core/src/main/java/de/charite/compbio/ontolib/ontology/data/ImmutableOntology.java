package de.charite.compbio.ontolib.ontology.data;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of an immutable {@link Ontology}.
 *
 * @param <T> Type to use for terms.
 * @param <R> Type to use for term relations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableOntology<T extends Term, R extends TermRelation> implements Ontology<T, R> {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** The graph storing the ontology's structure. */
  private final ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph;

  /** Id of the root term. */
  private final TermId rootTermId;

  /** The mapping from TermId to Term. */
  private final ImmutableMap<TermId, T> termMap;

  /** The mapping from edge Id to TermRelation. */
  private final ImmutableMap<Integer, R> relationMap;

  /**
   * Constructor.
   *
   * @param graph Graph to use for underlying structure.
   * @param rootTermId Root node's {@link TermId}.
   * @param termMap Mapping from {@link TermId} to <code>T</code>.
   * @param relationMap Mapping from numeric edge Id to <code>R</code>.
   */
  public ImmutableOntology(ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph,
      TermId rootTermId, ImmutableMap<TermId, T> termMap, ImmutableMap<Integer, R> relationMap) {
    this.graph = graph;
    this.rootTermId = rootTermId;
    this.termMap = termMap;
    this.relationMap = relationMap;
  }

  @Override
  public DirectedGraph<TermId, ? extends Edge<TermId>> getGraph() {
    return graph;
  }

  @Override
  public Map<TermId, T> getTermMap() {
    return termMap;
  }

  @Override
  public Map<Integer, R> getRelationMap() {
    return relationMap;
  }

  @Override
  public boolean isRootTerm(TermId termId) {
    return termId.equals(rootTermId);
  }

  @Override
  public Collection<TermId> getAncestors(TermId termId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<TermId> getAllAncestorTermIds(Collection<TermId> termIds, boolean includeRoot) {
    final Set<TermId> result = new HashSet<>();
    for (TermId termId : termIds) {
      result.add(termId);
      for (TermId ancestorId : getAncestors(termId)) {
        if (includeRoot || !isRootTerm(ancestorId)) {
          result.add(ancestorId);
        }
      }
    }
    return result;
  }

  @Override
  public TermId getRootTermId() {
    return rootTermId;
  }

  @Override
  public Collection<TermId> getTermIds() {
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
