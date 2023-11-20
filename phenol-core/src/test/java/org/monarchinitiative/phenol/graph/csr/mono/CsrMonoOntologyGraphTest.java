package org.monarchinitiative.phenol.graph.csr.mono;

import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.OntologyGraphEdges;
import org.monarchinitiative.phenol.graph.OntologyGraphTest;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Check that we can use {@link CsrMonoOntologyGraph} as an {@link OntologyGraph}.
 */
public class CsrMonoOntologyGraphTest extends OntologyGraphTest {

  @Override
  protected CsrMonoOntologyGraph<TermId> getGraph() {
    CsrMonoOntologyGraphBuilder builder = CsrMonoOntologyGraphBuilder.builder();
    return builder.build(OntologyGraphEdges.HP1, OntologyGraphEdges.hierarchyEdges());
  }

}
