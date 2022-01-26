package org.monarchinitiative.phenol.cli.cmd;

import org.monarchinitiative.phenol.cli.demo.PairwisePhenotypicSimilarityCalculator;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "pairwise",
  mixinStandardHelpOptions = true,
  description = "Pairwise phenotype enrichment")
public class PairwisePhenoCommand implements Callable<Integer> {


  @CommandLine.Option(names = {"-h"}, description = "path to hp.obo file", required = true)
  public Path hpoPath;
  @CommandLine.Option(names = "-a", description = "path to phenotype.hpoa file", required = true)
  public Path phenotypeDotHpoaPath;
  @CommandLine.Option(names = "-o", description = "output file name")
  public Path outname = Path.of("pairwise_disease_similarity.tsv");
  @CommandLine.Option(names = "--geneinfo", description = "path to downloaded file ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz")
  public Path geneInfoPath;
  @CommandLine.Option(names = "--mimgene2medgen", description = "path to downloaded file from ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen")
  public Path mim2genMedgenPath;


  @Override
  public Integer call() throws IOException {
    PairwisePhenotypicSimilarityCalculator pairwise = new PairwisePhenotypicSimilarityCalculator(hpoPath,
      phenotypeDotHpoaPath,
      outname,
      geneInfoPath,
      mim2genMedgenPath);
    pairwise.run();
    return 0;
  }
}
