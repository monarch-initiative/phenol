package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class HpoOnsetTest {

  @ParameterizedTest
  @CsvSource({
    "HP:0003577,     CONGENITAL_ONSET",
    "HP:0003581,     ADULT_ONSET",
    "HP:0003584,     LATE_ONSET",
    "HP:0011462,     YOUNG_ADULT_ONSET",
    "HP:0003596,     MIDDLE_AGE_ONSET",
    "HP:0003593,     INFANTILE_ONSET",
    "HP:0030674,     ANTENATAL_ONSET",
    "HP:0011460,     EMBRYONAL_ONSET",
    "HP:0410280,     PEDIATRIC_ONSET",
    "HP:0011461,     FETAL_ONSET",
    "HP:0003621,     JUVENILE_ONSET",
    "HP:0003623,     NEONATAL_ONSET",
    "HP:0011463,     CHILDHOOD_ONSET",
  })
  public void fromHpoIdString(String termId, HpoOnset onset) {
    Optional<HpoOnset> hpoOnsetOptional = HpoOnset.fromHpoIdString(termId);

    assertThat(hpoOnsetOptional.isPresent(), equalTo(true));
    HpoOnset hpoOnset = hpoOnsetOptional.get();
    assertThat(hpoOnset, equalTo(onset));
    assertThat(hpoOnset.id().getValue(), equalTo(termId));
  }

  @Test
  public void fromHpoIdString_unknownTermId() {
    Optional<HpoOnset> hpoOnsetOptional = HpoOnset.fromHpoIdString("UNKNOWN");

    assertThat(hpoOnsetOptional.isPresent(), equalTo(false));
  }
}
