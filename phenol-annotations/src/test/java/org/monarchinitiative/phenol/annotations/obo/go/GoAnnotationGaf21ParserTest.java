package org.monarchinitiative.phenol.annotations.obo.go;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for loading first 40 lines of human GO annotation GAF v2.1.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GoAnnotationGaf21ParserTest {

  private static List<GoGaf21Annotation> annotations;

  @BeforeAll
  public static void init() throws IOException {
    URL goGafURL = GoAnnotationGaf21ParserTest.class.getResource("/go/goa_human_head.gav21.gaf");
    if (goGafURL == null) {
      throw new IOException("Could not find goa_human_head.gav21.gaf " + goGafURL);
    }
    final File goGeneAnnotationHeadFile = new File(goGafURL.getFile());
    annotations = GoGeneAnnotationParser.loadAnnotations(goGeneAnnotationHeadFile);
  }


  @Test
  void ifAllSixAnnotationsWereParsed_thenOK() {
    assertEquals(6, annotations.size());
  }

  /*
  0. UniProtKB
  1. A0A024R161
  2. DNAJC25-GNG10
  3. GO:0004871
  GO_REF:0000038
  IEA
  UniProtKB-KW:KW-0807
  F
  Guanine nucleotide-binding protein subunit gamma
  A0A024R161_HUMAN|DNAJC25-GNG10|hCG_1994888
  protein
  taxon:9606
  20170603
  UniProt
 */
  @Test
  void testAnnotationsOfFirstEntry() {
    final GoGaf21Annotation firstRecord = annotations.get(0);
    assertEquals("UniProtKB",firstRecord.getDb());
    assertEquals("A0A024R161",firstRecord.getDbObjectId());
    assertEquals("DNAJC25-GNG10",firstRecord.getDbObjectSymbol());
    assertEquals(TermId.of("GO:0004871"), firstRecord.getGoId());
    assertEquals("F", firstRecord.getAspect());
    List<String> taxons = firstRecord.getTaxons();
    assertEquals(1, taxons.size());
    assertEquals("taxon:9606", taxons.get(0));
    TermId expectedId= TermId.of("GO:0004871");
    assertEquals("protein",firstRecord.getDbObjectType());
    assertEquals(expectedId,firstRecord.getGoId());
    assertNotNull(firstRecord.getEvidenceCode().get());
    assertEquals("IEA",firstRecord.getEvidenceCode().get());
  }




}
