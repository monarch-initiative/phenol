package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    List<GeneIdentifier> identifiers = br.lines()
      .skip(1) // header
      .map(toGeneIdentifier())
      .flatMap(Optional::stream)
      .collect(Collectors.toUnmodifiableList());

    return GeneIdentifiers.of(identifiers);
  }

  static Function<String, Optional<GeneIdentifier>> toGeneIdentifier() {
    return line -> {
      String[] token = line.split("\t", -1);
      if (token.length <= 18) {
        LOGGER.warn(
          "Skipping malformed line: expected ≥ 19 columns but found {}. Line: {}",
          token.length, line
        );
        return Optional.empty();
      }
      // 1 - Entrez ID
      String id = token[18];
      if (id.isBlank()) {
        LOGGER.debug("Skipping line with missing Entrez ID '{}'", line);
        return Optional.empty();
      }
      TermId entrezId = TermId.of(ENTREZ_GENE_PREFIX, id);

      // 2 - HGVS symbol
      String symbol = token[1];

      return Optional.of(GeneIdentifier.of(entrezId, symbol));
    };
  }

}
