package de.charite.compbio.ontolib.ontology.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class ImmutableTermIdTest {

  ImmutableTermPrefix termPrefix;
  ImmutableTermId termId;
  ImmutableTermId termId2;

  @Before
  public void setUp() {
    termPrefix = new ImmutableTermPrefix("HP");
    termId = new ImmutableTermId(termPrefix, 1);
    termId2 = new ImmutableTermId(termPrefix, 2);
  }

  @Test
  public void testStaticConstructMethod() {
    ImmutableTermId otherId = ImmutableTermId.constructWithPrefix("HP:0000001");
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1]", otherId.toString());
    assertEquals(termId, otherId);
  }

  @Test
  public void testComparable() {
    assertEquals(0, termId.compareTo(termId));
    assertThat(termId.compareTo(termId2), lessThan(0));
    assertThat(termId2.compareTo(termId), greaterThan(0));
  }
  
  @Test
  public void testQueryFunctions() {
    assertSame(termPrefix, termId.getPrefix());
    assertEquals(1, termId.getId());
    assertEquals("HP:0000001", termId.getIdWithPrefix());
  }
  
  @Test
  public void testToString() {
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=1]", termId.toString());
  }

}
