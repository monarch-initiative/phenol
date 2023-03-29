package org.monarchinitiative.phenol.cli.cmd;


import org.monarchinitiative.phenol.cli.demo.MpEnrichmentDemo;
import picocli.CommandLine;

import java.util.concurrent.Callable;
@CommandLine.Command(name = "mp-demo",
  mixinStandardHelpOptions = true,
  description = "MP enrichment")
public class MpDemoCommand implements Callable<Integer> {


  @CommandLine.Option(names = {"-o", "--obo"}, description = "path to mp.obo file", required = true)
  public String oboPath;

  @CommandLine.Option(names = {"-a", "--annot"}, description = "path to association file (MGI_GenePheno.rpt)", required = true)
  public String annotPath;
  /**
   * For the demo, We will create a study set that has 1/3 of the genes associated with this term
   * and three times as many other terms. The default GO:0070997 is 'neuron death'.
   */
  @CommandLine.Option(names = {"-i", "--input"}, description = "list of gene symbols (study set)", required = true)
  public String inputGeneList;

  @CommandLine.Option(names = {"-m", "--marker"}, description = "Marker file, MRK_List2.rpt")
  public String markerFile;


  @Override
  public Integer call() {
    MpEnrichmentDemo mp = new MpEnrichmentDemo(oboPath, annotPath, inputGeneList, markerFile);
    mp.run();
    return 0;
  }
}
