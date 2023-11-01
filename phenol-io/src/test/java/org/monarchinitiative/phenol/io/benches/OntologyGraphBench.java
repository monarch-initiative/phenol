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

  REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
  why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
  experiments, perform baseline and negative tests that provide experimental control, make sure
  the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
  Do not assume the numbers tell you what you want them to tell.

  NOTE: Current JVM experimentally supports Compiler Blackholes, and they are in use. Please exercise
  extra caution when trusting the results, look into the generated code to check the benchmark still
  works, and factor in a small probability of new VM bugs. Additionally, while comparisons between
  different JVMs are already problematic, the performance difference caused by different Blackhole
  modes can be very significant. Please make sure you use the consistent Blackhole mode for comparisons.

  Benchmark                             Mode  Cnt    Score    Error  Units
  OntologyGraphBench.mono_getChildren  thrpt   25  163.773 ± 11.709  ops/s
  OntologyGraphBench.mono_getParents   thrpt   25  149.663 ±  8.890  ops/s
  OntologyGraphBench.poly_getChildren  thrpt   25  134.231 ±  7.379  ops/s
  OntologyGraphBench.poly_getParents   thrpt   25  131.846 ±  5.647  ops/s
   */
}
