package org.monarchinitiative.phenol.ontology.data.impl;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class AllTermIdIteratorTest extends IteratorTestBase {

  @Test
  public void normal() {
    List<Term> terms = List.of(
      makeTerm("HP:1", List.of("HP:10", "HP:11", "HP:12")),
      makeTerm("HP:2", List.of()),
      makeTerm("HP:3", List.of("HP:30", "HP:31"))
    );

    List<TermId> items = new ArrayList<>();
    new AllTermIdIterator(terms.iterator())
      .forEachRemaining(items::add);

    assertThat(items, equalTo(makeTermIds("HP:1", "HP:10", "HP:11", "HP:12", "HP:2", "HP:3", "HP:30", "HP:31")));
  }

  @Test
  public void normal_noAltTerms() {
    List<Term> terms = List.of(
      makeTerm("HP:1", List.of()),
      makeTerm("HP:2", List.of()),
      makeTerm("HP:3", List.of())
    );

    List<TermId> items = new ArrayList<>();
    new AllTermIdIterator(terms.iterator())
      .forEachRemaining(items::add);

    assertThat(items, equalTo(makeTermIds("HP:1", "HP:2", "HP:3")));
  }

  @Test
  public void normal_noTermsWhatsoever() {
    List<Term> terms = List.of();

    List<TermId> items = new ArrayList<>();
    //noinspection RedundantOperationOnEmptyContainer
    new AllTermIdIterator(terms.iterator())
      .forEachRemaining(items::add);

    assertThat(items, is(emptyCollectionOf(TermId.class)));
  }


}
