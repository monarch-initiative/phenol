package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HumanGeneInfoLoaderTest {

  private static final Path EXAMPLE = TestBase.TEST_BASE.resolve("Homo_sapiens.gene_info.excerpt.gz");

  private static GeneIdentifiers GENE_IDENTIFIERS_ALL_TYPES;


  @BeforeAll
  public static void init() throws Exception {
    GENE_IDENTIFIERS_ALL_TYPES = HumanGeneInfoLoader.loadGeneIdentifiers(EXAMPLE, GeneInfoGeneType.ALL);
  }

  @Test
  public void testNumberOfGeneIdsParsed() {
    // zcat Homo_sapiens.gene_info.excerpt.gz | grep ^9606 | wc -l
    assertThat(GENE_IDENTIFIERS_ALL_TYPES.size(), equalTo(34));
  }

  @ParameterizedTest
  @CsvSource({
    "NCBIGene:4572, TRNQ",
    "NCBIGene:6910, TBX5"
  })
  public void testGetGeneSymbol(TermId tid, String symbol) {
    Optional<GeneIdentifier> gio = GENE_IDENTIFIERS_ALL_TYPES.geneIdById(tid);
    assertThat(gio.isPresent(), equalTo(true));
    assertThat(gio.get().id(), equalTo(tid));
    assertThat(gio.get().symbol(), equalTo(symbol));
  }

  @Test
  public void testRNR1_okWhenLoadingOnlyProteinCodingAndRRNAGeneTypes() throws Exception {
    GeneIdentifiers geneIdentifiers = HumanGeneInfoLoader.loadGeneIdentifiers(EXAMPLE, Set.of(GeneInfoGeneType.protein_coding, GeneInfoGeneType.rRNA));
    Optional<GeneIdentifier> gio = geneIdentifiers.geneIdBySymbol("RNR1");
    assertThat(gio.isPresent(), equalTo(true));
    GeneIdentifier rnr1 = gio.get();

    assertThat(rnr1.id().getValue(), equalTo("NCBIGene:4549"));
    assertThat(rnr1.symbol(), equalTo("RNR1"));
  }

  @Test
  public void testRNR1_throwsWhenLoadingAllGeneTypes() throws Exception {
    GeneIdentifiers geneIdentifiers = HumanGeneInfoLoader.loadGeneIdentifiers(EXAMPLE, GeneInfoGeneType.ALL);
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> geneIdentifiers.geneIdBySymbol("RNR1"));
    assertThat(e.getMessage(), containsString("Duplicate key RNR1 (attempted merging values RNR1 [NCBIGene:4549] and RNR1 [NCBIGene:6052])"));
  }
}
