package com.github.phenomics.ontolib.ontology.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermPrefix;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermSynonym;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermXref;
import com.github.phenomics.ontolib.ontology.data.TermSynonymScope;
import com.google.common.collect.Lists;

public class ImmutableTermSynonymTest {

  ImmutableTermSynonym termSynonym;

  @Before
  public void setUp() {
    termSynonym =
        new ImmutableTermSynonym("synonym", TermSynonymScope.EXACT, "BRITISH_ENGLISH",
            Lists.newArrayList(
                new ImmutableTermXref(new ImmutableTermId(new ImmutableTermPrefix("HP"), "0000001"),
                    "term description")));
  }

  @Test
  public void testQueryFunctions() {
    assertEquals("synonym", termSynonym.getValue());
    assertEquals(TermSynonymScope.EXACT, termSynonym.getScope());
    assertEquals("BRITISH_ENGLISH", termSynonym.getSynonymTypeName());
    assertEquals(
        "[ImmutableTermXref [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], description=term description]]",
        termSynonym.getTermXrefs().toString());
    assertEquals(
        "ImmutableTermSynonym [value=synonym, scope=EXACT, synonymTypeName=BRITISH_ENGLISH, termXrefs=[ImmutableTermXref [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], description=term description]]]",
        termSynonym.toString());
  }

}
