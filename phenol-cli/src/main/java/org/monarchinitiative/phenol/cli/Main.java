package org.monarchinitiative.phenol.cli;

import org.monarchinitiative.phenol.cli.cmd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "phenol demo",
  mixinStandardHelpOptions = true,
  version = "2.1.2",
  description = "phenol demo programs")
public class Main implements Callable<Integer> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  public static void main(String[] args) {
    if (args.length == 0) {
      // if the user doesn't pass any command or option, add -h to show help
      args = new String[]{"-h"};
    }
    CommandLine cline = new CommandLine(new Main())
      .addSubcommand("go-demo", new GoCommand())
      .addSubcommand("hp-demo", new HpDemoCommand())
      .addSubcommand("mondo-demo", new MondoDemoCommand())
      .addSubcommand("mpo", new MpDemoCommand())
      .addSubcommand("precompute-scores", new PrecomputeScoresCommand())
      .addSubcommand("resnik-gene", new ResnikCommand())
      .addSubcommand("precompute-resnik", new PrecomputeResnikMapCommand())
      ;
    cline.setToggleBooleanFlags(false);
    int exitCode = cline.execute(args);
    System.exit(exitCode);
  }





  @Override
  public Integer call() {
    // work done in subcommands
    return 0;
  }


}
