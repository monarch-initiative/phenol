package org.monarchinitiative.phenol.graph.csr.mono;

import org.monarchinitiative.phenol.graph.*;
import org.monarchinitiative.phenol.graph.csr.util.Util;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builder for {@link CsrMonoOntologyGraphBuilder}.
 *
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrMonoOntologyGraphBuilder implements OntologyGraphBuilder<TermId> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsrMonoOntologyGraphBuilder.class);

  private RelationType hierarchyRelation = RelationTypes.isA();

  /**
   * Create the builder.
   */
  public static CsrMonoOntologyGraphBuilder builder() {
    return new CsrMonoOntologyGraphBuilder();
  }

  @Override
  public OntologyGraphBuilder<TermId> hierarchyRelation(RelationType relationType) {
    if (relationType == null)
      LOGGER.warn("Hierarchy relation type must not be null. Skipping..");
    else
      this.hierarchyRelation = relationType;
    return this;
  }

  @Override
  public CsrMonoOntologyGraph<TermId> build(TermId root, Collection<? extends OntologyGraphEdge<TermId>> edges) {
    LOGGER.debug("Extracting edges with target hierarchy relation {}", hierarchyRelation.label());
    List<? extends OntologyGraphEdge<TermId>> hierarchyEdges = edges.stream()
      .filter(e -> e.relationType().equals(hierarchyRelation))
      .collect(Collectors.toList());

    LOGGER.debug("Sorting graph nodes");
    TermId[] nodes = edges.stream()
      .flatMap(e -> Stream.of(e.subject(), e.object()))
      .distinct()
      .sorted(TermId::compareTo)
      .toArray(TermId[]::new);

    CsrData<TermId> csrData = makeCsrData(nodes, hierarchyEdges);
    Map<TermId, Integer> nodeToIdx = new HashMap<>();
    for (int i = 0; i < nodes.length; i++) {
      TermId node = nodes[i];
      nodeToIdx.put(node, i);
    }

    return new CsrMonoOntologyGraph<>(root, nodeToIdx, csrData.getParents(), csrData.getChildren());
  }

  private CsrData<TermId> makeCsrData(TermId[] nodes,
                                      Collection<? extends OntologyGraphEdge<TermId>> edges) {
    Map<Integer, List<OntologyGraphEdge<TermId>>> adjacentEdges = Util.findAdjacentEdges(nodes, edges);

    List<Integer> parentIndptr = new ArrayList<>();
    parentIndptr.add(0);
    List<TermId> parents = new ArrayList<>();

    List<Integer> childIndptr = new ArrayList<>();
    childIndptr.add(0);
    List<TermId> children = new ArrayList<>();

    for (int rowIdx = 0; rowIdx < nodes.length; rowIdx++) {
      TermId source = nodes[rowIdx];
      List<OntologyGraphEdge<TermId>> adjacent = adjacentEdges.getOrDefault(rowIdx, List.of());

      for (OntologyGraphEdge<TermId> edge : adjacent) {
        // `inverted == true` if `src == subject` (child) and `object` is parent.
        boolean targetIsChild = source.equals(edge.object());
        TermId target = targetIsChild ? edge.subject() : edge.object();
        if (targetIsChild) {
          // edge where `subject` is child and `object` is parent.
          children.add(target);
        } else {
          // edge where `subject` is parent and `object` is child.
          parents.add(target);
        }
      }

      parentIndptr.add(parents.size());
      childIndptr.add(children.size());
    }

    StaticCsrArray<TermId> parentsArray = new StaticCsrArray<>(Util.toIntArray(parentIndptr), parents.toArray(new TermId[0]));
    StaticCsrArray<TermId> childrenArray = new StaticCsrArray<>(Util.toIntArray(childIndptr), children.toArray(new TermId[0]));

    return new CsrData<>(parentsArray, childrenArray);
  }

}
