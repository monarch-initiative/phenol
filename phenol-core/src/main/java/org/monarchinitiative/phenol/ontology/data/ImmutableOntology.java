package org.monarchinitiative.phenol.ontology.data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.*;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.algo.BreadthFirstSearch;
import org.monarchinitiative.phenol.graph.util.CompatibilityChecker;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.ontology.algo.OntologyTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toSet;

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

  /** Set of obselete term ids, separate so maps can remain for sub ontology construction. These are the alt_id entries */
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

  @Override
  public Optional<String> getTermLabel(TermId tid) {
    if (this.termMap.containsKey(tid)) {
      return Optional.of(this.termMap.get(tid).getName());
    } else {
      return Optional.empty();
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Map<String, String> metaInfo = new LinkedHashMap<>();
    private Collection<Term> terms = new ArrayList<>();
    private Collection<Relationship> relationships = new ArrayList<>();

    public Builder metaInfo(Map<String, String> metaInfo) {
      Objects.requireNonNull(metaInfo);
      this.metaInfo = new LinkedHashMap<>(metaInfo);
      return this;
    }

    public Builder terms(Collection<Term> terms) {
      Objects.requireNonNull(terms);
      this.terms = new ArrayList<>(terms);
      return this;
    }

    public Builder relationships(Collection<Relationship> relationships) {
      Objects.requireNonNull(relationships);
      this.relationships = new ArrayList<>(relationships);
      return this;
    }

    public ImmutableOntology build() {
      // A heuristic for determining root node(s).
      // If there are multiple candidate roots, we will just put owl:Thing as the root one.
      // WARNING - this method could mutate the terms and relationships, so DO NOT MOVE THIS METHOD CALL!
      TermId rootId = findRootTermId();

      // Term ids of non-obsolete Terms
      Set<TermId> nonObsoleteTermIds = Sets.newHashSet();
      // Key: a TermId; value: corresponding Term object
      Map<TermId, Term> termsMap = Maps.newTreeMap();

      for (Term term : terms) {
        if (!term.isObsolete()) {
          TermId termId = term.getId();
          nonObsoleteTermIds.add(termId);
          termsMap.put(termId, term);
          for (TermId alternateId : term.getAltTermIds()) {
            termsMap.put(alternateId, term);
          }
        }
      }
      // Term ids of obsolete Terms
      Set<TermId> obsoleteTermIds = terms.stream()
        .filter(Term::isObsolete)
        .map(Term::getId)
        .collect(toSet());

      // The relations are numbered incrementally--this is the key, and the value is the corresponding relation.
      Map<Integer, Relationship> relationshipMap = relationships.stream()
        .collect(Collectors.toMap(Relationship::getId, Function.identity()));

      DefaultDirectedGraph<TermId, IdLabeledEdge> phenolGraph = makeDefaultDirectedGraph(nonObsoleteTermIds, relationships);

      return new ImmutableOntology(
        ImmutableSortedMap.copyOf(metaInfo),
        phenolGraph,
        rootId,
        nonObsoleteTermIds,
        obsoleteTermIds,
        ImmutableMap.copyOf(termsMap),
        ImmutableMap.copyOf(relationshipMap));
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

    private TermId findRootTermId() {
      List<TermId> rootCandidates = findRootCandidates(relationships);
      if (rootCandidates.isEmpty()) {
        throw new PhenolRuntimeException("No root candidate found.");
      }
      if (rootCandidates.size() == 1) {
        return rootCandidates.get(0);
      }
      // No single root candidate, so create a new one and add it into the nodes and edges
      // As per suggestion https://github.com/monarch-initiative/phenol/issues/163#issuecomment-452880405
      // We'll use owl:Thing instead of ID:0000000 so as not to potentially conflict with an existing term id.
      Term artificialRootTerm = Term.of(TermId.of("owl", "Thing"), "artificial root term");
      logger.debug("Created new artificial root term {} {}", artificialRootTerm.getId(), artificialRootTerm.getName());
      addArtificialRootTerm(artificialRootTerm, rootCandidates);

      return artificialRootTerm.getId();
    }

    private List<TermId> findRootCandidates(Collection<Relationship> relationships) {
      Set<TermId> rootCandidateSet = Sets.newHashSet();
      Set<TermId> removeMarkSet = Sets.newHashSet();
      for (Relationship relationship : relationships) {
        TermId subjectTermId = relationship.getSource();
        TermId objectTermId = relationship.getTarget();
        // For each edge and connected nodes,
        // we add candidate obj nodes in rootCandidateSet, i.e. nodes that have incoming edges.
        rootCandidateSet.add(objectTermId);
        // we then remove subj nodes from rootCandidateSet, i.e. nodes that have outgoing edges.
        removeMarkSet.add(subjectTermId);
      }
      rootCandidateSet.removeAll(removeMarkSet);
      return new ArrayList<>(rootCandidateSet);
    }

    private void addArtificialRootTerm(Term rootTerm, List<TermId> rootCandidates) {
      terms.add(rootTerm);
      int edgeId = 1 + relationships.stream().mapToInt(Relationship::getId).max().orElse(0);
      for (TermId rootCandidate : rootCandidates) {
        IdLabeledEdge idLabeledEdge = new IdLabeledEdge(edgeId++);
        //Note-for the "artificial root term, we use the IS_A relation
        Relationship relationship = new Relationship(rootCandidate, rootTerm.getId(), idLabeledEdge.getId(), RelationshipType.IS_A);
        logger.debug("Adding new artificial root relationship {}", relationship);
        relationships.add(relationship);
      }
    }
  }
}
