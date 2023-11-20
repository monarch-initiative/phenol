package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.*;
import org.monarchinitiative.phenol.graph.csr.util.Util;
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
 * @see CsrPolyOntologyGraph
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrPolyOntologyGraphBuilder<E> implements OntologyGraphBuilder<TermId> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsrPolyOntologyGraphBuilder.class);

  /**
   * Create the builder using given type for storing the edge values.
   */
  public static <E> CsrPolyOntologyGraphBuilder<E> builder(Class<E> clz) {
    DataIndexer<E> indexer = indexerForClass(Objects.requireNonNull(clz));
    return new CsrPolyOntologyGraphBuilder<>(indexer);
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

  private CsrPolyOntologyGraphBuilder(DataIndexer<E> indexer) {
    this.indexer = Objects.requireNonNull(indexer);
  }

  /**
   * Set the {@code relationType} to be used as the main hierarchy.
   * <p>
   * {@link RelationTypes#isA()} by default.
   */
  @Override
  public CsrPolyOntologyGraphBuilder<E> hierarchyRelation(RelationType relationType) {
    if (relationType == null)
      LOGGER.warn("Hierarchy relation type must not be null. Skipping..");
    else
      this.hierarchyRelation = relationType;
    return this;
  }

  @Override
  public CsrPolyOntologyGraph<TermId, E> build(TermId root, Collection<? extends OntologyGraphEdge<TermId>> edges) {
    List<RelationType> relationTypes = edges.stream()
      .map(OntologyGraphEdge::relationType)
      .distinct()
      .collect(Collectors.toList());
    RelationCodec<E> codec = RelationCodec.of(relationTypes, indexer);
    int maxIndex = codec.maxIdx();
    if (maxIndex > indexer.maxIdx())
      throw new InsufficientWidthException(String.format("Unable to encode %d relation types using %d slots",
        maxIndex, indexer.maxIdx()));

    LOGGER.debug("Sorting graph nodes");
    List<TermId> nodes = edges.stream()
      .flatMap(e -> Stream.of(e.subject(), e.object()))
      .sorted(TermId::compareTo)
      .distinct()
      .collect(Collectors.toList());

    LOGGER.debug("Building the adjacency matrix");
    CsrData<E> csrData = makeCsrData(nodes, edges, codec);
    StaticCsrArray<E> adjacencyMatrix = new StaticCsrArray<>(csrData.getIndptr(), csrData.getIndices(), csrData.getData());

    Predicate<E> isParentOf = e -> codec.isSet(e, hierarchyRelation, false);
    Predicate<E> isChildOf = e -> codec.isSet(e, hierarchyRelation, true);

    LOGGER.debug("Assembling the ontology graph");
    return new CsrPolyOntologyGraph<>(root, nodes, adjacencyMatrix, TermId::compareTo, isParentOf, isChildOf);
  }

  private CsrData<E> makeCsrData(List<TermId> nodes,
                                 Collection<? extends OntologyGraphEdge<TermId>> edges,
                                 RelationCodec<E> codec) {
    List<Integer> indptr = new ArrayList<>();
    indptr.add(0);
    List<Integer> indices = new ArrayList<>();
    List<E> data = new ArrayList<>();

    Map<Integer, List<OntologyGraphEdge<TermId>>> adjacentEdges = Util.findAdjacentEdges(nodes, edges);

    CsrRowBuilder<E> row = new CsrRowBuilder<>(indexer);
    for (int rowIdx = 0; rowIdx < nodes.size(); rowIdx++) {
      TermId source = nodes.get(rowIdx);
      List<OntologyGraphEdge<TermId>> adjacent = adjacentEdges.getOrDefault(rowIdx, List.of());

      for (OntologyGraphEdge<TermId> edge : adjacent) {
        boolean inverted = source.equals(edge.object());
        TermId target = inverted ? edge.subject() : edge.object();

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

    return new CsrData<>(Util.toIntArray(indptr), Util.toIntArray(indices), data);
  }

}
