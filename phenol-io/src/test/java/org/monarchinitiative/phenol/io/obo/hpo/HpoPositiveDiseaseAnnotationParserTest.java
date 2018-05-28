package org.monarchinitiative.phenol.io.obo.hpo;

import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class HpoPositiveDiseaseAnnotationParserTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File hpoDiseaseAnnotationHeadFile;

  @Before
  public void setUp() throws IOException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(

    hpoDiseaseAnnotationHeadFile = tmpFolder.newFile("phenotype_annotation_head.tab");
    ResourceUtils.copyResourceToFile(
        "/phenotype_annotation_head.tab", hpoDiseaseAnnotationHeadFile);
  }

  /*
  @Test
  public void testParseHpoDiseaseAnnotationHead()
      throws IOException, TermAnnotationParserException {
    final HpoDiseaseAnnotationParser parser =
        new HpoDiseaseAnnotationParser(hpoDiseaseAnnotationHeadFile);

    // Read and check first record.
    final HpoDiseaseAnnotation firstRecord = parser.next();
    assertEquals(
        "HPODiseaseAnnotation [db=DECIPHER, dbObjectId=1, dbName=Wolf-Hirschhorn Syndrome, qualifier=, hpoId=TermId [prefix=ImmutableTermPrefix [value=HP], id=0000252], dbReference=DECIPHER:1, evidenceDescription=IEA, onsetModifier=, frequencyModifier=, with=, aspect=O, synonym=WOLF-HIRSCHHORN SYNDROME, date=Wed May 29 00:00:00 UTC 2013, assignedBy=HPO:skoehler]",
        firstRecord.toString());

    // Read remaining records and check count.
    int count = 1;
    while (parser.hasNext()) {
      parser.next();
      count += 1;
    }
    assertEquals(10, count);

    parser.close();
  }*/

}
