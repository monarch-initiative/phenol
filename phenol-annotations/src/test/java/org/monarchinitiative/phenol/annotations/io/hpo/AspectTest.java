package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoClinicalModifierTermIds.CLINICAL_COURSE;
import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT;
import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoSubOntologyRootTermIds.*;

public class AspectTest {

  @Test
  public void test_aspect_from_termId() {
    assertThat(Aspect.fromTermId(PHENOTYPIC_ABNORMALITY).get(), equalTo(Aspect.P));
    assertThat(Aspect.fromTermId(INHERITANCE_ROOT).get(), equalTo(Aspect.I));
    assertThat(Aspect.fromTermId(CLINICAL_COURSE).get(), equalTo(Aspect.C));
    assertThat(Aspect.fromTermId(CLINICAL_MODIFIER).get(), equalTo(Aspect.M));
    assertThat(Aspect.fromTermId(PAST_MEDICAL_HISTORY).get(), equalTo(Aspect.H));
    assertThat(Aspect.fromTermId(TermId.of("HP:0000000")).isEmpty(), equalTo(true));
  }
}
