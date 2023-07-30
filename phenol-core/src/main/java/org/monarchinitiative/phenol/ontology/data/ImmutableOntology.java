package org.monarchinitiative.phenol.ontology.data;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.OntologyGraphBuilders;
import org.monarchinitiative.phenol.graph.algo.BreadthFirstSearch;
import org.monarchinitiative.phenol.graph.util.CompatibilityChecker;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.monarchinitiative.phenol.ontology.algo.OntologyTerms;
import org.monarchinitiative.phenol.utils.OntologyUtils;
import org.monarchinitiative.phenol.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of an immutable {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class ImmutableOntology implements Ontology {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 2L;

  private static final Logger logger = LoggerFactory.getLogger(ImmutableOntology.class);

  /**
   * Meta information, as loaded from file.
   */
  private final SortedMap<String, String> metaInfo;

  /** The graph storing the ontology's structure. */
  private final DefaultDirectedGraph<TermId, IdLabeledEdge> graph;
  private final OntologyGraph<TermId> ontologyGraph;

  /** Id of the root term. */
  private final TermId rootTermId;

  /**
   * The mapping from TermId to TermI for all terms.
   */
  private final Map<TermId, Term> termMap;

  /**
   * Set of non-obsolete term ids, separate so maps can remain for sub ontology construction.
   */
  private final Set<TermId> nonObsoleteTermIds;

  /**
   * Set of obsolete term ids, separate so maps can remain for sub ontology construction. These are the alt_id entries
   */
  private final Set<TermId> obsoleteTermIds;

  /**
   * Set of all term IDs.
   */
  private final Set<TermId> allTermIds;

  /**
   * The mapping from edge Id to relationship.
   */
  private final Map<Integer, Relationship> relationMap;

  /**
   * Precomputed ancestors (including vertex itself).
   */
  private final Map<TermId, Set<TermId>> precomputedAncestors;

  /**
   * Constructor.
   *
   * @param metaInfo           {@link Map} with meta information.
   * @param graph              Graph to use for underlying structure.
   * @param ontologyGraph      Internal graph representation intended to replace {@code graph}.
   * @param rootTermId         Root node's {@link TermId}.
   * @param nonObsoleteTermIds {@link Collection} of {@link TermId}s of non-obsolete terms.
   * @param obsoleteTermIds    {@link Collection} of {@link TermId}s of obsolete terms.
   * @param termMap            Mapping from {@link TermId} to <code>T</code>.
   * @param relationMap        Mapping from numeric edge Id to <code>R</code>.
   */
  private ImmutableOntology(SortedMap<String, String> metaInfo,
                            DefaultDirectedGraph<TermId, IdLabeledEdge> graph,
                            OntologyGraph<TermId> ontologyGraph,
                            TermId rootTermId,
                            Set<TermId> nonObsoleteTermIds,
                            Set<TermId> obsoleteTermIds,
                            Map<TermId, Term> termMap,
                            Map<Integer, Relationship> relationMap) {
    this.metaInfo = Objects.requireNonNull(metaInfo, "Meta info must not be null");
    this.ontologyGraph = ontologyGraph; // nullable to allow creating a sub-ontology.
    this.graph = Objects.requireNonNull(graph, "Graph must not be null");
    this.rootTermId = Objects.requireNonNull(rootTermId, "Root term ID must not be null");
    this.termMap = Objects.requireNonNull(termMap, "Term map must not be null");
    this.nonObsoleteTermIds = Objects.requireNonNull(nonObsoleteTermIds, "Non-obsolete term IDs must not be null");
    this.obsoleteTermIds = Objects.requireNonNull(obsoleteTermIds, "Obsolete term IDs must not be null");
    this.allTermIds = Set.copyOf(Sets.union(nonObsoleteTermIds, obsoleteTermIds));
    this.relationMap = Objects.requireNonNull(relationMap, "Relation map must not be null");
    this.precomputedAncestors = precomputeAncestors(graph);
  }

  /**
   * @return Precomputed map from term id to list of ancestor term ids (a term is its own ancestor).
   */
  private static Map<TermId, Set<TermId>> precomputeAncestors(DefaultDirectedGraph<TermId, IdLabeledEdge> graph) {
    Map<TermId, Set<TermId>> mapBuilder = new HashMap<>();

    Set<TermId> ancestors = new HashSet<>(20);
    for (TermId termId : graph.vertexSet()) {
      BreadthFirstSearch<TermId, IdLabeledEdge> bfs = new BreadthFirstSearch<>();
      bfs.startFromForward(graph, termId, (g, v) -> {
        ancestors.add(v);
        return true;
      });
      mapBuilder.put(termId, Set.copyOf(ancestors));
      ancestors.clear();
    }

    return Map.copyOf(mapBuilder);
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
  public OntologyGraph<TermId> graph() {
    return ontologyGraph;
  }

  @Override
  public Map<TermId, Term> getTermMap() {
    return termMap;
  }

  @Override
  public Optional<Term> termForTermId(TermId termId) {
    return Optional.ofNullable(termMap.get(Objects.requireNonNull(termId)));
  }

  @Override
  public Map<Integer, Relationship> getRelationMap() {
    return relationMap;
  }

  @Override
  public Optional<Relationship> relationshipById(int relationshipId) {
    return Optional.ofNullable(relationMap.get(relationshipId));
  }

  @Override
  public Set<TermId> getAncestorTermIds(TermId termId, boolean includeRoot) {
    final TermId primaryTermId = getPrimaryTermId(termId);
    if (primaryTermId == null) {
      return Set.of();
    }

    Set<TermId> precomputed = precomputedAncestors.getOrDefault(termId, Set.of());
    if (includeRoot) {
      return precomputed;
    } else {
      Set<TermId> builder = new HashSet<>(precomputed.size() - 1);
      for (TermId term : precomputed) {
        if (!rootTermId.equals(term))
          builder.add(term);
      }
      return Set.copyOf(builder);
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
  public Set<TermId> getCommonAncestors(TermId a, TermId b) {
    Set<TermId> ancA = getAncestorTermIds(a, false);
    Set<TermId> ancB = getAncestorTermIds(b, false);
    return Sets.intersection(ancA, ancB);
  }

  @Override
  public TermId getRootTermId() {
    return rootTermId;
  }

  @Override
  public Iterable<TermId> allTermIds() {
    return allTermIds;
  }

  @Override
  public Collection<Term> getTerms() {
    return termMap.values();
  }

  @Override
  public Iterable<TermId> nonObsoleteTermIds() {
    return nonObsoleteTermIds;
  }

  @Override
  public Iterable<TermId> obsoleteTermIds() {
    return obsoleteTermIds;
  }

  @Override
  public Ontology subOntology(TermId subOntologyRoot) {
    Set<TermId> childTermIds = OntologyTerms.childrenOf(subOntologyRoot, this);
    DefaultDirectedGraph<TermId, IdLabeledEdge> subGraph = GraphUtil.subGraph(graph, childTermIds);
    Set<TermId> intersectingTerms = Sets.intersection(nonObsoleteTermIds, childTermIds);

    // make sure the TermId map contains only terms from the subontology
    Map<TermId, Term> termBuilder = new HashMap<>();
    for (TermId tid : intersectingTerms) {
      termBuilder.put(tid, termMap.get(tid));
    }
    Map<TermId, Term> subsetTermMap = Map.copyOf(termBuilder);

    // Only retain relations where both source and destination are terms in the subontology
    Map<Integer, Relationship> relationBuilder = new HashMap<>();
    for (Map.Entry<Integer, Relationship> entry : relationMap.entrySet()) {
      Relationship tr = entry.getValue();
      if (subsetTermMap.containsKey(tr.getSource()) && subsetTermMap.containsKey(tr.getTarget())) {
        relationBuilder.put(entry.getKey(), entry.getValue());
      }
    }
    Map<Integer, Relationship> relationMap = Map.copyOf(relationBuilder);

    // Note: natural order returns a builder whose keys are ordered by their natural ordering.
    SortedMap<String, String> metaInfoBuilder = new TreeMap<>();
    metaInfoBuilder.putAll(metaInfo);
    metaInfoBuilder.put("provenance", String.format("Ontology created as a subset from original ontology with root %s", getTermMap().get(rootTermId).getName()));

    return new ImmutableOntology(Collections.unmodifiableSortedMap(metaInfoBuilder),
      subGraph,
      null,
      subOntologyRoot,
      intersectingTerms,
      Sets.intersection(obsoleteTermIds, childTermIds),
      subsetTermMap,
      relationMap);
  }

  @Override
  public Optional<String> version() {
    return Optional.ofNullable(metaInfo.get("release"));
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final SortedMap<String, String> metaInfo = new TreeMap<>();
    private final Collection<Term> terms = new ArrayList<>();
    private final Collection<Relationship> relationships = new ArrayList<>();

    private Builder() {
    }

    public Builder metaInfo(Map<String, String> metaInfo) {
      Objects.requireNonNull(metaInfo);
      this.metaInfo.putAll(metaInfo);
      return this;
    }

    public Builder terms(Collection<Term> terms) {
      Objects.requireNonNull(terms);
      this.terms.addAll(terms);
      return this;
    }

    public Builder relationships(Collection<Relationship> relationships) {
      Objects.requireNonNull(relationships);
      this.relationships.addAll(relationships);
      return this;
    }

    public ImmutableOntology build() {
      // A heuristic for determining root node(s).
      // If there are multiple candidate roots, we will just put owl:Thing as the root one.
      // WARNING - this method could mutate the terms and relationships, so DO NOT MOVE THIS METHOD CALL!
      RelationshipType isA = RelationshipType.IS_A;
      TermId rootId = OntologyUtils.findRootTermId(terms, relationships, () -> isA);

      OntologyGraph<TermId> ontologyGraph = OntologyGraphBuilders.csrBuilder(Long.class)
        .hierarchyRelation(isA)
        .build(rootId, relationships);

      Set<TermId> obsoleteTermIds = new HashSet<>();
      Set<TermId> nonObsoleteTermIds = new HashSet<>();
      Map<TermId, Term> nonObsoleteTerms = new HashMap<>();

      for (Term term : terms) {
        if (term.isObsolete()) {
          obsoleteTermIds.add(term.id());
        } else {
          TermId termId = term.id();
          nonObsoleteTermIds.add(termId);
          nonObsoleteTerms.put(termId, term);
          for (TermId alternateId : term.getAltTermIds()) {
            nonObsoleteTerms.put(alternateId, term);
          }
        }
      }

      // The relations are numbered incrementally--this is the key, and the value is the corresponding relation.
      Map<Integer, Relationship> relationshipMap = relationships.stream()
        .collect(Collectors.toUnmodifiableMap(Relationship::getId, Function.identity()));

      DefaultDirectedGraph<TermId, IdLabeledEdge> phenolGraph = makeDefaultDirectedGraph(nonObsoleteTermIds, relationships);

      return new ImmutableOntology(Collections.unmodifiableSortedMap(metaInfo),
        phenolGraph,
        ontologyGraph,
        rootId,
        nonObsoleteTermIds,
        obsoleteTermIds,
        Map.copyOf(nonObsoleteTerms),
        relationshipMap);
    }

    private DefaultDirectedGraph<TermId, IdLabeledEdge> makeDefaultDirectedGraph(Set<TermId> nonObsoleteTermIds, Collection<Relationship> relationships) {
      DefaultDirectedGraph<TermId, IdLabeledEdge> phenolGraph = new DefaultDirectedGraph<>(IdLabeledEdge.class);
      // This is probably redundant as the relationships should contain all non-obsolete TermIds
      nonObsoleteTermIds.forEach(phenolGraph::addVertex);

      for (Relationship relationship : relationships) {
        TermId subjectTermId = relationship.getSource();
        TermId objectTermId = relationship.getTarget();

        phenolGraph.addVertex(subjectTermId);
        phenolGraph.addVertex(objectTermId);
        phenolGraph.addEdge(subjectTermId, objectTermId, new IdLabeledEdge(relationship.getId()));
      }

      CompatibilityChecker.check(phenolGraph.vertexSet(), phenolGraph.edgeSet());
      return phenolGraph;
    }

  }
}
