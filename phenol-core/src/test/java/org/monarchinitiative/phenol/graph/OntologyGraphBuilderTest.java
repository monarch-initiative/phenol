package org.monarchinitiative.phenol.graph;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class OntologyGraphBuilderTest {

  private static final TermId ROOT = TermId.of("HP:1");

  @Test
  public void csrBuilder() {
    OntologyGraph<TermId> graph = OntologyGraphBuilders.csrBuilder(Byte.class)
      .hierarchyRelation(RelationTypes.isA())
      .build(ROOT, OntologyGraphEdges.makeHierarchyAndPartOfEdges());

    assertThat(graph.root(), equalTo(TermId.of("HP:1")));
    assertThat(graph.size(), equalTo(13));
  }
}
