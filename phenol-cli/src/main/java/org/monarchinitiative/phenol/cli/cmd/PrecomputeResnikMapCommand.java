package org.monarchinitiative.phenol.cli.cmd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoader;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaderOptions;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaders;
import org.monarchinitiative.phenol.cli.demo.MicaCalculator;
import org.monarchinitiative.phenol.io.MinimalOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.similarity.HpoResnikSimilarityPrecompute;
import org.monarchinitiative.phenol.ontology.similarity.TermPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

/**
 * Precompute MICA information content for HPO and {@link HpoDiseases} and write the information contents
 * of the term pairs into a table.
 */
@CommandLine.Command(name = "precompute-resnik",
  mixinStandardHelpOptions = true,
  description = "Precompute Resnik term pair similarity table")
public class PrecomputeResnikMapCommand implements Callable<Integer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PrecomputeResnikMapCommand.class);

  @CommandLine.Option(names = {"--hpo"},
    description = "path to hp.json file",
    required = true)
  public Path hpoPath;

  @CommandLine.Option(names = "--hpoa",
    description = "path to phenotype.hpoa file",
    required = true)
  public Path hpoaPath;

  @CommandLine.Option(names = {"--assume-annotated"},
    description = {"Assume that each term annotates at least one disease.", "This prevents IC=Infinity for the absent terms"})
  public boolean assumeAnnotated;

  @CommandLine.Option(names = {"--output"},
    description = "Where to write the term pair similarity table (default: ${DEFAULT-VALUE})")
  public Path output = Path.of("term-pair-similarity.csv.gz");

  @Override
  public Integer call() throws Exception {
    LOGGER.info("Loading HPO from {}", hpoPath.toAbsolutePath());
    MinimalOntology hpo = MinimalOntologyLoader.loadOntology(hpoPath.toFile());

    LOGGER.info("Loading HPO annotations from {}", hpoaPath.toAbsolutePath());
    HpoDiseaseLoader loader = HpoDiseaseLoaders.defaultLoader(hpo, HpoDiseaseLoaderOptions.defaultOptions());
    HpoDiseases diseases = loader.load(hpoaPath);

    LOGGER.info("Calculating information content using {} diseases", diseases.size());
    Map<TermId, Double> termToIc = calculateTermToIc(hpo, diseases);

    LOGGER.info("Assigning MICA information content to term pairs");
    Map<TermPair, Double> termPairResnikSimilarityMap = assignMicaToTermPairs(hpo, termToIc);

    LOGGER.info("Writing term pair similarity to {}", output.toAbsolutePath());
    LocalDate date = LocalDate.now();
    String hpoVersion = hpo.version().orElse("N/A");
    String hpoaVersion = diseases.version().orElse("N/A");
    writeTermPairMap(termPairResnikSimilarityMap, date, hpoVersion, hpoaVersion);

    LOGGER.info("Done!");
    return 0;
  }

  private Map<TermId, Double> calculateTermToIc(MinimalOntology hpo, HpoDiseases diseases) {
    MicaCalculator micaCalculator = new MicaCalculator(hpo, assumeAnnotated);
    return micaCalculator.calculateMica(diseases).termToIc();
  }

  private static Map<TermPair, Double> assignMicaToTermPairs(MinimalOntology hpo, Map<TermId, Double> termToIc) {
    return HpoResnikSimilarityPrecompute.precomputeSimilaritiesForTermPairs(hpo, termToIc);
  }

  private void writeTermPairMap(Map<TermPair, Double> termPairResnikSimilarityMap,
                                LocalDate now,
                                String hpoVersion,
                                String hpoaVersion) throws IOException {
    try (Writer writer = openWriter();
         CSVPrinter printer = CSVFormat.Builder.create(CSVFormat.DEFAULT)
           .setCommentMarker('#')
           .build()
           .print(writer)) {
      // Metadata
      printer.printComment("Information content of the most informative common ancestor for term pairs");
      printer.printComment(String.format("HPO=%s;HPOA=%s;CREATED=%s", hpoVersion, hpoaVersion, now));

      // Header
      printer.printRecord("term_a", "term_b", "ic_mica");

      // Content
      for (Map.Entry<TermPair, Double> e : termPairResnikSimilarityMap.entrySet()) {
        TermPair pair = e.getKey();
        printer.print(pair.getTidA().getValue());
        printer.print(pair.getTidB().getValue());
        printer.print(e.getValue());
        printer.println();
      }
    }
  }

  private Writer openWriter() throws IOException {
    return output.toFile().getName().endsWith(".gz")
      ? new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(output))))
      : Files.newBufferedWriter(output);
  }
}
