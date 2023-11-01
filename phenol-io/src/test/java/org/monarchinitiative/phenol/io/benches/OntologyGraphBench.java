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
  public void poly_getParents(CsrPolyOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      blackhole.consume(ontology.ontology.graph().getParents(termId, false));
    }
  }

  @Benchmark
  public void poly_getChildren(CsrPolyOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      blackhole.consume(ontology.ontology.graph().getChildren(termId, false));
    }
  }

  @Benchmark
  public void mono_getParents(CsrMonoOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      blackhole.consume(ontology.ontology.graph().getParents(termId, false));
    }
  }

  @Benchmark
  public void mono_getChildren(CsrMonoOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      blackhole.consume(ontology.ontology.graph().getChildren(termId, false));
    }
  }

}
