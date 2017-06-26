package de.charite.compbio.ontolib.ontology.data;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

public class ImmutableTermSynonymTest {

  ImmutableTermSynonym termSynonym;

  @Before
  public void setUp() {
    termSynonym = new ImmutableTermSynonym("synonym", TermSynonymScope.EXACT, "BRITISH_ENGLISH",
        Lists.newArrayList(new ImmutableTermXref(
            new ImmutableTermId(new ImmutableTermPrefix("HP"), 1), "term description")));
  }

  @Test
  public void testQueryFunctions() {
    assertEquals("synonym", termSynonym.getValue());
    assertEquals(TermSynonymScope.EXACT, termSynonym.getScope());
    assertEquals("BRITISH_ENGLISH", termSynonym.getSynonymTypeName());
    assertEquals(
        "[ImmutableTermXref [id=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=1], description=term description]]",
        termSynonym.getTermXrefs().toString());
    assertEquals(
        "ImmutableTermSynonym [value=synonym, scope=EXACT, synonymTypeName=BRITISH_ENGLISH, "
            + "termXrefs=[ImmutableTermXref [id=ImmutableTermId [prefix=ImmutableTermPrefix "
            + "[value=HP], id=1], description=term description]]]",
        termSynonym.toString());
  }

}
