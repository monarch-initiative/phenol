package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class GeneInfoLoader {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";


  private GeneInfoLoader() {
    // should not be instantiated
  }

  /**
   * @param geneInfoFile Homo_sapiens.gene_info.gz File
   * @return an immutable Map with keys being NCBI Gene IDs and values being the corresponding symbols
   */
  public static GeneIdentifiers loadGeneIdentifiers(Path geneInfoFile) throws IOException {
    List<GeneIdentifier> builder = new ArrayList<>();
    try (BufferedReader reader = FileUtils.openForReading(geneInfoFile)) {
      String line;
      // We have seen that occasionally the Homo_sapiens_gene_info.gz
      // contains duplicate lines, which is an error but we do not want the code
      // to crash, so we check for previously found term ids with the seen set.
      // The TermId <-> symbol mapping is one to one.
      Set<TermId> seen = new HashSet<>();
      while ((line = reader.readLine()) != null) {
        String[] a = line.split("\t");
        String taxon = a[0];
        if (!taxon.equals("9606")) continue; // i.e., we want only Homo sapiens sapiens and not Neaderthal etc.
        if (!("unknown".equals(a[9]))) {
          String geneId = a[1];
          TermId tid = TermId.of(ENTREZ_GENE_PREFIX, geneId);
          if (seen.contains(tid)) continue;

          seen.add(tid);

          String symbol = a[2];

          builder.add(GeneIdentifier.of(tid, symbol));
        }
      }
      return GeneIdentifiers.of(List.copyOf(builder));
    }
  }
}
