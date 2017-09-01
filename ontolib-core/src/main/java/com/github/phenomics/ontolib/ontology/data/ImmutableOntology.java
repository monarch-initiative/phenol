package com.github.phenomics.ontolib.ontology.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.phenomics.ontolib.graph.algo.BreadthFirstSearch;
import com.github.phenomics.ontolib.graph.algo.VertexVisitor;
import com.github.phenomics.ontolib.graph.data.DirectedGraph;
import com.github.phenomics.ontolib.graph.data.Edge;
import com.github.phenomics.ontolib.graph.data.ImmutableDirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.ontology.algo.OntologyTerms;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Sets;

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

  /** Meta information, as loaded from file. */
  private final ImmutableSortedMap<String, String> metaInfo;

  /** The graph storing the ontology's structure. */
  private final ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph;

  /** Id of the root term. */
  private final TermId rootTermId;

  /** The mapping from TermId to Term for all terms. */
  private final ImmutableMap<TermId, T> termMap;

  /**
   * Set of non-obselete term ids, separate so maps can remain for sub ontology construction.
   */
  private final ImmutableSet<TermId> nonObsoleteTermIds;

  /**
   * Set of obselete term ids, separate so maps can remain for sub ontology construction.
   */
  private final ImmutableSet<TermId> obsoleteTermIds;

  /** Set of all term IDs. */
  private final ImmutableSet<TermId> allTermIds;

  /** The mapping from edge Id to TermRelation. */
  private final ImmutableMap<Integer, R> relationMap;

  /** Precomputed ancestors (including vertex itself). */
  private final ImmutableMap<TermId, ImmutableSet<TermId>> precomputedAncestors;

  /**
   * Constructor.
   *
   * @param metaInfo {@link ImmutableMap} with meta information.
   * @param graph Graph to use for underlying structure.
   * @param rootTermId Root node's {@link TermId}.
   * @param nonObsoleteTermIds {@link Collection} of {@link TermId}s of non-obsolete terms.
   * @param obsoleteTermIds {@link Collection} of {@link TermId}s of obsolete terms.
   * @param termMap Mapping from {@link TermId} to <code>T</code>.
   * @param relationMap Mapping from numeric edge Id to <code>R</code>.
   */
  public ImmutableOntology(ImmutableSortedMap<String, String> metaInfo,
      ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph, TermId rootTermId,
      Collection<? extends TermId> nonObsoleteTermIds, Collection<? extends TermId> obsoleteTermIds,
      ImmutableMap<TermId, T> termMap, ImmutableMap<Integer, R> relationMap) {
    this.metaInfo = metaInfo;
    this.graph = graph;
    this.rootTermId = rootTermId;
    this.termMap = termMap;
    this.nonObsoleteTermIds = ImmutableSet.copyOf(nonObsoleteTermIds);
    this.obsoleteTermIds = ImmutableSet.copyOf(obsoleteTermIds);
    this.allTermIds =
        ImmutableSet.copyOf(Sets.union(this.nonObsoleteTermIds, this.obsoleteTermIds));
    this.relationMap = relationMap;
    this.precomputedAncestors = precomputeAncestors();
  }

  /**
   * @return Precomputed map from term id to list of ancestor term ids (a term is its own ancestor).
   */
  private ImmutableMap<TermId, ImmutableSet<TermId>> precomputeAncestors() {
    final ImmutableMap.Builder<TermId, ImmutableSet<TermId>> mapBuilder = ImmutableMap.builder();

    for (TermId termId : graph.getVertices()) {
      final ImmutableSet.Builder<TermId> setBuilder = ImmutableSet.builder();
      BreadthFirstSearch<TermId, ImmutableEdge<TermId>> bfs = new BreadthFirstSearch<>();
      bfs.startFromForward(graph, termId, new VertexVisitor<TermId, ImmutableEdge<TermId>>() {
        @Override
        public boolean visit(DirectedGraph<TermId, ImmutableEdge<TermId>> g, TermId v) {
          setBuilder.add(v);
          return true;
        }
      });

      mapBuilder.put(termId, setBuilder.build());
    }

    return mapBuilder.build();
  }

  @Override
  public Map<String, String> getMetaInfo() {
    return metaInfo;
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
  public Set<TermId> getAncestorTermIds(TermId termId, boolean includeRoot) {
    final TermId primaryTermId = getPrimaryTermId(termId);
    if (primaryTermId == null) {
      return ImmutableSet.of();
    }

    final ImmutableSet<TermId> precomputed = precomputedAncestors.getOrDefault(termId, ImmutableSet.of());
    if (includeRoot) {
      return precomputed;
    } else {
      return ImmutableSet.copyOf(Sets.difference(precomputed, ImmutableSet.of(rootTermId)));
    }
  }

  @Override
  public Set<TermId> getAllAncestorTermIds(Collection<TermId> termIds, boolean includeRoot) {
    final Set<TermId> result = new HashSet<>();
    for (TermId termId : termIds) {
      result.addAll(getAncestorTermIds(termId, true));
    }
    if (!includeRoot) {
      result.remove(rootTermId);
    }
    return result;
  }

  @Override
  public TermId getRootTermId() {
    return rootTermId;
  }

  @Override
  public Set<TermId> getAllTermIds() {
    return allTermIds;
  }

  @Override
  public Collection<T> getTerms() {
    return termMap.values();
  }

  @Override
  public Set<TermId> getNonObsoleteTermIds() {
    return nonObsoleteTermIds;
  }

  @Override
  public Set<TermId> getObsoleteTermIds() {
    return obsoleteTermIds;
  }

  @Override
  public Ontology<T, R> subOntology(TermId subOntologyRoot) {
    final Set<TermId> childTermIds = OntologyTerms.childrenOf(subOntologyRoot, this);
    final ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> subGraph =
        (ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>>) graph.subGraph(childTermIds);
    return new ImmutableOntology<T, R>(metaInfo, subGraph, subOntologyRoot,
        Sets.intersection(nonObsoleteTermIds, childTermIds),
        Sets.intersection(obsoleteTermIds, childTermIds), termMap, relationMap);
  }

}
