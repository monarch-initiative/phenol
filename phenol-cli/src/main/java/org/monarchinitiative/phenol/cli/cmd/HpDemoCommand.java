package org.monarchinitiative.phenol.cli.cmd;

import org.monarchinitiative.phenol.cli.demo.HpDemo;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "hp-demo",
  mixinStandardHelpOptions = true,
  description = "HP enrichment")
public class HpDemoCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-o","--obo"}, description = "path to hp.obo file", required = true)
    private String hpoPath;
    @CommandLine.Option(names = {"-a","--annot"}, description = "path to HPO annotation file (phenotyoe.hpoa", required = true)
    private String annotPath;




  @Override
  public Integer call() throws IOException {
    HpDemo hp = new HpDemo(hpoPath, annotPath);
    hp.run();
    return 0;
  }
}
