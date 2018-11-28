package org.monarchinitiative.phenol.formats.mpo;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MpAnnotationTest {

  /** Test the toString method -- todo, we should prettify this output! */
  @Test
  public void testAnnotationTypeString() {
    TermId mpId = TermId.of("MP:123");
    List<String> pmids = ImmutableList.of();
    MpAnnotation.Builder builder = new MpAnnotation.Builder(mpId,pmids).sex(MpSex.FEMALE);
    MpAnnotation annot = builder.build();
    String expected = "MP:123FEMALE_SPECIFIC";
    assertEquals(expected,annot.toString());
    //System.out.println("Annotation="+annot);
  }

}
