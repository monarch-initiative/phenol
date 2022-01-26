package org.monarchinitiative.phenol.analysis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectAndIndirectTermAnnotationsTest {

  static TermId anophthalmia;
  static TermId abnormalityGlobeDSize;

  @BeforeAll
  public static void init() {
    anophthalmia = TermId.of("HP:0000528");
    abnormalityGlobeDSize = TermId.of(("HP:0100887"));
  }

  @Test
  public void testDirectIndirect() {
    //TODO Mock the ontology class and get ancesters method call.
    // these are the direct annotations were are putting in
    Set<TermId> totalAnnotations = new HashSet<>();
    totalAnnotations.add(anophthalmia);
    totalAnnotations.add(abnormalityGlobeDSize);
    Set<TermId> directAnnotations = new HashSet<>();
    directAnnotations.add(anophthalmia);

//    DirectAndIndirectTermAnnotations tannots = new DirectAndIndirectTermAnnotations();
////    tannots.addGeneAnnotationDirect(anophthalmia);
////    tannots.addGeneAnnotationTotal(abnormalityGlobeDSize);
//    assertEquals(1, tannots.directAnnotatedCount());
//    assertEquals(2, tannots.totalAnnotatedCount());
  }
}
