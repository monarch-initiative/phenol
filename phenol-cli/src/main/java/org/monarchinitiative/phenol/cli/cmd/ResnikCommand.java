package org.monarchinitiative.phenol.cli.cmd;


import org.monarchinitiative.phenol.cli.demo.ResnikGenebasedHpoDemo;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;
@CommandLine.Command(name = "resnik",
  mixinStandardHelpOptions = true,
  description = "Resnik")
public class ResnikCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"--hpo"}, description = "path to hp.obo file", required = true)
  public Path hpoPath;
  @CommandLine.Option(names = "-a", description = "path to phenotype.hpoa file", required = true)
  public Path phenotypeDotHpoaPath;
  @CommandLine.Option(names = "--geneinfo", description = "path to downloaded file ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens_gene_info.gz")
  public Path geneInfoPath;
  @CommandLine.Option(names = "--mimgene2medgen", description = "path to downloaded file from ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen")
  public Path mim2genMedgenPath;


  @Override
  public Integer call() {
    ResnikGenebasedHpoDemo resnik = new ResnikGenebasedHpoDemo(hpoPath, phenotypeDotHpoaPath, geneInfoPath, mim2genMedgenPath);
    resnik.run();
    return 0;
  }
}
