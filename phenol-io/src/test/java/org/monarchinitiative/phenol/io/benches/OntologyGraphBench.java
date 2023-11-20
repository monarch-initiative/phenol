package org.monarchinitiative.phenol.io.benches;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmark different implementations of {@link org.monarchinitiative.phenol.graph.OntologyGraph}.
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2)
public class OntologyGraphBench {

  @Benchmark
  public void poly_getParents(CsrPolyOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getParents(termId, false)
        .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void poly_getChildren(CsrPolyOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getChildren(termId, false)
          .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void poly_getAncestors(CsrPolyOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getAncestors(termId, false)
        .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void poly_getDescendants(CsrPolyOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getDescendants(termId, false)
        .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void mono_getParents(CsrMonoOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getParents(termId, false)
        .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void mono_getChildren(CsrMonoOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getChildren(termId, false)
        .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void mono_getAncestors(CsrMonoOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getAncestors(termId, false)
        .forEach(blackhole::consume);
    }
  }

  @Benchmark
  public void mono_getDescendants(CsrMonoOntologyGraphSetup ontology, Blackhole blackhole) {
    for (TermId termId : ontology.primaryTermIds) {
      ontology.ontology.graph().getDescendants(termId, false)
        .forEach(blackhole::consume);
    }
  }

  /*
  # Run complete. Total time: 00:33:59
  Commit 6a470a0f

  Benchmark                             Mode  Cnt    Score    Error  Units
  OntologyGraphBench.mono_getChildren  thrpt   25  163.773 ± 11.709  ops/s
  OntologyGraphBench.mono_getParents   thrpt   25  149.663 ±  8.890  ops/s
  OntologyGraphBench.poly_getChildren  thrpt   25  134.231 ±  7.379  ops/s
  OntologyGraphBench.poly_getParents   thrpt   25  131.846 ±  5.647  ops/s


  # Run complete. Total time: 00:48:00
  Commit 474ffa74

  Benchmark                                Mode  Cnt    Score     Error  Units
  OntologyGraphBench.mono_getAncestors    thrpt   25   43.154 ±   0.660  ops/s
  OntologyGraphBench.mono_getChildren     thrpt   25  555.751 ± 129.691  ops/s
  OntologyGraphBench.mono_getDescendants  thrpt   25   37.129 ±   0.921  ops/s
  OntologyGraphBench.mono_getParents      thrpt   25  568.041 ±  45.601  ops/s
  OntologyGraphBench.poly_getAncestors    thrpt   25   18.252 ±   0.686  ops/s
  OntologyGraphBench.poly_getChildren     thrpt   25  117.166 ±  16.093  ops/s
  OntologyGraphBench.poly_getDescendants  thrpt   25   31.324 ±   1.547  ops/s
  OntologyGraphBench.poly_getParents      thrpt   25  124.328 ±  14.620  ops/s
  */
}
