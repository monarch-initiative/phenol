package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneInfoParserTest {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";
  final Path homoSapiensGeneInfoPath = Paths.get("src/test/resources/Homo_sapiens.gene_info.excerpt.gz");
  private final Map<TermId, String> id2symMap = GeneInfoParser.loadGeneIdToSymbolMap(homoSapiensGeneInfoPath.toFile());

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
