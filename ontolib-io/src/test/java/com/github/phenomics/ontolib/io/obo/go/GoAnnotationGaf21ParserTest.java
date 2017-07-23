package com.github.phenomics.ontolib.io.obo.go;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.phenomics.ontolib.formats.go.GoGaf21Annotation;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.io.obo.go.GoGeneAnnotationParser;
import com.github.phenomics.ontolib.io.utils.ResourceUtils;

/**
 * Test for loading first 40 lines of human GO annotation GAF v2.1.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GoAnnotationGaf21ParserTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private File goGeneAnnotationHeadFile;

  @Before
  public void setUp() throws IOException {
    goGeneAnnotationHeadFile = tmpFolder.newFile("goa_human_head.gav21.gaf");
    ResourceUtils.copyResourceToFile("/goa_human_head.gav21.gaf", goGeneAnnotationHeadFile);
  }

  @Test
  public void testParseGoDiseaseAnnotationHead() throws IOException, TermAnnotationParserException {
    final GoGeneAnnotationParser parser = new GoGeneAnnotationParser(goGeneAnnotationHeadFile);

    // Read and check first record.
    final GoGaf21Annotation firstRecord = parser.next();
    assertEquals(
        "GoGaf21Annotation [db=UniProtKB, dbObjectId=A0A024R161, dbObjectSymbol=DNAJC25-GNG10, qualifier=, goId=ImmutableTermId [prefix=ImmutableTermPrefix [value=GO], id=0004871], dbReference=GO_REF:0000038, evidenceCode=IEA, with=UniProtKB-KW:KW-0807, aspect=F, dbObjectName=Guanine nucleotide-binding protein subunit gamma, dbObjectSynonym=A0A024R161_HUMAN|DNAJC25-GNG10|hCG_1994888, dbObjectType=protein, taxons=[taxon:9606], date=20170603, assignedBy=UniProt, annotationExtension=null, geneProductFormId=null]",
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
