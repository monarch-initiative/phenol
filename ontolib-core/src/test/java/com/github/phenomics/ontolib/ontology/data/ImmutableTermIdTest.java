package com.github.phenomics.ontolib.ontology.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermPrefix;

public class ImmutableTermIdTest {

  ImmutableTermPrefix termPrefix;
  ImmutableTermId termId;
  ImmutableTermId termId2;

  @Before
  public void setUp() {
    termPrefix = new ImmutableTermPrefix("HP");
    termId = new ImmutableTermId(termPrefix, "0000001");
    termId2 = new ImmutableTermId(termPrefix, "0000002");
  }

  @Test
  public void testStaticConstructMethod() {
    ImmutableTermId otherId = ImmutableTermId.constructWithPrefix("HP:0000001");
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]",
        otherId.toString());
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
    assertSame(termPrefix, termId.getPrefix());
    assertEquals("0000001", termId.getId());
    assertEquals("HP:0000001", termId.getIdWithPrefix());
  }

  @Test
  public void testToString() {
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]",
        termId.toString());
  }

}
