package org.monarchinitiative.phenol.ontology.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ImmutableTermXrefTest {

  private TermId termId;
  private TermXref termXref;

  @Before
  public void setUp() {
    termId = TermId.constructWithPrefix("HP:0000001");
    termXref = new TermXref(termId, "Some description");
  }

  @Test
  public void test() {
    assertEquals(
        "TermId [prefix=TermPrefix [value=HP], id=0000001]",
        termXref.getId().toString());
    assertEquals("Some description", termXref.getDescription());
    assertEquals(
        "ImmutableTermXref [id=TermId [prefix=TermPrefix [value=HP], id=0000001], description=Some description]",
        termXref.toString());
  }
}
