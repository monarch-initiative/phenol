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

  @Test
  public void testCandidaAlbicans() throws PhenolException {
    String[] F = {"CGD","CAL0000196141","AAF1","enables","GO:0005634","CGD_REF:CAL0080631|PMID:9573092","IGI","","C","","C3_06470W_A|CAD1|IPF2468.1|Contig4-3080_0008|Contig4-3080_0010|orf6.8596|CA5726|CaO19.7436|orf19.7436|C3_06470W_B|C3_06470W|CAWG_02957","gene_product","taxon:5476","20140625","CGD"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("CGD","CAL0000196141");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0005634");
    assertEquals(goId,annot.getGoId());
    assertEquals("C",annot.getAspect());
  }


  @Test
  public void testMouse() throws PhenolException {
    String[] F = {"MGI","MGI:1918911","0610005C13Rik","enables","GO:0005575","GO_REF:0000015|MGI:MGI:2156816","ND","","C","RIKEN cDNA 0610005C13 gene","","gene","taxon:10090","20100209","MGI"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("MGI:1918911");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0005575");
  }

  @Test
  public void testYeast() throws PhenolException {
    String[] F = {"SGD","S000002318","STE7","enables","GO:0000187","PMID:8384702","IDA","","P","Signal transducing MAP kinase kinase","YDL159W|mitogen-activated protein kinase kinase STE7","gene","taxon:559292","20171213","SGD","has_direct_input(UniProtKB:P16892),part_of(GO:0000749)"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("SGD:S000002318");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0000187");
  }

  @Test
  public void testDog() throws PhenolException {
    String[] F = {"UniProtKB", "A0A075B5H7", "A0A075B5H7", "enables", "GO:0006412", "GO_REF:0000002", "IEA", "InterPro:IPR000552|InterPro:IPR011332", "P", "Uncharacterized protein", "", "protein", "taxon:9615", "20181103", "InterPro"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("UniProtKB:A0A075B5H7");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0006412");
  }

  @Test
  public void testEcoli() throws PhenolException {
    String[] F = {"UniProtKB", "P0A776", "rppH", "enables", "GO:0034353", "PMID:18202662", "IDA", "", "F", "", "", "gene", "taxon:83333", "20081017", "EcoliWiki"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("UniProtKB:P0A776");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0034353");
  }

  @Test
  public void testRat() throws PhenolException {
    String[] F = {"RGD", "10059729", "Baiap3", "enables", "GO:0000149", "RGD:1624291", "ISO", "RGD:1318052", "F", "BAI1-associated protein 3", "", "gene", "taxon:10116", "20180405", "RGD"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("RGD:10059729");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0000149");

  }

  @Test
  public void testChicken() throws PhenolException {
    String[] F = {"UniProtKB","A0A088BIK7","EDbeta","enables","GO:0005882","GO_REF:0000038","IEA","UniProtKB-KW:KW-0416","C","Keratin","EDbeta|EDBETA","protein","taxon:9031","20181103","UniProt"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("UniProtKB:A0A088BIK7");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0005882");

  }



  @Test
  public void testHuman() throws PhenolException {
    String[] F = {"UniProtKB","A0A024R161","DNAJC25-GNG10","enables","GO:0007186","GO_REF:0000002","IEA","InterPro:IPR001770|InterPro:IPR015898|InterPro:IPR036284","P","Guanine nucleotide-binding protein subunit gamma","DNAJC25-GNG10|hCG_1994888","protein","taxon:9606","20181103","InterPro"};
    String line = String.join("\t", F);
    GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
    TermId expected = TermId.of("UniProtKB:A0A024R161");
    assertEquals(expected,annot.getDbObjectTermId());
    TermId goId = TermId.of("GO:0007186");

  }


}
