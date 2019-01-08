package org.monarchinitiative.phenol.ontology.data;

import java.util.*;

import com.google.common.collect.*;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.algo.BreadthFirstSearch;
import org.monarchinitiative.phenol.graph.util.CompatibilityChecker;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.ontology.algo.OntologyTerms;

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
      Collection<TermId> nonObsoleteTermIds,
      Collection<TermId> obsoleteTermIds,
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
        (g, v) -> {
          setBuilder.add(v);
          return true;
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
    final DefaultDirectedGraph<TermId, IdLabeledEdge> subGraph = GraphUtil.subGraph(graph, childTermIds);
    Set<TermId> intersectingTerms = Sets.intersection(nonObsoleteTermIds, childTermIds);

    // make sure the TermI map contains only terms from the subontology
    final ImmutableMap.Builder<TermId, Term> termBuilder = ImmutableMap.builder();
    for (TermId tid : intersectingTerms) {
      termBuilder.put(tid, termMap.get(tid));
    }

    ImmutableMap<TermId, Term> subsetTermMap = termBuilder.build();
    // Only retain relations where both source and destination are terms in the subontology
    final ImmutableMap.Builder<Integer, Relationship> relationBuilder = ImmutableMap.builder();
    for (Map.Entry<Integer, Relationship> entry : relationMap.entrySet() ) {
      Relationship tr = entry.getValue();
      if (subsetTermMap.containsKey(tr.getSource()) && subsetTermMap.containsKey(tr.getTarget())) {
        relationBuilder.put(entry.getKey(), entry.getValue());
      }
    }
    // Note: natural order returns a builder whose keys are ordered by their natural ordering.
    final ImmutableSortedMap.Builder<String, String> metaInfoBuilder = ImmutableSortedMap.naturalOrder();
    for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
      metaInfoBuilder.put(entry.getKey(), entry.getValue());
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Map<String, String> metaInfo = new LinkedHashMap<>();
    private Collection<Term> terms = new ArrayList<>();
    private Collection<Relationship> relationships = new ArrayList<>();

    public Builder metaInfo(Map<String, String> metaInfo) {
      this.metaInfo = metaInfo;
      return this;
    }

    public Builder terms(Collection<Term> terms) {
      this.terms = terms;
      return this;
    }

    public Builder relationships(Collection<Relationship> relationships) {
      this.relationships = relationships;
      return this;
    }

    public ImmutableOntology build() {
      Objects.requireNonNull(metaInfo);
      Objects.requireNonNull(terms);
      Objects.requireNonNull(relationships);

      DefaultDirectedGraph<TermId, IdLabeledEdge> phenolGraph = new DefaultDirectedGraph<>(IdLabeledEdge.class);

      // Term ids of non-obsolete Terms
      Collection<TermId> nonObsoleteTermIds = Sets.newHashSet();
      // Term ids of obsolete Terms
      Collection<TermId> obsoleteTermIds = Sets.newHashSet();
      // Key: a TermId; value: corresponding Term object
      Map<TermId, Term> termsMap = Maps.newTreeMap();

      for (Term term : terms) {
        TermId termId = term.getId();
        if (term.isObsolete()) {
          obsoleteTermIds.add(termId);
        } else {
          nonObsoleteTermIds.add(termId);
          phenolGraph.addVertex(termId);
          termsMap.put(termId, term);
          for (TermId alternateId : term.getAltTermIds()) {
            termsMap.put(alternateId, term);
          }
        }
      }

      Set<TermId> rootCandidateSet = Sets.newHashSet();
      Set<TermId> removeMarkSet = Sets.newHashSet();
      // The relations are numbered incrementally--this is the key, and the value is the corresponding relation.
      Map<Integer, Relationship> relationshipMap = Maps.newHashMap();
      for (Relationship relationship : relationships) {
        TermId subjectTermId = relationship.getSource();
        TermId objectTermId = relationship.getTarget();
        // For each edge and connected nodes,
        // we add candidate obj nodes in rootCandidateSet, i.e. nodes that have incoming edges.
        // we then remove subj nodes from rootCandidateSet, i.e. nodes that have outgoing edges.
        rootCandidateSet.add(objectTermId);
        removeMarkSet.add(subjectTermId);

        IdLabeledEdge edge = new IdLabeledEdge(relationship.getId());
        phenolGraph.addVertex(subjectTermId);
        phenolGraph.addVertex(objectTermId);
        phenolGraph.addEdge(subjectTermId, objectTermId, edge);
        relationshipMap.put(edge.getId(), relationship);
      }

      rootCandidateSet.removeAll(removeMarkSet);
      CompatibilityChecker.check(phenolGraph.vertexSet(), phenolGraph.edgeSet());

      // A heuristic for determining root node(s).
      // If there are multiple candidate roots, we will just put owl:Thing as the root one.
      TermId rootId;
      Optional<TermId> firstId = rootCandidateSet.stream().findFirst();
      if (firstId.isPresent()) {
        if (rootCandidateSet.size() == 1) {
          rootId = firstId.get();
        } else {
          Term rootTerm = createArtificialRootTerm(firstId.get());
          rootId = rootTerm.getId();
          phenolGraph.addVertex(rootId);
          nonObsoleteTermIds.add(rootId);
          termsMap.put(rootId, rootTerm);
          int edgeId = 1 + relationships.stream().mapToInt(Relationship::getId).max().orElse(0);
          for (TermId childOfNewRootTermId : rootCandidateSet) {
            IdLabeledEdge idLabeledEdge = new IdLabeledEdge(edgeId++);
            phenolGraph.addEdge(childOfNewRootTermId, rootId, idLabeledEdge);
            //Note-for the "artificial root term, we use the IS_A relation
            Relationship relationship = new Relationship(childOfNewRootTermId, rootId, idLabeledEdge.getId(), RelationshipType.IS_A);
            relationshipMap.put(idLabeledEdge.getId(), relationship);
          }
        }
      } else {
        throw new PhenolRuntimeException("No root candidate found.");
      }

      return new ImmutableOntology(
        ImmutableSortedMap.copyOf(metaInfo),
        phenolGraph,
        rootId,
        nonObsoleteTermIds,
        obsoleteTermIds,
        ImmutableMap.copyOf(termsMap),
        ImmutableMap.copyOf(relationshipMap));
    }

    private Term createArtificialRootTerm(TermId firstId) {
      // getPrefix should always work actually, but if we cannot find a term for some reason, use Owl as the prefix
      // Assumption: "0000000" is not used for actual terms in any OBO ontology, otherwise use owl terminology
      TermId artificialTermId = firstId == null ? TermId.of("owl", "thing") : TermId.of(firstId.getPrefix(), "0000000");
      return Term.of(artificialTermId, "artificial root term");
    }
  }
}
