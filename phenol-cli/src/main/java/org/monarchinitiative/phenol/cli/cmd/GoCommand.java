package org.monarchinitiative.phenol.cli.cmd;

import org.monarchinitiative.phenol.cli.demo.GoEnrichmentDemo;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "go-demo",
  mixinStandardHelpOptions = true,
  description = "Go enrichment")
public class GoCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-o", "--obo"}, description = "path to go.obo file", required = true)
  private String pathGoObo;
  @CommandLine.Option(names = {"-a", "--annot"}, description = "path to go association file (e.g., goa_human.gaf", required = true)
  private String pathGoGaf;
  /**
   * For the demo, We will create a study set that has 1/3 of the genes associated with this term
   * and three times as many other terms. The default GO:0070997 is 'neuron death'.
   */
  @CommandLine.Option(names = {"-i", "--id"}, description = "term ID to search for enrichment")
  private String goTermId = "GO:0097190";

  @Override
  public Integer call() {
    GoEnrichmentDemo go = new GoEnrichmentDemo(pathGoObo, pathGoGaf, goTermId);
    go.run();
    return 0;
  }
}
