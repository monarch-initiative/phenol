package org.monarchinitiative.phenol.ontology.data.impl;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class AltTermIdIteratorTest extends IteratorTestBase {

  @Test
  public void normal() {
    List<Term> terms = List.of(
      makeTerm("HP:0", List.of()),
      makeTerm("HP:1", List.of("HP:10", "HP:11", "HP:12")),
      makeTerm("HP:2", List.of()),
      makeTerm("HP:3", List.of("HP:30")),
      makeTerm("HP:4", List.of()),
      makeTerm("HP:5", List.of("HP:50", "HP:51")),
      makeTerm("HP:6", List.of())
    );

    List<TermId> items = new ArrayList<>();
    new AltTermIdIterator(terms.iterator())
      .forEachRemaining(items::add);

    assertThat(items, equalTo(makeTermIds("HP:10", "HP:11", "HP:12", "HP:30", "HP:50", "HP:51")));
  }

  @Test
  public void empty() {
    List<Term> terms = List.of();

    List<TermId> items = new ArrayList<>();
    //noinspection RedundantOperationOnEmptyContainer
    new AltTermIdIterator(terms.iterator())
      .forEachRemaining(items::add);

    assertThat(items, equalTo(makeTermIds()));
  }
}
