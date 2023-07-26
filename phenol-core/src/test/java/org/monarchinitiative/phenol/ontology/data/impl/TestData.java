package org.monarchinitiative.phenol.ontology.data.impl;

import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TestData {

  static final RelationshipType IS_A = RelationshipType.IS_A;
  static final TermId T1 = TermId.of("HP:1");
  static final TermId T01 = TermId.of("HP:01");
  static final TermId T010 = TermId.of("HP:010");
  static final TermId T011 = TermId.of("HP:011");
  static final TermId T0110 = TermId.of("HP:0110");

  static final TermId T02 = TermId.of("HP:02");
  static final TermId T020 = TermId.of("HP:020");
  static final TermId T021 = TermId.of("HP:021");
  static final TermId T022 = TermId.of("HP:022");
  static final TermId T03 = TermId.of("HP:03");

  static final List<Relationship> RELATIONSHIPS = List.of(
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

  static final SimpleMinimalOntology SIMPLE_MINIMAL_ONTOLOGY = makeSimpleMinimalOntology();

  private TestData() {}

  static SimpleMinimalOntology makeSimpleMinimalOntology() {
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
      .relationships(RELATIONSHIPS)
      .build();
  }

  private static List<TermId> makeTermIds(String... values) {
    return Arrays.stream(values)
      .map(TermId::of)
      .collect(Collectors.toList());
  }

}
