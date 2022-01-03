package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.0.2 (2017-11-24)
 */
public class HpoAnnotationTest {
  /** If no frequency is provided, the parser uses the default (100%) */
  @SuppressWarnings("unused")
  private static final HpoFrequency defaultFrequency = HpoFrequency.fromTermId(TermId.of("HP:0040280"));

  /** Different onset means the terms are not equal */
  @Test
  public void testEqualityOfTerms2() {
    TermId oxycephalyId = TermId.of("HP:0000263");
    HpoAnnotation oxycephaly1 =
        new HpoAnnotation.Builder(oxycephalyId).onset(HpoOnset.ADULT_ONSET).build();
    HpoAnnotation oxycephaly2 = new HpoAnnotation.Builder(oxycephalyId).build();
    assertNotEquals(oxycephaly1, oxycephaly2);
  }

  /** Different onset means the terms are not equal */
  @Test
  public void testEqualityOfTerms3() {
    TermId oxycephalyId = TermId.of("HP:0000263");
    HpoAnnotation oxycephaly1 =
        new HpoAnnotation.Builder(oxycephalyId).onset(HpoOnset.ADULT_ONSET).build();
    HpoAnnotation oxycephaly2 =
        new HpoAnnotation.Builder(oxycephalyId).onset(HpoOnset.CHILDHOOD_ONSET).build();
    assertNotEquals(oxycephaly1, oxycephaly2);
  }

  @Test
  public void testEqualityOfTerms4() {
    TermId oxycephalyId = TermId.of("HP:0000263");
    HpoAnnotation oxycephaly1 =
        new HpoAnnotation.Builder(oxycephalyId)
            .onset(HpoOnset.ADULT_ONSET)
            .frequency(HpoFrequency.ALWAYS_PRESENT.mean())
            .build();

    HpoAnnotation oxycephaly2 =
        new HpoAnnotation.Builder(oxycephalyId)
            .onset(HpoOnset.ADULT_ONSET)
            .frequency(HpoFrequency.OCCASIONAL.mean())
            .build();
    assertNotEquals(oxycephaly1, oxycephaly2);
  }
}
