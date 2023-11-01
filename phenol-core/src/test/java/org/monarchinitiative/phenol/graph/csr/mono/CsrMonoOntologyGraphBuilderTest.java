package org.monarchinitiative.phenol.graph.csr.mono;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.graph.OntologyGraphEdges;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Check that {@link CsrMonoOntologyGraphBuilder} builds the expected {@link CsrMonoOntologyGraph} from known input.
 */
public class CsrMonoOntologyGraphBuilderTest {

  private static final TermId ROOT = OntologyGraphEdges.HP1;

  @Test
  public void build() {
    CsrMonoOntologyGraphBuilder builder = CsrMonoOntologyGraphBuilder.builder();
    CsrMonoOntologyGraph<TermId> graph = builder.build(ROOT, OntologyGraphEdges.hierarchyEdges());

    assertThat(graph, is(notNullValue(CsrMonoOntologyGraph.class)));
    assertThat(graph.root(), equalTo(ROOT));
  }

  @Test
  public void checkIndptrs() {
    CsrMonoOntologyGraphBuilder builder = CsrMonoOntologyGraphBuilder.builder();
    CsrMonoOntologyGraph<TermId> graph = builder.build(ROOT, OntologyGraphEdges.hierarchyEdges());

    assertThat(graph.getParentArray().getIndptr(), equalTo(new int[]{0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 10}));
    assertThat(graph.getChildArray().getIndptr(), equalTo(new int[]{0, 2, 3, 4, 4, 7, 7, 7, 7, 7, 10}));
  }

  @Test
  public void checkData() {
    CsrMonoOntologyGraphBuilder builder = CsrMonoOntologyGraphBuilder.builder();
    CsrMonoOntologyGraph<TermId> graph = builder.build(ROOT, OntologyGraphEdges.hierarchyEdges());

    /*
       Checking data is more tricky, since we must check that we have groups of term IDs in particular column.
       This is enough because The OntologyGraph API does not require us to provide child/parent termIDs in
       any particular order. So, we must not test the `StaticCsrArray.getData()` directly.
       Instead, we test if we find expected parents/children in the column.
     */

    String[][] expectedParents = {
      /* HP:01   */ {"HP:1"},
      /* HP:010  */ {"HP:01"},
      /* HP:011  */ {"HP:01"},
      /* HP:0110 */ {"HP:010", "HP:011"},
      /* HP:02   */ {"HP:1"},
      /* HP:020  */ {"HP:02"},
      /* HP:021  */ {"HP:02"},
      /* HP:022  */ {"HP:02"},
      /* HP:03   */ {"HP:1"},
      /* HP:1    */ {},
    };


    assertThatGroupsAreConsistent(graph.size(), graph.getParentArray(), expectedParents);

    String[][] expectedChildren = {
      /* HP:01   */ {"HP:010", "HP:011"},
      /* HP:010  */ {"HP:0110"},
      /* HP:011  */ {"HP:0110"},
      /* HP:0110 */ {},
      /* HP:02   */ {"HP:020", "HP:021", "HP:022"},
      /* HP:020  */ {},
      /* HP:021  */ {},
      /* HP:022  */ {},
      /* HP:03   */ {},
      /* HP:1    */ {"HP:01", "HP:02", "HP:03"},
    };
    assertThatGroupsAreConsistent(graph.size(), graph.getChildArray(), expectedChildren);
  }

  private void assertThatGroupsAreConsistent(int size,
                                             StaticCsrArray<TermId> array,
                                             String[][] expectedCuries) {
    int[] indptr = array.getIndptr();
    TermId[] data = array.getData();
    for (int i = 0; i < size; i++) {
      int start = indptr[i];
      int end = indptr[i + 1];
      String[] curies = expectedCuries[i];
      for (int j = start; j < end; j++) {
        String query = data[j].getValue();
        assertThat(curies, hasItemInArray(query));
      }
    }
  }
}
