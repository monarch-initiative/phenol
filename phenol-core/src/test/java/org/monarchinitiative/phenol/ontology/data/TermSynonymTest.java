package org.monarchinitiative.phenol.ontology.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TermSynonymTest {

  private TermSynonym termSynonym;

  @BeforeEach
  public void setUp() {
    termSynonym =
        new TermSynonym(
            "synonym",
            TermSynonymScope.EXACT,
            "BRITISH_ENGLISH",
            ImmutableList.of(new TermXref(TermId.of("HP:0000001"),"term description")),
          "http://purl.obolibrary.org/obo/hp.obo#layperson");
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

  @Test
  public void testLaypersonLabel() {
    assertTrue(termSynonym.isLayperson());
  }
}
