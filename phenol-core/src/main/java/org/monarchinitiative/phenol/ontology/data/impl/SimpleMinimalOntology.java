package org.monarchinitiative.phenol.ontology.data.impl;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.OntologyGraphBuilder;
import org.monarchinitiative.phenol.graph.OntologyGraphBuilders;
import org.monarchinitiative.phenol.graph.util.CompatibilityChecker;
import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.utils.IterableIteratorWrapper;
import org.monarchinitiative.phenol.utils.OntologyUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link SimpleMinimalOntology} implements {@linkplain MinimalOntology} with an {@link OntologyGraph}
 * and {@linkplain Map}s of {@link Term}s and {@link Relationship}s.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class SimpleMinimalOntology implements MinimalOntology {

  private final OntologyGraph<TermId> ontologyGraph;
  private final List<Term> terms;
  private final Map<TermId, Term> termMap;
  private final RelationshipContainer relationships;
  private final SortedMap<String, String> metaInfo;
  private final TermIdCount termIdCount;

  SimpleMinimalOntology(OntologyGraph<TermId> ontologyGraph,
                        List<Term> terms,
                        Map<TermId, Term> termMap,
                        RelationshipContainer relationships,
                        SortedMap<String, String> metaInfo,
                        TermIdCount termIdCount) {
    this.ontologyGraph = Objects.requireNonNull(ontologyGraph);
    this.terms = Objects.requireNonNull(terms);
    this.termMap = Objects.requireNonNull(termMap);
    this.relationships = Objects.requireNonNull(relationships);
    this.metaInfo = Objects.requireNonNull(metaInfo);
    this.termIdCount = Objects.requireNonNull(termIdCount);
  }


  @Override
  public Map<String, String> getMetaInfo() {
    return metaInfo;
  }

  @Override
  public DefaultDirectedGraph<TermId, IdLabeledEdge> getGraph() {
    throw new UnsupportedOperationException("SimpleMinimalOntology does not use JGraphT graph!");
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
    return Optional.ofNullable(termMap.get(termId));
  }

  @Override
  public Map<Integer, Relationship> getRelationMap() {
    return relationships.getRelationMap();
  }

  @Override
  public Optional<Relationship> relationshipById(int relationshipId) {
    return relationships.relationshipById(relationshipId);
  }

  @Override
  public Iterable<TermId> allTermIds() {
    return new IterableIteratorWrapper<>(() -> new AllTermIdIterator(terms.iterator()));
  }

  @Override
  public int allTermIdCount() {
    return termIdCount.allTermIdCount;
  }

  @Override
  public Iterable<TermId> nonObsoleteTermIds() {
    return new IterableIteratorWrapper<>(() -> terms.stream().map(Term::id).iterator());
  }

  @Override
  public int nonObsoleteTermIdCount() {
    return termIdCount.nonObsoleteTermIdCount;
  }

  @Override
  public Iterable<TermId> obsoleteTermIds() {
    return new IterableIteratorWrapper<>(() -> new AltTermIdIterator(terms.iterator()));
  }

  @Override
  public int obsoleteTermIdsCount() {
    return termIdCount.obsoleteTermIdCount;
  }

  @Override
  public Collection<Term> getTerms() {
    return terms;
  }

  @Override
  public MinimalOntology subOntology(TermId subOntologyRoot) {
    if (subOntologyRoot.equals(getRootTermId()))
      return this;

    if (!containsTermId(subOntologyRoot))
        throw new PhenolRuntimeException(String.format("%s is not in the ontology", subOntologyRoot.getValue()));

    OntologyGraph<TermId> subGraph = ontologyGraph.extractSubgraph(subOntologyRoot);

    List<Term> terms = new ArrayList<>();
    Map<TermId, Term> termMap = new HashMap<>();
    int all = 0, obsolete = 0;
    for (TermId termId : subGraph) {
      Optional<Term> term = termForTermId(termId);
      if (term.isPresent()) {
        Term t = term.get();
        terms.add(t);
        termMap.put(termId, t);

        if (t.isObsolete())
          obsolete++;

        all++;
      }
    }

    // TODO - implement filtering
    RelationshipContainer relationships = this.relationships;

    TermIdCount termIdCount = new TermIdCount(all, obsolete, all - obsolete);

    SortedMap<String, String> metaInfo = updateMetaInfo(subOntologyRoot, this.metaInfo);
    return new SimpleMinimalOntology(subGraph, terms, termMap, relationships, metaInfo, termIdCount);
  }

  private static SortedMap<String, String> updateMetaInfo(TermId subOntologyRoot, SortedMap<String, String> original) {
    SortedMap<String, String> metaBuilder = new TreeMap<>(original);
    metaBuilder.put("provenance", String.format("Ontology created as a subset from original ontology with root %s", subOntologyRoot.getValue()));
    return Collections.unmodifiableSortedMap(metaBuilder);
  }

  @Override
  public Optional<String> version() {
    return Optional.ofNullable(metaInfo.get("release"));
  }

  private static class TermIdCount {
    private final int allTermIdCount;
    private final int obsoleteTermIdCount;
    private final int nonObsoleteTermIdCount;

    private TermIdCount(int allTermIdCount, int obsoleteTermIdCount, int nonObsoleteTermIdCount) {
      this.allTermIdCount = allTermIdCount;
      this.obsoleteTermIdCount = obsoleteTermIdCount;
      this.nonObsoleteTermIdCount = nonObsoleteTermIdCount;
    }
  }

  /**
   * Create a new builder for the {@linkplain SimpleMinimalOntology}.
   *
   * @return the builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for building {@linkplain SimpleMinimalOntology}.
   */
  public static class Builder {

    private RelationshipType hierarchyRelationshipType = RelationshipType.IS_A;
    private Map<String, String> metaInfo = Map.of();
    private final List<Term> terms = new ArrayList<>();
    private final List<Relationship> relationships = new ArrayList<>();
    private boolean forceBuild = false;
    private GraphImplementation graphImplementation = GraphImplementation.MONO;

    /**
     * Enum to choose from the {@link OntologyGraph} implementations.
     */
    public enum GraphImplementation {

      /**
       * Uses {@link OntologyGraphBuilders#csrBuilder(Class)} to build the graph.
       */
      POLY,

      /**
       * Uses {@link OntologyGraphBuilders#monoCsrBuilder()} to build the graph.
       */
      MONO
    }

    private Builder() {}

    /**
     * Set the relationship type to be used to represent the ontology hierarchy.
     *
     * @param relationshipType a non-null {@linkplain RelationshipType}.
     * @return the builder.
     */
    public Builder hierarchyRelationshipType(RelationshipType relationshipType) {
      this.hierarchyRelationshipType = Objects.requireNonNull(relationshipType);
      return this;
    }

    /**
     * Set the ontology meta information.
     *
     * @param metaInfo a non-null {@linkplain Map} with key-value meta information.
     * @return the builder.
     */
    public Builder metaInfo(Map<String, String> metaInfo) {
      this.metaInfo = Objects.requireNonNull(metaInfo);
      return this;
    }

    /**
     * Set the ontology terms.
     *
     * @param terms a non-null {@linkplain Collection} of terms.
     * @return the builder.
     */
    public Builder terms(Collection<? extends Term> terms) {
      this.terms.clear();
      this.terms.addAll(Objects.requireNonNull(terms));
      return this;
    }

    /**
     * Set the ontology relationships.
     *
     * @param relationships a non-null {@linkplain Collection} of relationships.
     * @return the builder.
     */
    public Builder relationships(Collection<? extends Relationship> relationships) {
      this.relationships.clear();
      this.relationships.addAll(Objects.requireNonNull(relationships));
      return this;
    }

    /**
     * Force the build by skipping the compatibility checks.
     *
     * @param value {@code true} if the checks should be skipped.
     * @return the builder.
     */
    public Builder forceBuild(boolean value) {
      this.forceBuild = value;
      return this;
    }

    /**
     * Set the graph implementation to be used.
     *
     * @param graphImplementation the graph implementation to use.
     * @return the builder.
     */
    public Builder graphImplementation(GraphImplementation graphImplementation) {
      this.graphImplementation = Objects.requireNonNull(graphImplementation);
      return this;
    }

    /**
     * Build the ontology from the provided {@code metaInfo}, {@code terms}, and {@code relationships}.
     * @return the built {@link SimpleMinimalOntology}.
     */
    public SimpleMinimalOntology build() {
      // Check if we've got everything we need.
      if (terms.isEmpty())
        throw new IllegalStateException("No terms were provided to build the ontology");
      if (relationships.isEmpty())
        throw new IllegalStateException("No relationships were provided to build the ontology");

      // Then, find the root term and build the graph.
      // IMPORTANT - this must be done before working with terms and relationships because an artificial root
      // may be added!
      TermId rootId = OntologyUtils.findRootTermId(terms, relationships, () -> hierarchyRelationshipType);

      // Then, build the term and relationship maps.
      Map<TermId, Term> termMap = new HashMap<>();
      List<Term> primaryTerms = new ArrayList<>();
      int all = 0, nonObsolete = 0;
      for (Term term : terms) {
        if (!term.isObsolete()) {
          primaryTerms.add(term);
          termMap.put(term.id(), term);
          all++;
          nonObsolete++;
          for (TermId altTermId : term.getAltTermIds()) {
            termMap.put(altTermId, term);
            all++;
          }
        }
      }

      RelationshipContainer relationshipContainer = packageRelationships(relationships);
      TermIdCount termIdCount = new TermIdCount(all, all - nonObsolete, nonObsolete);

      if (!forceBuild) {
        // Check if the vertices and edges meet the ontology graph requirements.
        List<TermId> vertices = primaryTerms.stream()
          .map(Term::id)
          .collect(Collectors.toList());
        CompatibilityChecker.checkCompatibility(vertices, relationships);
      }

      // Build the graph.
      OntologyGraphBuilder<TermId> graphBuilder;
      switch (graphImplementation) {
        case MONO:
          graphBuilder = OntologyGraphBuilders.monoCsrBuilder();
          break;
        case POLY:
          graphBuilder = OntologyGraphBuilders.csrBuilder(Long.class);
          break;
        default:
          throw new IllegalArgumentException(String.format("Unsupported graph implementation %s", graphImplementation));
      }
      OntologyGraph<TermId> ontologyGraph = graphBuilder.hierarchyRelation(hierarchyRelationshipType)
        .build(rootId, relationships);

      // Finally, wrap everything into the ontology!
      return new SimpleMinimalOntology(ontologyGraph,
        primaryTerms,
        termMap,
        relationshipContainer,
        Collections.unmodifiableSortedMap(new TreeMap<>(metaInfo)),
        termIdCount);
    }

    private RelationshipContainer packageRelationships(List<Relationship> relationships) {
      int max = -1, min = 0;
      for (Relationship relationship : relationships) {
        int id = relationship.getId();
        max = Math.max(id, max);
        min = Math.min(id, min);
      }
      if (min < 0) {
        // We have >=1 negative relationship indices, therefore we cannot use `ArrayRelationshipContainer`
        return new MapRelationshipContainer(relationships.stream()
          .collect(Collectors.toMap(Relationship::getId, Function.identity())));
      } else {
        // All relationship indices are positive ints.
        int span = max - min;
        float occupancy = (float) (span) / (relationships.size() -1); // -1 due to 0-based indexing.
        if (occupancy > .90f) {
          // >90% entries are used
          return ArrayRelationshipContainer.of(relationships);
        } else {
          return new MapRelationshipContainer(relationships.stream()
            .collect(Collectors.toMap(Relationship::getId, Function.identity())));
        }
      }
    }

  }
}
