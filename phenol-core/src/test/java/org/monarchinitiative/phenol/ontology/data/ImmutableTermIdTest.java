package org.monarchinitiative.phenol.ontology.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImmutableTermIdTest {

  private final TermId termId = TermId.of("HP:0000001");
  private final TermId termId2 = TermId.of("HP:0000002");

  @Test
  public void testStaticConstructMethod() {
    TermId otherId = TermId.of("HP:0000001");
    assertEquals("HP:0000001", otherId.toString());
    assertEquals(termId, otherId);
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
  }

  @Test
  public void testComparable() {
    assertEquals(0, termId.compareTo(termId));
    assertThat(termId.compareTo(termId2), lessThan(0));
    assertThat(termId2.compareTo(termId), greaterThan(0));
  }

  @Test
  public void testEquals() {
    assertTrue(termId.equals(termId));
    assertFalse(termId.equals(termId2));
  }

  @Test
  public void testQueryFunctions() {
    assertEquals("HP", termId.getPrefix());
    assertEquals("0000001", termId.getId());
    assertEquals("HP:0000001", termId.getValue());
  }

  @Test
  public void testToString() {
    assertEquals("HP:0000001", termId.toString());
  }
}
