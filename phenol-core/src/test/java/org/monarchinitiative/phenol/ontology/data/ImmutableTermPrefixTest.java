package org.monarchinitiative.phenol.ontology.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ImmutableTermPrefixTest {

  ImmutableTermPrefix termPrefix;
  ImmutableTermPrefix termPrefix2;

  @Before
  public void setUp() {
    termPrefix = new ImmutableTermPrefix("HP");
    termPrefix2 = new ImmutableTermPrefix("GO");
  }

  @Test
  public void testComparable() {
    assertEquals(0, termPrefix.compareTo(termPrefix));
    assertThat(termPrefix.compareTo(termPrefix2), greaterThan(0));
    assertThat(termPrefix2.compareTo(termPrefix), lessThan(0));
  }

  @Test
  public void testQueryFunctions() {
    assertEquals("HP", termPrefix.getValue());
  }

  @Test
  public void testToString() {
    assertEquals("ImmutableTermPrefix [value=HP]", termPrefix.toString());
  }


}
