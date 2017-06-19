package de.charite.compbio.ontolib.io.obo.hpo;

import static org.junit.Assert.assertEquals;

import de.charite.compbio.ontolib.formats.hpo.HpoDiseaseAnnotation;
import de.charite.compbio.ontolib.io.base.TermAnnotationParserException;
import de.charite.compbio.ontolib.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HpoDiseaseAnnotationParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File hpoDiseaseAnnotationHeadFile;

  @Before
  public void setUp() throws IOException {
    hpoDiseaseAnnotationHeadFile = tmpFolder.newFile("negative_phenotype_annotation_head.tab");
    ResourceUtils.copyResourceToFile("/negative_phenotype_annotation_head.tab",
        hpoDiseaseAnnotationHeadFile);
  }

  @Test
  public void testParseHpoDiseaseAnnotationHead()
      throws IOException, TermAnnotationParserException {
    final HpoDiseaseAnnotationParser parser =
        new HpoDiseaseAnnotationParser(hpoDiseaseAnnotationHeadFile);

    // Read and check first record.
    final HpoDiseaseAnnotation firstRecord = parser.next();
    assertEquals("HPODiseaseAnnotation [db=OMIM, dbObjectId=101000, dbName=#101000 "
        + "NEUROFIBROMATOSIS, TYPE II; NF2;;NEUROFIBROMATOSIS, CENTRAL TYPE;;ACOUSTIC "
        + "SCHWANNOMAS, BILATERAL;;BILATERAL ACOUSTIC NEUROFIBROMATOSIS; BANF;;ACOUSTIC "
        + "NEURINOMA, BILATERAL; ACN, qualifier=NOT, hpoId=ImmutableTermId "
        + "[prefix=ImmutableTermPrefix [value=HP], id=0009737], dbReference=OMIM:101000, "
        + "evidenceCode=IEA, onsetModifier=, frequencyModifier=, with=, aspect=O, synonym=, "
        + "date=2010.06.18, assignedBy=HPO:skoehler]", firstRecord.toString());

    // Read remaining records and check count.
    int count = 1;
    while (parser.hasNext()) {
      parser.next();
      count += 1;
    }
    assertEquals(10, count);

    parser.close();
  }

}
