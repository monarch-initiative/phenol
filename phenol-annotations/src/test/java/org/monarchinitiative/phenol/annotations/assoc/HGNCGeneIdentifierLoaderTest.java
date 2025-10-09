package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class HGNCGeneIdentifierLoaderTest {

  private static final Path HGNC_HEAD20 = TestBase.TEST_BASE.resolve("hgnc_complete_set.head10_and_special.tsv");

  /** Header of the hgnc_complete_set.txt file. */
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


  /** This line from the hgnc_complete_set.txt lacks the NCBI Gene identifier in field 18. We create this test to
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

  private HGNCGeneIdentifierLoader instance;

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

  private static TermId tid(String value) {
    return TermId.of(value);
  }

  /** Test what happens with lines in hgnc_complete_set that do not contain NCBI Gene identifiers */
  @Test
  void test_incomplete_input_line() {
    assertEquals(headers.size(), incompleteHgncFields.size());
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

  /** We are testing the same line as above that does not contain an NCBI gene identifier. We expect that parsing this
   * specific line not only should not throw an exception (above) but also should return an Empty Optional.
   */
  @Test
  void testIncompleteLine() {
      String AASDHPPT_AS1 = String.join("\t", incompleteHgncFields);
      Function<String, Optional<GeneIdentifier>> parser = HGNCGeneIdentifierLoader.toGeneIdentifier();
      Optional<GeneIdentifier> opt = parser.apply(AASDHPPT_AS1);
      assertTrue(opt.isEmpty());
    }


}
