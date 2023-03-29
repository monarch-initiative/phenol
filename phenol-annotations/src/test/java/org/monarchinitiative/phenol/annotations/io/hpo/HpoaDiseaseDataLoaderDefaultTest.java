package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HpoaDiseaseDataLoaderDefaultTest {

  private static final Path HPOA = TestBase.TEST_BASE.resolve("annotations").resolve("phenotype.fake.hpoa");
  private static final Path HPOA_OLD = TestBase.TEST_BASE.resolve("annotations").resolve("phenotype.fake.old.hpoa");

  private HpoaDiseaseDataLoaderDefault instance;

  @BeforeEach
  public void setUp() {
    Set<DiseaseDatabase> databasePrefixes = Set.of(DiseaseDatabase.OMIM, DiseaseDatabase.ORPHANET, DiseaseDatabase.DECIPHER);
    instance = new HpoaDiseaseDataLoaderDefault(databasePrefixes);
  }

  @Test
  public void parseCurrentHpoa() throws Exception {
    HpoaDiseaseDataContainer diseaseData = instance.loadDiseaseData(HPOA);

    assertThat(diseaseData.diseaseData(), hasSize(3));
    assertThat(diseaseData.diseaseData().stream()
      .map(HpoaDiseaseData::id)
      .collect(Collectors.toList()), hasItems(TermId.of("OMIM:987654"), TermId.of("ORPHA:123456"), TermId.of("OMIM:111111")));

    // Versions are parsed OK
    assertThat(diseaseData.version().isPresent(), equalTo(true));
    assertThat(diseaseData.version().get(), equalTo("2021-08-02"));
    assertThat(diseaseData.getHpoVersion().isPresent(), equalTo(true));
    assertThat(diseaseData.getHpoVersion().get(), equalTo("2021-08-02"));
  }

  @Test
  public void parseOldHpoa() throws Exception {
    // See README.md in the test resources folder for more info regarding the old HPOA format.
    HpoaDiseaseDataContainer diseaseData = instance.loadDiseaseData(HPOA_OLD);

    assertThat(diseaseData.diseaseData(), hasSize(3));
    assertThat(diseaseData.diseaseData().stream()
      .map(HpoaDiseaseData::id)
      .collect(Collectors.toList()), hasItems(TermId.of("OMIM:987654"), TermId.of("ORPHA:123456"), TermId.of("OMIM:111111")));

    // Versions are parsed OK
    assertThat(diseaseData.version().isPresent(), equalTo(true));
    assertThat(diseaseData.version().get(), equalTo("2021-08-02"));
    assertThat(diseaseData.getHpoVersion().isPresent(), equalTo(true));
    assertThat(diseaseData.getHpoVersion().get(), equalTo("2021-08-02"));
  }
}
