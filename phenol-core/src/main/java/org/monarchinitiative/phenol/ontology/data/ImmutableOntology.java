package org.monarchinitiative.phenol.ontology.data;

import java.util.*;

import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.algo.BreadthFirstSearch;
import org.monarchinitiative.phenol.graph.algo.VertexVisitor;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.ontology.algo.OntologyTerms;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Sets;

/**
 * Implementation of an immutable {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class ImmutableOntology implements Ontology {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 2L;

  /** Meta information, as loaded from file. */
  private final ImmutableSortedMap<String, String> metaInfo;

  /** The graph storing the ontology's structure. */
  private final DefaultDirectedGraph<TermId, IdLabeledEdge> graph;

  /** Id of the root term. */
  private final TermId rootTermId;

  /** The mapping from TermId to TermI for all terms. */
  private final ImmutableMap<TermId, Term> termMap;

  /** Set of non-obselete term ids, separate so maps can remain for sub ontology construction. */
  private final ImmutableSet<TermId> nonObsoleteTermIds;

  /** Set of obselete term ids, separate so maps can remain for sub ontology construction. */
  private final ImmutableSet<TermId> obsoleteTermIds;

  /** Set of all term IDs. */
  private final ImmutableSet<TermId> allTermIds;

  /** The mapping from edge Id to relationship. */
  private final ImmutableMap<Integer, Relationship> relationMap;

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
  public ImmutableOntology(
      ImmutableSortedMap<String, String> metaInfo,
      DefaultDirectedGraph<TermId, IdLabeledEdge> graph,
      TermId rootTermId,
      Collection<? extends TermId> nonObsoleteTermIds,
      Collection<? extends TermId> obsoleteTermIds,
      ImmutableMap<TermId, Term> termMap,
      ImmutableMap<Integer, Relationship> relationMap) {
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

    for (TermId termId : graph.vertexSet()) {
      final ImmutableSet.Builder<TermId> setBuilder = ImmutableSet.builder();
      BreadthFirstSearch<TermId, IdLabeledEdge> bfs = new BreadthFirstSearch<>();
      bfs.startFromForward(
          graph,
          termId,
          new VertexVisitor<TermId, IdLabeledEdge>() {
            @Override
            public boolean visit(DefaultDirectedGraph<TermId, IdLabeledEdge> g, TermId v) {
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
  public DefaultDirectedGraph<TermId, IdLabeledEdge> getGraph() {
    return graph;
  }

  @Override
  public Map<TermId, Term> getTermMap() {
    return termMap;
  }

  @Override
  public Map<Integer, Relationship> getRelationMap() {
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

    final ImmutableSet<TermId> precomputed =
        precomputedAncestors.getOrDefault(termId, ImmutableSet.of());
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
  public Collection<Term> getTerms() {
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
  public Ontology subOntology(TermId subOntologyRoot) {
    final Set<TermId> childTermIds = OntologyTerms.childrenOf(subOntologyRoot, this);
    final DefaultDirectedGraph<TermId, IdLabeledEdge> subGraph =
        GraphUtil.subGraph(graph, childTermIds);
    Set<TermId> intersectingTerms = Sets.intersection(nonObsoleteTermIds, childTermIds);
    // make sure the TermI map contains only terms from the subontology
    final ImmutableMap.Builder<TermId, Term> termBuilder = ImmutableMap.builder();

    for (final TermId tid : intersectingTerms) {
      termBuilder.put(tid, termMap.get(tid));
    }
    ImmutableMap<TermId, Term> subsetTermMap = termBuilder.build();
    // Only retain relations where both source and destination are terms in the subontology
    final ImmutableMap.Builder<Integer, Relationship> relationBuilder = ImmutableMap.builder();
    for (Map.Entry<Integer, Relationship> entry :  relationMap.entrySet() ) {
      Relationship tr = entry.getValue();
      if (subsetTermMap.containsKey(tr.getSource()) && subsetTermMap.containsKey(tr.getTarget())) {
        relationBuilder.put(entry.getKey(), entry.getValue());
      }
    }
    // Note: natural order returns a builder whose keys are ordered by their natural ordering.
    final ImmutableSortedMap.Builder<String, String> metaInfoBuilder =
        ImmutableSortedMap.naturalOrder();
    for (String key : metaInfo.keySet()) {
      metaInfoBuilder.put(key, metaInfo.get(key));
    }
    metaInfoBuilder.put(
        "provenance",
        String.format(
            "Ontology created as a subset from original ontology with root %s",
            getTermMap().get(rootTermId).getName()));
    ImmutableSortedMap<String, String> extendedMetaInfo = metaInfoBuilder.build();

    return new ImmutableOntology(
        extendedMetaInfo,
        subGraph,
        subOntologyRoot,
        intersectingTerms,
        Sets.intersection(obsoleteTermIds, childTermIds),
        subsetTermMap,
        relationBuilder.build());
  }
}
