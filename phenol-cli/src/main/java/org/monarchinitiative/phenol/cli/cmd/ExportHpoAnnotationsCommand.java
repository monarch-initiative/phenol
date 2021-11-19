package org.monarchinitiative.phenol.cli.cmd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.io.HpoAnnotationLoader;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

@CommandLine.Command(name = "export-hpo-annotations",
  mixinStandardHelpOptions = true,
  description = "Export HPO disease annotations into a long CSV table")
public class ExportHpoAnnotationsCommand implements Callable<Integer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportHpoAnnotationsCommand.class);

  @CommandLine.Parameters(index = "0", paramLabel = "phenotype.hpoa", description = "Path to phenotype.hpoa file")
  public File hpoAnnotationsPath;

  @CommandLine.Parameters(index = "1", paramLabel = "hpo.obo", description = "Path to HPO OBO file")
  public File hpoOntologyPath;

  @CommandLine.Parameters(index = "2", paramLabel = "output.csv", description = "Where to write the CSV output file")
  public File outputPath;

  private static BufferedWriter openOutputFile(Path outputPath) throws IOException {
    return outputPath.toFile().getName().endsWith(".gz")
      ? new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outputPath))))
      : Files.newBufferedWriter(outputPath);
  }

  @Override
  public Integer call() throws Exception {
    LOGGER.info("Running `export-hpo-annotations` command");
    LOGGER.info("Loading ontology from `{}`", hpoOntologyPath.getAbsolutePath());
    Ontology ontology = OntologyLoader.loadOntology(hpoOntologyPath);

    LOGGER.info("Loading HPO disease annotations from `{}`", hpoAnnotationsPath.getAbsolutePath());
    Map<TermId, HpoDisease> termIdHpoDiseaseMap = HpoAnnotationLoader.loadDiseaseMap(hpoAnnotationsPath, ontology);

    return processFiles(termIdHpoDiseaseMap, outputPath);
  }

  private int processFiles(Map<TermId, HpoDisease> termIdHpoDiseaseMap, File outputPath) {
    LOGGER.info("Writing annotations to `{}`", outputPath.getAbsolutePath());
    try (BufferedWriter writer = openOutputFile(outputPath.toPath());
         CSVPrinter printer = CSVFormat.Builder.create(CSVFormat.DEFAULT)
           .setHeader("DiseaseId", "HpoId", "OnsetId", "Numerator", "Denominator")
           .build().print(writer)) {

      for (HpoDisease disease : termIdHpoDiseaseMap.values()) {
        disease.phenotypicAbnormalities().forEachOrdered(annotation -> {

          for (HpoDiseaseAnnotationMetadata meta : annotation.metadata()) {
            TermId onsetTermId = meta.onset().toTermId();

            String numerator = "NaN", denominator = "NaN";
            Optional<Ratio> ratioOptional = meta.frequency().nOfMProbands();
            if (ratioOptional.isPresent()) {
              Ratio ratio = ratioOptional.get();
              numerator = String.valueOf(ratio.numerator());
              denominator = String.valueOf(ratio.denominator());
            }

            try {
              printer.printRecord(
                disease.diseaseDatabaseTermId().getValue(), // DiseaseId
                annotation.termId().getValue(), // HpoId
                onsetTermId.getValue(), // OnsetId
                numerator, // Numerator
                denominator // Denominator
              );
            } catch (IOException e) {
              LOGGER.warn("Error writing annotation `{}`", annotation, e);
            }
          }
        });
      }
    } catch (IOException e) {
      LOGGER.error("Error: {}", e.getMessage(), e);
      return 1;
    }
    LOGGER.info("Done");
    return 0;
  }


}
