package org.monarchinitiative.phenol.ontology.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ImmutableTermXrefTest {

  @Test
  void test() {
    TermId termId = TermId.of("HP:0000001");
    TermXref termXref = new TermXref(termId, "Some description");

    assertEquals(termId, termXref.getId());
    assertEquals("Some description", termXref.getDescription());
  }
}
