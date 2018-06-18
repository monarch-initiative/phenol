package org.monarchinitiative.phenol.io.obo.hpo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.rules.TemporaryFolder;

/**
 * TODO refactor to use the small hp file and a small annotation file.
 */
public class HpoDiseaseAnnotationParserTest {

  private HpoDiseaseAnnotationParser parser;

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(
    File hpoDiseaseAnnotationToyFile = tmpFolder.newFile("phenotype.100lines.hpoa.tmp");
    ResourceUtils.copyResourceToFile(
        "/phenotype.100lines.hpoa", hpoDiseaseAnnotationToyFile);
//    parser =
//      new HpoDiseaseAnnotationParser(hpoDiseaseAnnotationToyFile.getAbsolutePath());
  }

  /*
  @Test
  public void testParseHpoDiseaseAnnotationHead() {
    final HpoDiseaseAnnotationParser

  }


    // Read and check first record.
    final HpoDiseaseAnnotation firstRecord = parser.next();
    assertEquals(
        "HPODiseaseAnnotation [db=OMIM, dbObjectId=101000, dbName=#101000 NEUROFIBROMATOSIS, TYPE II; NF2;;NEUROFIBROMATOSIS, CENTRAL TYPE;;ACOUSTIC SCHWANNOMAS, BILATERAL;;BILATERAL ACOUSTIC NEUROFIBROMATOSIS; BANF;;ACOUSTIC NEURINOMA, BILATERAL; ACN, qualifier=NOT, hpoId=TermId [prefix=ImmutableTermPrefix [value=HP], id=0009737], dbReference=OMIM:101000, evidenceDescription=IEA, onsetModifier=, frequencyModifier=, with=, aspect=O, synonym=, date=Fri Jun 18 00:00:00 UTC 2010, assignedBy=HPO:skoehler]",
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
