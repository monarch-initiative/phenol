package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class IteratorTestBase {

  protected static List<TermId> makeTermIds(String... values) {
    return Arrays.stream(values)
      .map(TermId::of)
      .collect(Collectors.toList());
  }

  protected static Term makeTerm(String primary, List<String> alternative) {
    Term.Builder builder = Term.builder(TermId.of(primary))
      .name("Whatever");
    List<TermId> alternates = alternative.stream()
      .map(TermId::of)
      .collect(Collectors.toList());

    return builder
      .altTermIds(alternates)
      .build();
  }
}
