package org.monarchinitiative.phenol.ontology.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.monarchinitiative.phenol.ontology.TestOntology;

import java.util.HashSet;
import java.util.Set;

public class TermIdsTest {

  private final Ontology ontology = TestOntology.ontology();
  private final TermId id1 = TestOntology.TERM_ID_1;

  @Test
  public void augmentWithAncestorsIncludeRoot() {
    assertEquals(
      Set.of(
        TermId.of("HP:0000001"),
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004"),
        TermId.of("HP:0000005")
      ),
      TermIds.augmentWithAncestors(ontology, new HashSet<>(Set.of(id1)), true));
  }

  @Test
  public void augmentWithAncestorsExcludeRoot() {
    assertEquals(TermId.of("HP:0000005"), ontology.getRootTermId());
    assertEquals(
      Set.of(
        TermId.of("HP:0000001"),
        TermId.of("HP:0000002"),
        TermId.of("HP:0000003"),
        TermId.of("HP:0000004")
      ),
      TermIds.augmentWithAncestors(ontology, new HashSet<>(Set.of(id1)), false));
  }
}
