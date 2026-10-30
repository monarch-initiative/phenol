package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

class HGNCGeneIdentifierLoader implements GeneIdentifierLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HGNCGeneIdentifierLoader.class);
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";

  private static final HGNCGeneIdentifierLoader INSTANCE = new HGNCGeneIdentifierLoader();

  static HGNCGeneIdentifierLoader instance() {
    return INSTANCE;
  }

  @Override
  public GeneIdentifiers load(Reader reader) throws IOException {
    BufferedReader br = (reader instanceof BufferedReader)
      ? (BufferedReader) reader
      : new BufferedReader(reader);

    String[] fields = br.readLine().split("\t", -1);
    ColIndices indices = findSymbolAndEntrezIdColIdx(fields);

    List<GeneIdentifier> identifiers = br.lines()
      .map(toGeneIdentifier(indices))
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    return GeneIdentifiers.of(identifiers);
  }

  static Function<String, Optional<GeneIdentifier>> toGeneIdentifier(ColIndices indices) {
    return line -> {
      String[] token = line.split("\t", -1);
      if (token.length < indices.maxIdx || !token[indices.hgncIdx].startsWith("HGNC")) {
        LOGGER.warn(
          "Skipping malformed line: expected a line with ≥ 19 columns starting with `HGNC` but found {} columns. Line: {}",
          token.length, line
        );
        return Optional.empty();
      }
      // 1 - Entrez ID
      String id = token[indices.entrezIdIdx];
      if (id.isBlank()) {
        LOGGER.debug("Skipping line with missing Entrez ID '{}'", line);
        return Optional.empty();
      }
      TermId entrezId = TermId.of(ENTREZ_GENE_PREFIX, id);

      // 2 - HGVS symbol
      String symbol = token[indices.symbolIdx];

      return Optional.of(GeneIdentifier.of(entrezId, symbol));
    };
  }

  static ColIndices findSymbolAndEntrezIdColIdx(String[] fields) throws IOException {
    int hgncIdx = -1;
    int symbolIdx = -1;
    int entrezIdx = -1;
    int i = 0;
    for (String field : fields) {
      if ("symbol".equals(field)) {
        symbolIdx = i++;
      } else if ("entrez_id".equals(field)) {
        entrezIdx = i++;
      } else if ("hgnc_id".equals(field)) {
        hgncIdx = i++;
      } else {
        i++;
      }
      if (hgncIdx >= 0 && symbolIdx >= 0 && entrezIdx >= 0) {
        int maxIdx = Arrays.stream(new int[]{hgncIdx, symbolIdx, entrezIdx}).max().orElseThrow();
        return new ColIndices(hgncIdx, symbolIdx, entrezIdx, maxIdx);
      }
    }
    throw new IOException("Could not find the col indices in the header");
  }

  static class ColIndices {
    final int hgncIdx;
    final int symbolIdx;
    final int entrezIdIdx;
    final int maxIdx;

    private ColIndices(int hgncIdx, int symbolIdx, int entrezIdIdx, int maxIdx) {
      this.hgncIdx = hgncIdx;
      this.symbolIdx = symbolIdx;
      this.entrezIdIdx = entrezIdIdx;
      this.maxIdx = maxIdx;
    }
  }
}
