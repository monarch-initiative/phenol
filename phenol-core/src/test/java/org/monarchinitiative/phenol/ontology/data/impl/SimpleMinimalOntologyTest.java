package org.monarchinitiative.phenol.ontology.data.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.monarchinitiative.phenol.ontology.data.impl.TestData.*;

public class SimpleMinimalOntologyTest {

  @Nested
  public class OutcomeOfNormalBuild {

    @Test
    public void weHaveVersion() {
      assertThat(SIMPLE_MINIMAL_ONTOLOGY.version().isPresent(), equalTo(true));
      assertThat(SIMPLE_MINIMAL_ONTOLOGY.version().get(), equalTo("1.2.3"));
    }

    @Test
    public void root() {
      assertThat(SIMPLE_MINIMAL_ONTOLOGY.getRootTermId(), equalTo(TermId.of("HP:1")));
    }

    @Test
    public void nonObsoleteTermIds() {
      List<TermId> termIds = new ArrayList<>();
      SIMPLE_MINIMAL_ONTOLOGY.nonObsoleteTermIds().forEach(termIds::add);
      assertThat(termIds, containsInAnyOrder(T1, T01, T010, T011, T0110, T02, T020, T021, T022, T03));
      assertThat(termIds, hasSize(10));
    }

    @Test
    public void obsoleteTermIds() {
      List<TermId> termIds = new ArrayList<>();
      SIMPLE_MINIMAL_ONTOLOGY.obsoleteTermIds().forEach(termIds::add);
      assertThat(termIds, containsInAnyOrder(
        TermId.of("HP:010111"),
        TermId.of("HP:010222"),
        TermId.of("HP:02111"),
        TermId.of("HP:02222")));
      assertThat(termIds, hasSize(4));
    }

    @Test
    public void allTermIds() {
      List<TermId> termIds = new ArrayList<>();
      SIMPLE_MINIMAL_ONTOLOGY.allTermIds().forEach(termIds::add);
      // Primary
      assertThat(termIds, hasItems(T1, T01, T010, T011, T0110, T02, T020, T021, T022, T03));
      // Obsolete
      assertThat(termIds, hasItems(
        TermId.of("HP:010111"),
        TermId.of("HP:010222"),
        TermId.of("HP:02111"),
        TermId.of("HP:02222")));
      // Total of 14 term IDs
      assertThat(termIds, hasSize(14));
    }

    @Test
    public void termByTermId() {
      Optional<Term> t01opt = SIMPLE_MINIMAL_ONTOLOGY.termForTermId(T01);
      assertThat(t01opt.isPresent(), equalTo(true));
      Term t01 = t01opt.get();
      assertThat(t01.id(), equalTo(T01));
      assertThat(t01.getName(), equalTo("T01"));
      assertThat(t01.getDefinition(), equalTo("some definition T01"));
      assertThat(t01.isObsolete(), equalTo(false));

      // lookup by obsolete term ID works
      Optional<Term> t02opt = SIMPLE_MINIMAL_ONTOLOGY.termForTermId(TermId.of("HP:02111"));
      assertThat(t02opt.isPresent(), equalTo(true));
      Term t02 = t02opt.get();
      assertThat(t02.id(), equalTo(T02));
      assertThat(t02.getName(), equalTo("T02"));
      assertThat(t02.isObsolete(), equalTo(false));
    }

    @Test
    public void getChildren() {
      List<TermId> children = new ArrayList<>();
      SIMPLE_MINIMAL_ONTOLOGY.graph().getChildren(T1, true).forEach(children::add);
      assertThat(children, containsInAnyOrder(T1, T01, T02, T03));
    }

    @Test
    public void getDescendants() {
      List<TermId> children = new ArrayList<>();
      SIMPLE_MINIMAL_ONTOLOGY.graph().getDescendants(T1, false).forEach(children::add);
      assertThat(children, containsInAnyOrder(T01, T010, T011, T0110, T02, T020, T021, T022, T03));
    }

    @Test
    public void getTerms() {
      assertThat(SIMPLE_MINIMAL_ONTOLOGY.getTerms().stream().map(Term::id).collect(Collectors.toList()),
        containsInAnyOrder(T1, T01, T010, T011, T0110, T02, T020, T021, T022, T03));
      assertThat(SIMPLE_MINIMAL_ONTOLOGY.getTerms().stream().noneMatch(Term::isObsolete), equalTo(true));
    }

    @Test
    public void subOntology() {
      MinimalOntology subOntology = SIMPLE_MINIMAL_ONTOLOGY.subOntology(T02);

      assertThat(subOntology.getRootTermId(), equalTo(T02));
      assertThat(subOntology.graph().root(), equalTo(T02));

      List<TermId> subTerms = subOntology.getTerms().stream()
        .map(Term::id)
        .sorted()
        .collect(Collectors.toList());
      assertThat(subTerms, equalTo(List.of(T02, T020, T021, T022)));

      // TODO - test relationships

      assertThat(subOntology.getMetaInfo(), equalTo(Map.of(
        "provenance", "Ontology created as a subset from original ontology with root HP:02",
        "release", "1.2.3")));
    }

  }

}
