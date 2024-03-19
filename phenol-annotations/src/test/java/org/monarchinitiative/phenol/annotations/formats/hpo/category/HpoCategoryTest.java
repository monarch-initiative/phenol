package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Not too much to test for this class.
 */
@Disabled("until deprecation")
public class HpoCategoryTest {

  @Test
  public void testContructor() {
    TermId tid = TermId.of("FAKE:123");
    String label = "Fake category";
    HpoCategory hpocat = new HpoCategory(tid, label);
    assertEquals(0, hpocat.getNumberOfAnnotations());
    TermId t2 = TermId.of("FAKE_ANNOTATION:123");
    hpocat.addAnnotatedTerm(t2);
    assertEquals(1, hpocat.getNumberOfAnnotations());
  }
}
