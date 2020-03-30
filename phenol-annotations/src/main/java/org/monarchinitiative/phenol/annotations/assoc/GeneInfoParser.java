package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class GeneInfoParser {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";


  private GeneInfoParser() {
    // should not be instantiated
  }

  /**
   * @param geneInfoPath Path to Homo_sapiens.gene_info.gz
   * @return an immutable Map with keys being NCBI Gene IDs and values being the corresponding symbols
   */
  public static Map<TermId, String> loadGeneIdToSymbolMap(String geneInfoPath) {
    if (geneInfoPath == null) {
      throw new PhenolRuntimeException("Attempt to loadGeneIdToSymbolMap with null value for geneInfoPath");
    }
    return loadGeneIdToSymbolMap(new File(geneInfoPath));
  }

  /**
   * @param geneInfoFile  Homo_sapiens.gene_info.gz File
   * @return an immutable Map with keys being NCBI Gene IDs and values being the corresponding symbols
   */
  public static Map<TermId, String> loadGeneIdToSymbolMap(File geneInfoFile) {
    if (! geneInfoFile.exists()) {
      throw new PhenolRuntimeException("Could not find Homo_sapiens.gene_info.gz");
    }
    ImmutableMap.Builder<TermId, String> builder = new ImmutableMap.Builder<>();
    try {
      InputStream fileStream = new FileInputStream(geneInfoFile);
      InputStream gzipStream = new GZIPInputStream(fileStream);
      Reader decoder = new InputStreamReader(gzipStream);
      BufferedReader br = new BufferedReader(decoder);
      String line;
      // We have seen that occasionally the Homo_sapiens_gene_info.gz
      // contains duplicate lines, which is an error but we do not want the code
      // to crash, so we check for previously found term ids with the seen set.
      // The TermId <-> symbol mapping is one to one.
      Set<TermId> seen = new HashSet<>();
      while ((line = br.readLine()) != null) {
        String[] a = line.split("\t");
        String taxon = a[0];
        if (!taxon.equals("9606")) continue; // i.e., we want only Homo sapiens sapiens and not Neaderthal etc.
        if (!("unknown".equals(a[9]))) {
          String geneId = a[1];
          String symbol = a[2];
          TermId tid = TermId.of(ENTREZ_GENE_PREFIX, geneId);
          if (seen.contains(tid)) {
            continue;
          }
          seen.add(tid);
          builder.put(tid, symbol);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new PhenolRuntimeException("IOException encountered when trying to read Homo_sapiens.gene_info.gz.");
    }
    return builder.build();
  }

}
