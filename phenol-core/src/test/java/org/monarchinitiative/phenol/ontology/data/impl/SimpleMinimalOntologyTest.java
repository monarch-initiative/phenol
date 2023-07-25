package org.monarchinitiative.phenol.ontology.data.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class SimpleMinimalOntologyTest {

  private static final RelationshipType IS_A = RelationshipType.IS_A;

  @Nested
  public class OutcomeOfNormalBuild {

    TermId T1 = TermId.of("HP:1");
    TermId T01 = TermId.of("HP:01");
    TermId T010 = TermId.of("HP:010");
    TermId T011 = TermId.of("HP:011");
    TermId T0110 = TermId.of("HP:0110");

    TermId T02 = TermId.of("HP:02");
    TermId T020 = TermId.of("HP:020");
    TermId T021 = TermId.of("HP:021");
    TermId T022 = TermId.of("HP:022");
    TermId T03 = TermId.of("HP:03");

    private final SimpleMinimalOntology ontology = makeSimpleMinimalOntology();

    @Test
    public void weHaveVersion() {
      assertThat(ontology.version().isPresent(), equalTo(true));
      assertThat(ontology.version().get(), equalTo("1.2.3"));
    }

    @Test
    public void root() {
      assertThat(ontology.getRootTermId(), equalTo(TermId.of("HP:1")));
    }

    @Test
    public void nonObsoleteTermIds() {
      List<TermId> termIds = new ArrayList<>();
      ontology.nonObsoleteTermIds().forEach(termIds::add);
      assertThat(termIds, containsInAnyOrder(T1, T01, T010, T011, T0110, T02, T020, T021, T022, T03));
      assertThat(termIds, hasSize(10));
    }

    @Test
    public void obsoleteTermIds() {
      List<TermId> termIds = new ArrayList<>();
      ontology.obsoleteTermIds().forEach(termIds::add);
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
      ontology.allTermIds().forEach(termIds::add);
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
      Optional<Term> t01opt = ontology.termForTermId(T01);
      assertThat(t01opt.isPresent(), equalTo(true));
      Term t01 = t01opt.get();
      assertThat(t01.id(), equalTo(T01));
      assertThat(t01.getName(), equalTo("T01"));
      assertThat(t01.getDefinition(), equalTo("some definition T01"));
      assertThat(t01.isObsolete(), equalTo(false));

      // lookup by obsolete term ID works
      Optional<Term> t02opt = ontology.termForTermId(TermId.of("HP:02111"));
      assertThat(t02opt.isPresent(), equalTo(true));
      Term t02 = t02opt.get();
      assertThat(t02.id(), equalTo(T02));
      assertThat(t02.getName(), equalTo("T02"));
      assertThat(t02.isObsolete(), equalTo(false));
    }

    @Test
    public void getChildren() {
      List<TermId> children = new ArrayList<>();
      ontology.graph().getChildren(T1, true).forEach(children::add);
      assertThat(children, containsInAnyOrder(T1, T01, T02, T03));
    }

    @Test
    public void getDescendants() {
      List<TermId> children = new ArrayList<>();
      ontology.graph().getDescendants(T1, false).forEach(children::add);
      assertThat(children, containsInAnyOrder(T01, T010, T011, T0110, T02, T020, T021, T022, T03));
    }

    @Test
    public void getTerms() {
      assertThat(ontology.getTerms().stream().map(Term::id).collect(Collectors.toList()),
        containsInAnyOrder(T1, T01, T010, T011, T0110, T02, T020, T021, T022, T03));
      assertThat(ontology.getTerms().stream().noneMatch(Term::isObsolete), equalTo(true));
    }

    private SimpleMinimalOntology makeSimpleMinimalOntology() {

      List<Relationship> relationships = List.of(
        new Relationship(T01, T1, 0, IS_A),
        new Relationship(T010, T01, 1, IS_A),
        new Relationship(T011, T01, 2, IS_A),
        // Multi-parent
        new Relationship(T0110, T010, 3, IS_A),
        new Relationship(T0110, T011, 4, IS_A),
        new Relationship(T02, T1, 5, IS_A),
        new Relationship(T020, T02, 6, IS_A),
        new Relationship(T021, T02, 7, IS_A),
        new Relationship(T022, T02, 8, IS_A),
        new Relationship(T03, T1, 9, IS_A)
      );

      List<Term> terms = List.of(
        Term.builder(T1).name("T1").definition("some definition T1").build(),
        Term.builder(T01).name("T01").definition("some definition T01").build(),
        Term.builder(T010).name("T010").definition("some definition T010")
          .altTermIds(makeTermIds("HP:010111", "HP:010222"))
          .build(),
        Term.builder(T011).name("T011").definition("some definition T011").build(),
        Term.builder(T0110).name("T0110").definition("some definition T0110").build(),
        Term.builder(T02).name("T02").definition("some definition T02")
          .altTermIds(makeTermIds("HP:02111", "HP:02222"))
          .build(),
        Term.builder(TermId.of("HP:02111")).name("Whatever")
          .definition("HP:02111 won't make it due to being obsolete").obsolete(true).build(),
        Term.builder(T020).name("T020").definition("some definition T020").build(),
        Term.builder(T021).name("T021").definition("some definition T021").build(),
        Term.builder(T022).name("T022").definition("some definition T022").build(),
        Term.builder(T03).name("T03").definition("some definition T03").build()
      );
      return SimpleMinimalOntology.builder()
        .metaInfo(Map.of("release", "1.2.3"))
        .hierarchyRelationshipType(IS_A)
        .terms(terms)
        .relationships(relationships)
        .build();
    }

  }



  private static List<TermId> makeTermIds(String... values) {
    return Arrays.stream(values)
      .map(TermId::of)
      .collect(Collectors.toList());
  }
}
