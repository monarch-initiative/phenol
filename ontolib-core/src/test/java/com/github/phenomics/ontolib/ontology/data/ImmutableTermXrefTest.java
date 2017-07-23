package com.github.phenomics.ontolib.ontology.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermXref;

public class ImmutableTermXrefTest {

  ImmutableTermId termId;
  ImmutableTermXref termXref;

  @Before
  public void setUp() {
    termId = ImmutableTermId.constructWithPrefix("HP:0000001");
    termXref = new ImmutableTermXref(termId, "Some description");
  }

  @Test
  public void test() {
    assertEquals("ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001]",
        termXref.getId().toString());
    assertEquals("Some description", termXref.getDescription());
    assertEquals(
        "ImmutableTermXref [id=ImmutableTermId [prefix=ImmutableTermPrefix [value=HP], id=0000001], description=Some description]",
        termXref.toString());
  }

}
