package org.monarchinitiative.phenol.io.obo.hpo;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.monarchinitiative.phenol.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;

import static org.junit.Assert.assertEquals;

public class HpoGeneAnnotationParserTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File hpoGeneAnnotationHeadFile;

  @Before
  public void setUp() throws IOException {
    hpoGeneAnnotationHeadFile =
        tmpFolder.newFile("OMIM_ALL_FREQUENCIES_genes_to_phenotype_head.txt");
    ResourceUtils.copyResourceToFile(
        "/OMIM_ALL_FREQUENCIES_genes_to_phenotype_head.txt", hpoGeneAnnotationHeadFile);
  }

  @Test
  public void testParseHpoDiseaseAnnotationHead() throws IOException, TermAnnotationParserException {
    final HpoGeneAnnotationParser parser = new HpoGeneAnnotationParser(hpoGeneAnnotationHeadFile);

    // Read and check first record.
    final HpoGeneAnnotation firstRecord = parser.next();
    assertEquals(
      "HPOGeneAnnotation [entrezGeneId=8192, entrezGeneSymbol=CLPP, termName=Primary amenorrhea, termId=TermId [prefix=TermPrefix [value=HP], id=0000786]]",
      firstRecord.toString());
    // Read remaining records and check count.
    int count = 1;
    while (parser.hasNext()) {
      parser.next();
      count += 1;
    }
    assertEquals(9, count);

    parser.close();
  }
}
