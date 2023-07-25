package org.monarchinitiative.phenol.ontology;

import org.monarchinitiative.phenol.ontology.data.*;

import java.util.List;

/**
 * Test utility class for providing a pre-constructed {@link Ontology} and {@link TermId}
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class TestOntology {

  private static final Ontology ONTOLOGY;

  public static final TermId TERM_ID_1 = TermId.of("HP:0000001");
  public static final TermId TERM_ID_2 = TermId.of("HP:0000002");
  public static final TermId TERM_ID_3 = TermId.of("HP:0000003");
  public static final TermId TERM_ID_4 = TermId.of("HP:0000004");
  public static final TermId TERM_ID_5 = TermId.of("HP:0000005");

  static {
    List<Term> terms = List.of(
      Term.builder(TERM_ID_1)
        .name("term1")
        .definition("some definition 1")
        .build(),
      Term.builder(TERM_ID_2)
        .name("term2")
        .definition("some definition 2")
        .build(),
      Term.builder(TERM_ID_3)
        .name("term3")
        .definition("some definition 3")
        .build(),
      Term.builder(TERM_ID_4)
        .name("term4")
        .definition("some definition 4")
        .build(),
      Term.builder(TERM_ID_5)
        .name("term5")
        .definition("some definition 5")
        .build());

    // TODO: This is confusing as the root term here is TERM_ID_5 (HP:0000005).
    //  In the real HPO the root term is HP:0000001. The reason being that the relationships
    //  here are created backwards.
    List<Relationship> relationships = List.of(
      new Relationship(TERM_ID_1, TERM_ID_2, 1, RelationshipType.IS_A),
      new Relationship(TERM_ID_1, TERM_ID_3, 2, RelationshipType.IS_A),
      new Relationship(TERM_ID_1, TERM_ID_4, 3, RelationshipType.IS_A),
      new Relationship(TERM_ID_2, TERM_ID_5, 4, RelationshipType.IS_A),
      new Relationship(TERM_ID_3, TERM_ID_5, 5, RelationshipType.IS_A),
      new Relationship(TERM_ID_4, TERM_ID_5, 6, RelationshipType.IS_A)
    );

    ONTOLOGY = ImmutableOntology.builder()
      .terms(terms)
      .relationships(relationships)
      .build();
  }

  private TestOntology() {
  }

  public static Ontology ontology() {
    return ONTOLOGY;
  }

  public static TermId rootId() {
    return TERM_ID_5;
  }
}
