package org.monarchinitiative.phenol.io.obo.go;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.io.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Test for loading first 40 lines of human GO annotation GAF v2.1.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GoAnnotationGaf21ParserTest {

  @Rule public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File goGeneAnnotationHeadFile;

  @Before
  public void setUp() throws IOException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(

    goGeneAnnotationHeadFile = tmpFolder.newFile("goa_human_head.gav21.gaf");
    ResourceUtils.copyResourceToFile("/go/goa_human_head.gav21.gaf", goGeneAnnotationHeadFile);
  }


  /*
  UniProtKB	A0A024R161	DNAJC25-GNG10		GO:0004871	GO_REF:0000038	IEA	UniProtKB-KW:KW-0807	F	Guanine nucleotide-binding protein subunit gamma	A0A024R161_HUMAN|DNAJC25-GNG10|hCG_1994888	protein	taxon:9606	20170603	UniProt

   */
  @Test
  public void testParseGoDiseaseAnnotationHead() throws IOException, TermAnnotationParserException {
    final GoGeneAnnotationParser parser = new GoGeneAnnotationParser(goGeneAnnotationHeadFile);

    // Read and check first record.
    final GoGaf21Annotation firstRecord = parser.next();
    assertEquals("UniProtKB",firstRecord.getDb());
    assertEquals("A0A024R161",firstRecord.getDbObjectId());
    assertEquals("DNAJC25-GNG10",firstRecord.getDbObjectSymbol());
    TermId expectedId= TermId.constructWithPrefix("GO:0004871");
    assertEquals(expectedId,firstRecord.getGoId());
    assertNotNull(firstRecord.getEvidenceCode().get());
    assertEquals("IEA",firstRecord.getEvidenceCode().get());
    assertEquals(
        "GoGaf21Annotation [db=UniProtKB, dbObjectId=A0A024R161, dbObjectSymbol=DNAJC25-GNG10, qualifier=, goId=TermId [prefix=TermPrefix [value=GO], id=0004871], dbReference=GO_REF:0000038, evidenceCode=IEA, with=UniProtKB-KW:KW-0807, aspect=F, dbObjectName=Guanine nucleotide-binding protein subunit gamma, dbObjectSynonym=A0A024R161_HUMAN|DNAJC25-GNG10|hCG_1994888, dbObjectType=protein, taxons=[taxon:9606], date=Sat Jun 03 00:00:00 UTC 2017, assignedBy=UniProt, annotationExtension=null, geneProductFormId=null]",
        firstRecord.toString());

    // Read remaining records and check count.
    int count = 1;
    while (parser.hasNext()) {
      parser.next();
      count += 1;
    }
    assertEquals(6, count);

    parser.close();
  }
}
