package org.monarchinitiative.phenol.io.benches;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmark different implementations of {@link org.monarchinitiative.phenol.graph.OntologyGraph}.
 */
@BenchmarkMode(Mode.Throughput)
public class OntologyGraphBench {

  @Benchmark
  public void phenol_getParents(PhenolOntologySetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      blackhole.consume(ontology.ontology.graph().getParents(termId, false));
    }
  }

  @Benchmark
  public void phenol_getChildren(PhenolOntologySetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      blackhole.consume(ontology.ontology.graph().getChildren(termId, false));
    }
  }

}
