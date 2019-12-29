package org.monarchinitiative.phenol.annotations.obo.go;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for loading first 40 lines of human GO annotation GAF v2.1.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GoAnnotationGaf21ParserTest {

  /*
  UniProtKB	A0A024R161	DNAJC25-GNG10		GO:0004871	GO_REF:0000038	IEA	UniProtKB-KW:KW-0807	F	Guanine nucleotide-binding protein subunit gamma	A0A024R161_HUMAN|DNAJC25-GNG10|hCG_1994888	protein	taxon:9606	20170603	UniProt

   */
  @Test
  public void testParseGoDiseaseAnnotationHead() throws PhenolException {
    final File goGeneAnnotationHeadFile = Paths.get("src/test/resources/go/goa_human_head.gav21.gaf").toFile();
    final GoGeneAnnotationParser parser = new GoGeneAnnotationParser(goGeneAnnotationHeadFile);

    // Read and check first record.
    final GoGaf21Annotation firstRecord = parser.getAnnotations().get(0);
    assertEquals("UniProtKB",firstRecord.getDb());
    assertEquals("A0A024R161",firstRecord.getDbObjectId());
    assertEquals("DNAJC25-GNG10",firstRecord.getDbObjectSymbol());
    TermId expectedId= TermId.of("GO:0004871");
    assertEquals(expectedId,firstRecord.getGoId());
    assertNotNull(firstRecord.getEvidenceCode().get());
    assertEquals("IEA",firstRecord.getEvidenceCode().get());


    // Read remaining records and check count.

    assertEquals(6, parser.getAnnotations().size());
  }
}
