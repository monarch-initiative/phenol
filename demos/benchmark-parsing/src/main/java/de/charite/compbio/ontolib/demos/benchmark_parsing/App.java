package de.charite.compbio.ontolib.demos.benchmark_parsing;

import de.charite.compbio.ontolib.io.obo.OBOParser;
import de.charite.compbio.ontolib.io.obo.hpo.HPOOBOParser;
import java.io.File;
import java.io.IOException;

/**
 * App for benchmarking parsing of ontology files.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class App {

  /** Command line arguments. */
  private final String[] args;

  /** Command to executed. */
  private Command command;

  /** File to parse. */
  private File file;

  /**
   * Construct with argument list.
   * 
   * @param args Argument list.
   */
  public App(String[] args) {
    this.args = args;
  }

  /**
   * Run application.
   */
  public void run() {
    this.parseArgs();
    switch (command) {
      case PARSE_OBO:
        parseOBO();
        break;
      case PARSE_HPO_OBO:
        parseHPOOBO();
        break;
      default:
        printUsageError("I don't know about command " + command);
    }
  }

  private void parseOBO() {
    final long startTime = System.nanoTime();

    final OBOParser parser = new OBOParser();
    try {
      parser.parseFile(file);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    final long endTime = System.nanoTime();
    final double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Parsing OBO took " + duration + " seconds");
  }

  private void parseHPOOBO() {
    final long startTime = System.nanoTime();

    HPOOBOParser parser = new HPOOBOParser(file);
    try {
      parser.parse();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    final long endTime = System.nanoTime();
    final double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Parsing HPO OBO took " + duration + " seconds");
  }

  /**
   * Parse command line arguments.
   */
  private void parseArgs() {
    if (args.length != 2) {
      printUsageError("Invalid argument count!");
    }

    switch (args[0]) {
      case "--obo":
        command = Command.PARSE_OBO;
        break;
      case "--hpo-obo":
        command = Command.PARSE_HPO_OBO;
        break;
      default:
        printUsageError("Invalid first argument " + args[0]);
        return;
    }

    file = new File(args[1]);
  }

  /** Print error and usage, then exit. */
  private void printUsageError(String string) {
    System.err.println("ERROR: " + string + "\n");
    System.err.println("Usage: java -jar app.jar (--obo FILE.obo|--hpo-obo hp.obo)");
    System.exit(1);
  }

  /** Enumeration for describing current command. */
  enum Command {
    /** Generic OBO parsing */
    PARSE_OBO,
    /** Parse HPO OBO file */
    PARSE_HPO_OBO;
  }

  /**
   * Program entry point.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    new App(args).run();
  }

}
