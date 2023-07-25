package org.monarchinitiative.phenol.ontology.data.impl;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.OntologyGraphBuilders;
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
  private final Map<Integer, Relationship> relationMap;
  private final SortedMap<String, String> metaInfo;

  SimpleMinimalOntology(OntologyGraph<TermId> ontologyGraph,
                        List<Term> terms,
                        Map<TermId, Term> termMap,
                        Map<Integer, Relationship> relationMap,
                        SortedMap<String, String> metaInfo) {
    this.ontologyGraph = Objects.requireNonNull(ontologyGraph);
    this.terms = Objects.requireNonNull(terms);
    this.termMap = Objects.requireNonNull(termMap);
    this.relationMap = Objects.requireNonNull(relationMap);
    this.metaInfo = Objects.requireNonNull(metaInfo);
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
    return relationMap;
  }

  @Override
  public Optional<Relationship> relationshipById(int relationshipId) {
    return Optional.ofNullable(relationMap.get(relationshipId));
  }

  @Override
  public TermId getRootTermId() {
    return ontologyGraph.root();
  }

  @Override
  public Iterable<TermId> allTermIds() {
    return new IterableIteratorWrapper<>(() -> new AllTermIdIterator(terms.iterator()));
  }

  @Override
  public Iterable<TermId> nonObsoleteTermIds() {
    return new IterableIteratorWrapper<>(() -> terms.stream().map(Term::id).iterator());
  }

  @Override
  public Iterable<TermId> obsoleteTermIds() {
    return new IterableIteratorWrapper<>(() -> new AltTermIdIterator(terms.iterator()));
  }

  @Override
  public Collection<Term> getTerms() {
    return terms;
  }

  @Override
  public Optional<String> version() {
    return Optional.ofNullable(metaInfo.get("release"));
  }

  /**
   * Create a new builder for the {@linkplain SimpleMinimalOntology}.
   *
   * @return
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private RelationshipType hierarchyRelationshipType = RelationshipType.IS_A;
    private Map<String, String> metaInfo = Map.of();
    private List<Term> terms;
    private List<Relationship> relationships;

    private Builder() {}

    public Builder hierarchyRelationshipType(RelationshipType relationshipType) {
      this.hierarchyRelationshipType = Objects.requireNonNull(relationshipType);
      return this;
    }

    public Builder metaInfo(Map<String, String> metaInfo) {
      this.metaInfo = Objects.requireNonNull(metaInfo);
      return this;
    }

    public Builder terms(List<Term> terms) {
      this.terms = Objects.requireNonNull(terms);
      return this;
    }

    public Builder relationships(List<Relationship> relationships) {
      this.relationships = Objects.requireNonNull(relationships);
      return this;
    }

    public SimpleMinimalOntology build() {
      // Check if we've got everything we need.
      if (terms == null || terms.isEmpty())
        throw new IllegalStateException("No terms were provided to build the ontology");
      if (relationships == null || relationships.isEmpty())
        throw new IllegalStateException("No relationships were provided to build the ontology");

      // First, find the root term.
      TermId rootId = OntologyUtils.findRootTermId(terms, relationships, () -> hierarchyRelationshipType);

      // Next, build the graph.
      OntologyGraph<TermId> ontologyGraph = OntologyGraphBuilders.csrBuilder(Short.class)
        .hierarchyRelation(hierarchyRelationshipType)
        .build(rootId, relationships);

      // Then, build the term and relationship maps.
      Map<TermId, Term> termMap = new HashMap<>();
      List<Term> primaryTerms = new ArrayList<>();
      for (Term term : terms) {
        if (!term.isObsolete()) {
          primaryTerms.add(term);
          termMap.put(term.id(), term);
          for (TermId altTermId : term.getAltTermIds()) {
            termMap.put(altTermId, term);
          }
        }
      }

      Map<Integer, Relationship> relationshipMap = relationships.stream()
        .collect(Collectors.toUnmodifiableMap(Relationship::getId, Function.identity()));

      // Finally, wrap everything into the ontology!
      return new SimpleMinimalOntology(ontologyGraph, primaryTerms, termMap, relationshipMap, new TreeMap<>(metaInfo));
    }

  }
}
