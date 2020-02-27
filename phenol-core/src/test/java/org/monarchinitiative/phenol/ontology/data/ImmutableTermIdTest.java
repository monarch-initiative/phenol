package org.monarchinitiative.phenol.ontology.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;


class ImmutableTermIdTest {

  private final TermId termId = TermId.of("HP:0000001");
  private final TermId termId2 = TermId.of("HP:0000002");

  @Test
  void testStaticConstructMethod() {
    TermId otherId = TermId.of("HP:0000001");
    assertEquals("HP:0000001", otherId.toString());
    assertEquals(termId, otherId);
    assertThrows(PhenolRuntimeException.class, () -> TermId.of(null));
    assertThrows(PhenolRuntimeException.class, () -> TermId.of(""));
  }

  @Test
  void testStaticOfConstructorFullId() {
    TermId otherId = TermId.of("HP:0000001");
    assertEquals(termId, otherId);
  }

  @Test
  void testStaticOfConstructorPrefixAndId() {
    TermId otherId = TermId.of("HP", "0000001");
    assertEquals(termId, otherId);
    assertThrows(PhenolRuntimeException.class, () -> TermId.of(null, "0000000"));
    assertThrows(PhenolRuntimeException.class, () -> TermId.of("", "0000000"));

    assertThrows(PhenolRuntimeException.class, () -> TermId.of("HP", null));
    assertThrows(PhenolRuntimeException.class, () -> TermId.of("HP", ""));
  }

  @Test
  void testComparable() {
    assertEquals(0, termId.compareTo(termId));
    assertThat(termId.compareTo(termId2), lessThan(0));
    assertThat(termId2.compareTo(termId), greaterThan(0));
  }

  @Test
  void testEquals() {
    assertEquals(termId, termId);
    assertNotEquals(termId, termId2);
  }

  @Test
  void testQueryFunctions() {
    assertEquals("HP", termId.getPrefix());
    assertEquals("0000001", termId.getId());
    assertEquals("HP:0000001", termId.getValue());
  }

  @Test
  void testToString() {
    assertEquals("HP:0000001", termId.toString());
  }
}
