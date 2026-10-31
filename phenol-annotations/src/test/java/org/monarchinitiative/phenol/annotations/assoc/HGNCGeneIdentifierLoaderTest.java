package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class HGNCGeneIdentifierLoaderTest {

  private static final Path HGNC_HEAD20 = TestBase.TEST_BASE.resolve("hgnc_complete_set.head10_and_special.tsv");
  /**
   * This line from the hgnc_complete_set.txt lacks the NCBI Gene identifier in field 18. We create this test to
   * show that are parsing code does not crash with incomplete lines like this.
   */
  private static final List<String> incompleteHgncFields = List.of(
    "HGNC:58262",
    "AASDHPPT-AS1",
    "AASDHPPT antisense RNA 1",
    "non-coding RNA",
    "RNA, long non-coding",
    "Approved",
    "11q22.3",
    "11q22.3",
    "",
    "",
    "",
    "",
    "Antisense RNAs",
    "1987",
    "1/27/25",
    "",
    "",
    "1/27/25",
    "",
    "ENSG00000254433",
    "54433",
    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
  );
  /**
   * Header of the hgnc_complete_set.txt file.
   */
  List<String> headers = List.of(
    "hgnc_id",
    "symbol",
    "name",
    "locus_group",
    "locus_type",
    "status",
    "location",
    "location_sortable",
    "alias_symbol",
    "alias_name",
    "prev_symbol",
    "prev_name",
    "gene_group",
    "gene_group_id",
    "date_approved_reserved",
    "date_symbol_changed",
    "date_name_changed",
    "date_modified",
    "entrez_id",
    "ensembl_gene_id",
    "vega_id",
    "ucsc_id",
    "ena",
    "refseq_accession",
    "ccds_id",
    "uniprot_ids",
    "pubmed_id",
    "mgd_id",
    "rgd_id",
    "lsdb",
    "cosmic",
    "omim_id",
    "mirbase",
    "homeodb",
    "snornabase",
    "bioparadigms_slc",
    "orphanet",
    "pseudogene.org",
    "horde_id",
    "merops",
    "imgt",
    "iuphar",
    "kznf_gene_catalog",
    "mamit-trnadb",
    "cd",
    "lncrnadb",
    "enzyme_id",
    "intermediate_filament_db",
    "rna_central_id",
    "lncipedia",
    "gtrnadb",
    "agr",
    "mane_select",
    "gencc"
  );
  private HGNCGeneIdentifierLoader instance;

  private static TermId tid(String value) {
    return TermId.of(value);
  }

  @BeforeEach
  public void setUp() {
    instance = new HGNCGeneIdentifierLoader();
  }

  @Test
  public void load() throws Exception {
    GeneIdentifiers identifiers = instance.load(HGNC_HEAD20);

    assertThat(Math.toIntExact(identifiers.stream().count()), equalTo(9));

    List<TermId> ids = identifiers.stream()
      .map(GeneIdentifier::id)
      .collect(Collectors.toList());
    assertThat(ids, containsInRelativeOrder(tid("NCBIGene:1"), tid("NCBIGene:503538"), tid("NCBIGene:29974"), tid("NCBIGene:2"), tid("NCBIGene:144571"), tid("NCBIGene:144568"), tid("NCBIGene:100874108"), tid("NCBIGene:106478979"), tid("NCBIGene:3")));

    List<String> symbols = identifiers.stream()
      .map(GeneIdentifier::symbol)
      .collect(Collectors.toList());
    assertThat(symbols, containsInRelativeOrder("A1BG", "A1BG-AS1", "A1CF", "A2M", "A2M-AS1", "A2ML1", "A2ML1-AS1", "A2ML1-AS2", "A2MP1"));
  }

  /**
   * Test what happens with lines in hgnc_complete_set that do not contain NCBI Gene identifiers
   */
  @Test
  public void test_incomplete_input_line() {
    assertThat(headers.size(), equalTo(incompleteHgncFields.size()));
    String header = String.join("\t", headers);
    String AASDHPPT_AS1 = String.join("\t", incompleteHgncFields);
    String miniHgncFile = header + "\t" + AASDHPPT_AS1;
    HGNCGeneIdentifierLoader instance = HGNCGeneIdentifierLoader.instance();
    assertDoesNotThrow(() -> {
      try (Reader reader = new StringReader(miniHgncFile)) {
        instance.load(reader);
      }
    });
  }

  /**
   * We are testing the same line as above that does not contain an NCBI gene identifier. We expect that parsing this
   * specific line not only should not throw an exception (above) but also should return an Empty Optional.
   */
  @Test
  public void testIncompleteLine() throws IOException {
    String AASDHPPT_AS1 = String.join("\t", incompleteHgncFields);
    HGNCGeneIdentifierLoader.ColIndices indices = HGNCGeneIdentifierLoader.findSymbolAndEntrezIdColIdx(headers.toArray(String[]::new));
    Function<String, Optional<GeneIdentifier>> parser = HGNCGeneIdentifierLoader.toGeneIdentifier(indices);
    Optional<GeneIdentifier> opt = parser.apply(AASDHPPT_AS1);
    assertThat(opt.isEmpty(), is(true));
  }

  /**
   * The <code>location_sortable</code> column recently disappeared from the hgnc complete set file.
   * Test that the parser can process the new format.
   */
  @Test
  public void testRnaLine() throws IOException {
    String header = "hgnc_id\tsymbol\tname\tlocus_group\tlocus_type\tstatus\tlocation\talias_symbol\talias_name\tprev_symbol\tprev_name\tgene_group\tgene_group_id\tdate_approved_reserved\tdate_symbol_changed\tdate_name_changed\tdate_modified\tentrez_id\tensembl_gene_id\tvega_id\tucsc_id\tena\trefseq_accession\tccds_id\tuniprot_ids\tpubmed_id\tmgd_id\trgd_id\tlsdb\tcosmic\tomim_id\tmirbase\thomeodb\tsnornabase\tbioparadigms_slc\torphanet\tpseudogene.org\thorde_id\tmerops\timgt\tiuphar\tkznf_gene_catalog\tmamit-trnadb\tcd\tlncrnadb\tenzyme_id\tintermediate_filament_db\trna_central_id\tlncipedia\tgtrnadb\tagr\tmane_select\tgencc\n";

    HGNCGeneIdentifierLoader.ColIndices indices = HGNCGeneIdentifierLoader.findSymbolAndEntrezIdColIdx(header.split("\\t", -1));
    String rnaGene = "HGNC:31430\tLINC00595\tlong independently transcribed non-coding RNA 595\tnon-coding RNA\tRNA, long non-coding\tApproved\t10q22.3\t\t\tC10orf101\t\"long intergenic non-protein coding RNA 595|chromosome 10 open reading frame 101\"\tLong independently transcribed non-coding RNAs\t1986\t2004-05-27\t2012-04-30\t2026-06-11\t2026-06-11\t414243\tENSG00000230417\tOTTHUMG00000018552\t\t\"AA256890|BQ082446|BQ070817\"\tNR_073447\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tURS0000D78019\t\t\tHGNC:31430\t\t\n";

    Optional<GeneIdentifier> opt = HGNCGeneIdentifierLoader.toGeneIdentifier(indices).apply(rnaGene);
    assertThat(opt.isPresent(), is(true));
    GeneIdentifier gi = opt.get();
    assertThat(gi.id(), equalTo(TermId.of("NCBIGene:414243")));
    assertThat(gi.symbol(), equalTo("LINC00595"));
  }
}
