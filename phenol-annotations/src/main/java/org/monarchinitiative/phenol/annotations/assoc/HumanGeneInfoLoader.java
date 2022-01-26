package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HumanGeneInfoLoader {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";


  private HumanGeneInfoLoader() {
    // should not be instantiated
  }

  /**
   * Load gene identifiers from <em>Homo_sapiens.gene_info.gz</em>.
   *
   * @param geneInfoFile path to <em>Homo_sapiens.gene_info.gz</em>
   * @return gene identifiers
   */
  public static GeneIdentifiers loadGeneIdentifiers(Path geneInfoFile) throws IOException {
    // We have seen that occasionally the Homo_sapiens_gene_info.gz
    // contains duplicate lines, which is an error, but we do not want the code
    // to crash, so we only allow distinct gene identifiers.
    // The TermId <-> symbol mapping is one to one.
    try (BufferedReader reader = FileUtils.newBufferedReader(geneInfoFile)) {
      List<GeneIdentifier> identifiers = reader.lines()
        .map(toGeneIdentifier())
        .flatMap(Optional::stream)
        .distinct()
        .collect(Collectors.toUnmodifiableList());
      return GeneIdentifiers.of(identifiers);
    }
  }

  private static Function<String, Optional<GeneIdentifier>> toGeneIdentifier() {
    return line -> {
      /*
      A line example:
      9606	336	APOA2	-	Apo-AII|ApoA-II|apoAII	MIM:107670|HGNC:HGNC:601|Ensembl:ENSG00000158874|Vega:OTTHUMG00000034346	1	1q23.3	apolipoprotein A2	protein-coding	APOA2	apolipoprotein A2	O	apolipoprotein A-II	20180603	-
       */

      String[] a = line.split("\t");
      String taxon = a[0];
      if (!taxon.equals("9606"))// i.e., we want only Homo sapiens sapiens and not Neanderthal etc.
        return Optional.empty();
      if ("unknown".equals(a[9]))
        return Optional.empty();


      TermId tid = TermId.of(ENTREZ_GENE_PREFIX, a[1]); // a[1] is geneId
      return Optional.of(GeneIdentifier.of(tid, a[2])); // a[2] is gene symbol
    };
  }
}
