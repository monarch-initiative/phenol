package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneInfoParserTest {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";
  private static Map<TermId, String> id2symMap;


  @BeforeAll
  private static void init() throws IOException {
    final String homoSapiensGeneInfoPath = "Homo_sapiens.gene_info.excerpt.gz";
    ClassLoader classLoader = Gene2DiseaseAsssociationParserTest.class.getClassLoader();
    URL homoSapiensGeneInfoURL = classLoader.getResource(homoSapiensGeneInfoPath);
    if (homoSapiensGeneInfoURL == null) {
      throw new IOException("Could not find Homo_sapiens.gene_info.excerpt.gz at " + homoSapiensGeneInfoPath);
    }
    id2symMap = GeneInfoParser.loadGeneIdToSymbolMap(homoSapiensGeneInfoURL.getFile());
  }

  @Test
  void testNumberOfGeneIdsParsed() {
    // zcat Homo_sapiens.gene_info.excerpt.gz | grep ^9606 | wc -l
    int expected = 26;
    assertEquals(expected, id2symMap.size());
  }

  @Test
  void testGetGeneSymbolTRNQ() {
    TermId tid = TermId.of(ENTREZ_GENE_PREFIX, "4572");
    String expected = "TRNQ";
    assertEquals(expected, id2symMap.get(tid));
  }

  @Test
  void testGetGeneSymbolTBX5() {
    TermId tid = TermId.of(ENTREZ_GENE_PREFIX, "6910");
    String expected = "TBX5";
    assertEquals(expected, id2symMap.get(tid));
  }

}
