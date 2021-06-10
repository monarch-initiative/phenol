package org.monarchinitiative.phenol.ontology.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SynonymTypeTest {


  @Test
  public void laypersonSynonymTypeTest() {
    // The following string gets passed to the ENUM if the OBO file has
    // synonym: "Eye disease" RELATED layperson []
    String layperson = "http://purl.obolibrary.org/obo/hp#layperson";
    assertEquals(SynonymType.LAYPERSON_TERM, SynonymType.fromString(layperson));
    layperson = "http://purl.obolibrary.org/obo/hp.obo#layperson";
    assertEquals(SynonymType.LAYPERSON_TERM, SynonymType.fromString(layperson));
  }
}
