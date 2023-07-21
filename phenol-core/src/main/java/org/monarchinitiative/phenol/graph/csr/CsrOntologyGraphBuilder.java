package org.monarchinitiative.phenol.graph.csr;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.*;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A builder for {@link OntologyGraph} backed by a CSR matrix.
 *
 * @param <E> data type for storing the relationships between graph nodes.
 * @see CsrOntologyGraph
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrOntologyGraphBuilder<E> implements OntologyGraphBuilder<TermId> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsrOntologyGraphBuilder.class);

  /**
   * Create the builder using given type for storing the edge values.
   */
  public static <E> CsrOntologyGraphBuilder<E> builder(Class<E> clz) {
    DataIndexer<E> indexer = indexerForClass(Objects.requireNonNull(clz));
    return new CsrOntologyGraphBuilder<>(indexer);
  }

  // The casts are safe since we can only use 4 supported types, and we fail for the other types.
  @SuppressWarnings("unchecked")
  private static <E> DataIndexer<E> indexerForClass(Class<E> clz) {
    if (clz.equals(Byte.class)) {
      return (DataIndexer<E>) DataIndexer.byteIndexer();
    } else if (clz.equals(Short.class)) {
      return (DataIndexer<E>) DataIndexer.shortIndexer();
    } else if (clz.equals(Integer.class)) {
      return (DataIndexer<E>) DataIndexer.integerIndexer();
    } else if (clz.equals(Long.class)) {
      return (DataIndexer<E>) DataIndexer.longIndexer();
    } else {
      throw new PhenolRuntimeException(String.format("%s cannot be used to hold the edge data", clz.getName()));
    }
  }

  private RelationType hierarchyRelation = RelationTypes.isA();
  private final DataIndexer<E> indexer;

  private CsrOntologyGraphBuilder(DataIndexer<E> indexer) {
    this.indexer = Objects.requireNonNull(indexer);
  }

  /**
   * Set the {@code relationType} to be used as the main hierarchy.
   * <p>
   * {@link RelationTypes#isA()} by default.
   */
  @Override
  public CsrOntologyGraphBuilder<E> hierarchyRelation(RelationType relationType) {
    if (relationType == null)
      LOGGER.warn("Hierarchy relation type must not be null. Skipping..");
    else
      this.hierarchyRelation = relationType;
    return this;
  }

  @Override
  public CsrOntologyGraph<TermId, E> build(Collection<OntologyGraphEdge<TermId>> edges) {
    TermId root = findRootCandidates(edges, hierarchyRelation);

    List<RelationType> relationTypes = edges.stream()
      .map(OntologyGraphEdge::relationType)
      .distinct()
      .collect(Collectors.toList());
    RelationCodec<E> codec = RelationCodec.of(relationTypes, indexer);
    int maxIndex = codec.maxIdx();
    if (maxIndex > indexer.maxIdx())
      throw new IllegalArgumentException(String.format("Unable to encode %d relation types using %d slots",
        maxIndex, indexer.maxIdx()));

    LOGGER.debug("Sorting graph nodes");
    TermId[] nodes = edges.stream()
      .flatMap(e -> Stream.of(e.subject(), e.object()))
      .sorted(TermId::compareTo)
      .distinct()
      .toArray(TermId[]::new);

    LOGGER.debug("Building the adjacency matrix");
    CsrData<E> csrData = makeCsrData(nodes, edges, codec);
    ImmutableCsrMatrix<E> adjacencyMatrix = new ImmutableCsrMatrix<>(csrData.getIndptr(), csrData.getIndices(), csrData.getData());

    LOGGER.debug("Assembling the ontology graph");
    Predicate<E> hierarchy = e -> codec.isSet(e, hierarchyRelation, false);
    Predicate<E> hierarchyInverted = e -> codec.isSet(e, hierarchyRelation, true);
    return new CsrOntologyGraph<>(root, nodes, adjacencyMatrix, TermId::compareTo, hierarchy, hierarchyInverted);
  }

  private static TermId findRootCandidates(Collection<OntologyGraphEdge<TermId>> edges,
                                           RelationType hierarchy) {
    Set<TermId> rootCandidates = new HashSet<>();
    Set<TermId> removeMark = new HashSet<>();
    for (OntologyGraphEdge<TermId> edge : edges) {
      if (hierarchy.equals(edge.relationType())) {
        rootCandidates.add(edge.object());
        removeMark.add(edge.subject());
      }
    }

    rootCandidates.removeAll(removeMark);
    if (rootCandidates.size() == 0) {
      throw new PhenolRuntimeException("No root candidate found");
    } else if (rootCandidates.size() == 1) {
      TermId root = rootCandidates.iterator().next();
      LOGGER.debug("Found root candidate {}", root);

      return root;
    } else {
      // No single root candidate, so create a new one and add it into the nodes and edges
      // As per suggestion https://github.com/monarch-initiative/phenol/issues/163#issuecomment-452880405
      // We'll use owl:Thing instead of ID:0000000 so as not to potentially conflict with an existing term id.
      LOGGER.debug("Found {} root candidates. Adding owl:Thing", rootCandidates.size());
      TermId root = TermId.of("owl:Thing");
      for (TermId candidate : rootCandidates) {
        edges.add(OntologyGraphEdge.of(candidate, root, hierarchy));
      }

      return root;
    }
  }

  private CsrData<E> makeCsrData(TermId[] nodes,
                                 Collection<OntologyGraphEdge<TermId>> edges,
                                 RelationCodec<E> codec) {
    List<Integer> indptr = new ArrayList<>();
    indptr.add(0);
    List<Integer> indices = new ArrayList<>();
    List<E> data = new ArrayList<>();

    Map<Integer, List<OntologyGraphEdge<TermId>>> adjacentEdges = findAdjacentEdges(nodes, edges);

    CsrRowBuilder<E> row = new CsrRowBuilder<>(indexer);
    for (int rowIdx = 0; rowIdx < nodes.length; rowIdx++) {
      TermId source = nodes[rowIdx];
      List<OntologyGraphEdge<TermId>> adjacent = adjacentEdges.getOrDefault(rowIdx, List.of());

      for (OntologyGraphEdge<TermId> edge : adjacent) {
        boolean inverted = source.equals(edge.subject());
        TermId target = inverted ? edge.object() : edge.subject();

        // encode the relationship into the edge.
        int colIdx = Util.getIndexOfUsingBinarySearch(target, nodes, TermId::compareTo);
        int bitIdx = codec.calculateBitIndex(edge.relationType(), inverted);
        row.setNthBitInCol(colIdx, bitIdx);
      }

      indices.addAll(row.getColIndices());
      data.addAll(row.getValues());
      indptr.add(data.size());
      row.clear();
    }

    return new CsrData<>(Util.toIntArray(indptr), Util.toIntArray(indices), data.toArray(indexer.createArray()));
  }

  /**
   * We build the CSR matrix row by row, and we need to know about all nodes that are adjacent with
   * (have a relationship with) the node represented by the row under the construction.
   * Here we prepare a mapping from the row index to a list of all adjacent edges.
   */
  private static Map<Integer, List<OntologyGraphEdge<TermId>>> findAdjacentEdges(TermId[] nodes,
                                                                                 Collection<OntologyGraphEdge<TermId>> edges) {
    Map<Integer, List<OntologyGraphEdge<TermId>>> data = new HashMap<>();

    TermId lastSub = null;
    int lastSubIdx = -1;

    for (OntologyGraphEdge<TermId> edge : edges) {
      int subIdx;
      TermId sub = edge.subject();
      if (sub == lastSub) {
        subIdx = lastSubIdx;
      } else {
        lastSub = sub;
        subIdx = Util.getIndexOfUsingBinarySearch(sub, nodes, TermId::compareTo);
        lastSubIdx = subIdx;
      }

      TermId obj = edge.object();
      int objIdx = Util.getIndexOfUsingBinarySearch(obj, nodes, TermId::compareTo);

      data.computeIfAbsent(subIdx, x -> new ArrayList<>()).add(edge);
      data.computeIfAbsent(objIdx, x -> new ArrayList<>()).add(edge);
    }

    return data;
  }

}
