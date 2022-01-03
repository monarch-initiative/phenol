package org.monarchinitiative.phenol.annotations.formats.go;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoGaf22AnnotationTest {

    private static List<String> goGafLines;

    @BeforeAll
    public static void init() throws IOException {
      URL goGaf22URL = GoGaf22AnnotationTest.class.getResource("/go/goa_human_head_gaf22.gaf");
      goGafLines = new ArrayList<>();
      BufferedReader br = new BufferedReader(new FileReader(goGaf22URL.getFile()));
      String line;
      while ((line = br.readLine()) != null) {
          if (line.startsWith("!"))
              continue; // skip comments
          goGafLines.add(line);
      }
    }

    /**
     * Our test file has exactly ten non-comment lines
     */
    @Test
    public void if_ten_lines_ingested_then_ok() {
        assertEquals(10, goGafLines.size());
    }

    /**
     * Line 0: UniProtKB	A0A024RBG1	NUDT4B	enables	GO:0003723	GO_REF:0000043	IEA	UniProtKB-KW:KW-0694	F	Diphosphoinositol polyphosphate phosphohydrolase NUDT4B	NUDT4B	protein	taxon:9606	20211010	UniProt
     */
    @Test
    public void if_item0_data_retrieved_then_ok() throws PhenolException {
        GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(goGafLines.get(0));
        assertEquals(TermId.of("UniProtKB:A0A024RBG1"), annot.getDbObjectTermId());
        assertEquals("NUDT4B", annot.getDbObjectSymbol());
        assertEquals(GoQualifier.enables, annot.getQualifier());
        assertTrue(annot.getEvidenceCode().isPresent());
        assertEquals("IEA", annot.getEvidenceCode().get());
        assertEquals(TermId.of("GO:0003723"), annot.getGoId());
        assertEquals("Diphosphoinositol polyphosphate phosphohydrolase NUDT4B", annot.getDbObjectName());
        assertEquals("protein", annot.getDbObjectType());
    }


}
