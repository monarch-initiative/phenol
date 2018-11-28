package org.monarchinitiative.phenol.ontology.data;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ImmutableTermSynonymTest {

  private TermSynonym termSynonym;

  @Before
  public void setUp() {
    termSynonym =
        new TermSynonym(
            "synonym",
            TermSynonymScope.EXACT,
            "BRITISH_ENGLISH",
            ImmutableList.of(new TermXref(TermId.of("HP:0000001"),"term description")));
  }

  @Test
  public void testQueryFunctions() {
    assertEquals("synonym", termSynonym.getValue());
    assertEquals(TermSynonymScope.EXACT, termSynonym.getScope());
    assertEquals("BRITISH_ENGLISH", termSynonym.getSynonymTypeName());
    assertEquals(
        "[ImmutableTermXref [id=HP:0000001, description=term description]]",
        termSynonym.getTermXrefs().toString());
    assertEquals(
        "TermSynonym [value=synonym, scope=EXACT, synonymTypeName=BRITISH_ENGLISH, termXrefs=[ImmutableTermXref [id=HP:0000001, description=term description]]]",
        termSynonym.toString());
  }
}
