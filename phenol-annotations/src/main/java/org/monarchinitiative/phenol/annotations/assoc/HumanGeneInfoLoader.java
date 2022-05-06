package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HumanGeneInfoLoader {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";


  private HumanGeneInfoLoader() {
    // should not be instantiated
  }

  public static GeneIdentifiers loadGeneIdentifiers(Path geneInfoFile, Set<GeneInfoGeneType> geneTypes) throws IOException {
    try (InputStream is = FileUtils.newInputStream(geneInfoFile)) {
      return loadGeneIdentifiers(is, geneTypes);
    }
  }

  /**
   * Load gene identifiers from <em>Homo_sapiens.gene_info.gz</em>.
   *
   * @param is        input stream. The stream is decoded using {@link java.nio.charset.StandardCharsets#UTF_8} and
   *                  it is not closed after consuming.
   * @param geneTypes types of genes to include
   * @return gene identifiers
   */
  public static GeneIdentifiers loadGeneIdentifiers(InputStream is, Set<GeneInfoGeneType> geneTypes) {
    // The TermId <-> symbol mapping is one to one.
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      List<GeneIdentifier> identifiers = reader.lines()
        .filter(line -> !line.startsWith("#")) // skip header
        .map(l -> l.split("\t"))
        .filter(retainSelected(geneTypes))
        .map(toGeneIdentifier())
        .flatMap(Optional::stream)
        .collect(Collectors.toUnmodifiableList());
      return GeneIdentifiers.of(identifiers);
  }

  private static Predicate<? super String[]> retainSelected(Set<GeneInfoGeneType> geneTypes) {
    return tokens -> geneTypes.contains(GeneInfoGeneType.parse(tokens[9]));
  }

  /**
   * Load gene identifiers from <em>Homo_sapiens.gene_info.gz</em> including <em>all</em> {@link GeneInfoGeneType}s.
   *
   * @param geneInfoFile path to <em>Homo_sapiens.gene_info.gz</em>
   * @return gene identifiers
   * @deprecated to be removed in v2.0.0, use {@link #loadGeneIdentifiers(Path, Set)} instead.
   */
  @Deprecated(forRemoval = true, since = "v2.0.0-RC3")
  public static GeneIdentifiers loadGeneIdentifiers(Path geneInfoFile) throws IOException {
    return loadGeneIdentifiers(geneInfoFile, GeneInfoGeneType.ALL);
  }

  private static Function<String[], Optional<GeneIdentifier>> toGeneIdentifier() {
    return tokens -> {
      /*
      A line example:
      9606	336	APOA2	-	Apo-AII|ApoA-II|apoAII	MIM:107670|HGNC:HGNC:601|Ensembl:ENSG00000158874|Vega:OTTHUMG00000034346	1	1q23.3	apolipoprotein A2	protein-coding	APOA2	apolipoprotein A2	O	apolipoprotein A-II	20180603	-
       */

      String taxon = tokens[0];
      if (!taxon.equals("9606"))// i.e., we want only Homo sapiens sapiens and not Neanderthal etc.
        return Optional.empty();

      String id = tokens[1]; // a[1] is geneId
      if (id.equals("122405565"))
        // There are 2 entries for `SMIM44`. Let's keep the first one
        // 9606    122405565       SMIM44  -       -       Ensembl:ENSG00000284638 19      19p13.3 small integral membrane protein 44      protein-coding  -       -       -       small integral membrane protein 44      20220313        -
        return Optional.empty();

      String symbol = tokens[2]; // a[2] is gene symbol
      if (symbol.equals("TRNAV-CAC"))
        // TRNAV-CAC are not present on genenames.org - the entries below are of low quality
        // 9606    107985614       TRNAV-CAC       -       -       -       1       1q21.1  transfer RNA valine (anticodon CAC)     tRNA    -       -       -       -       20211123        -
        // 9606    107985615       TRNAV-CAC       -       -       -       1       1q21.1  transfer RNA valine (anticodon CAC)     tRNA    -       -       -       -       20211123        -
        return Optional.empty();

      TermId tid = TermId.of(ENTREZ_GENE_PREFIX, id);
      return Optional.of(GeneIdentifier.of(tid, symbol));
    };
  }
}
