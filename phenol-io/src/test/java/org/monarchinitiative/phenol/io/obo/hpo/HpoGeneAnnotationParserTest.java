package org.monarchinitiative.phenol.io.obo.hpo;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HpoGeneAnnotationParserTest {

  private final File hpoGeneAnnotationHeadFile = Paths.get("src/test/resources/OMIM_ALL_FREQUENCIES_genes_to_phenotype_head.txt").toFile();

  @Test
  public void testParseHpoDiseaseAnnotationHead() throws IOException, TermAnnotationParserException {
    final HpoGeneAnnotationParser parser = new HpoGeneAnnotationParser(hpoGeneAnnotationHeadFile);

    // Read and check first record.
    final HpoGeneAnnotation firstRecord = parser.next();
    assertEquals(
      new HpoGeneAnnotation(8192,"CLPP", "Primary amenorrhea", TermId.of("HP:0000786")),
      firstRecord);
    // Read remaining records and check count.
    int count = 1;
    while (parser.hasNext()) {
      parser.next();
      count++;
    }
    assertEquals(9, count);

    parser.close();
  }
}
