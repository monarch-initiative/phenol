package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class HumanGeneInfoLoaderTest {
  private static GeneIdentifiers GENE_IDENTIFIERS;


  @BeforeAll
  public static void init() throws Exception {
    Path homoSapiensGeneInfo = Path.of(HumanGeneInfoLoaderTest.class.getResource("/Homo_sapiens.gene_info.excerpt.gz").toURI());
    GENE_IDENTIFIERS = HumanGeneInfoLoader.loadGeneIdentifiers(homoSapiensGeneInfo);
  }

  @Test
  public void testNumberOfGeneIdsParsed() {
    // zcat Homo_sapiens.gene_info.excerpt.gz | grep ^9606 | wc -l
    assertThat(GENE_IDENTIFIERS.geneIdentifiers(), hasSize(32));
  }

  @ParameterizedTest
  @CsvSource({
    "NCBIGene:4572, TRNQ",
    "NCBIGene:6910, TBX5"
  })
  public void testGetGeneSymbol(TermId tid, String symbol) {
    assertThat(GENE_IDENTIFIERS.geneIdToSymbol().get(tid), equalTo(symbol));
  }

}
