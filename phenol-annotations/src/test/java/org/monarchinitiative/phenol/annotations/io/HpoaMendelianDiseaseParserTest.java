package org.monarchinitiative.phenol.annotations.io;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.disease.MendelianDisease;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class HpoaMendelianDiseaseParserTest {

  // TODO: 10/25/21 improve HPOA content
  private static final Path SMALL_HPOA = Path.of("src/test/resources/small.hpoa");

  @Test
  public void parse() throws Exception {
    HpoaMendelianDiseaseParser parser = HpoaMendelianDiseaseParser.of();
    List<MendelianDisease> diseases = parser.parse(SMALL_HPOA);

    assertThat(diseases.size(), equalTo(2));
    diseases = diseases.stream()
      .sorted(Comparator.comparing(d -> d.id().getId()))
      .collect(Collectors.toUnmodifiableList());

    MendelianDisease marfan = diseases.get(0);
    assertThat(marfan.id(), equalTo(TermId.of("OMIM:154700")));
    assertThat(marfan.name(), equalTo("Marfan syndrome"));

    MendelianDisease leigh = diseases.get(1);
    assertThat(leigh.id(), equalTo(TermId.of("ORPHA:506")));
    assertThat(leigh.name(), equalTo("Leigh syndrome"));
    // TODO: 10/25/21 add assertions
  }
}
