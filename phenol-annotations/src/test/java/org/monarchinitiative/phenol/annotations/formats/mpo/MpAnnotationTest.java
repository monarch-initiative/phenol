package org.monarchinitiative.phenol.annotations.formats.mpo;


import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MpAnnotationTest {

  /** Test the toString method -- todo, we should prettify this output! */
  @Test
  public void testAnnotationTypeString() {
    TermId mpId = TermId.of("MP:123");
    Set<String> pmids = ImmutableSet.of();
    MpAnnotation.Builder builder = new MpAnnotation.Builder(mpId,pmids).sexSpecific(MpSex.FEMALE);
    MpAnnotation annot = builder.build();
    String expected = "MP:123FEMALE_SPECIFIC_ABNORMAL";
    assertEquals(expected,annot.toString());
    //System.out.println("Annotation="+annot);
  }

}
