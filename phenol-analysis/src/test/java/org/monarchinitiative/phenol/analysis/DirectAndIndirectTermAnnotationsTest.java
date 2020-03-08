package org.monarchinitiative.phenol.analysis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectAndIndirectTermAnnotationsTest {

  static TermId anophthalmia;
  static TermId abnormalityGlobeDSize;

  @BeforeAll
  static void init() {
    anophthalmia = TermId.of("HP:0000528");
    abnormalityGlobeDSize = TermId.of(("HP:0100887"));
  }

  @Test
  void testDirectIndirect() {
    DirectAndIndirectTermAnnotations tannots = new DirectAndIndirectTermAnnotations();
    tannots.addGeneAnnotationDirect(anophthalmia);
    tannots.addGeneAnnotationTotal(abnormalityGlobeDSize);
    assertEquals(1, tannots.directAnnotatedCount());
    assertEquals(2, tannots.totalAnnotatedCount());
  }
}
