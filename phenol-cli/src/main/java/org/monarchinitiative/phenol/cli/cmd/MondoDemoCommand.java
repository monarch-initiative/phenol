package org.monarchinitiative.phenol.cli.cmd;

import org.monarchinitiative.phenol.cli.demo.MondoDemo;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "mondo",
  mixinStandardHelpOptions = true,
  description = "MONDO demo")
public class MondoDemoCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-m", "--mondo"}, description = "path to mondo.obo file", required = true)
  private String mondoPath;
  @CommandLine.Option(names = {"-o", "--outfile"}, description = "name/path of output file", required = true)
  private String outPath;


  @Override
  public Integer call() {
    MondoDemo mondo = new MondoDemo(mondoPath, outPath);
    mondo.run();
    return 0;
  }
}
