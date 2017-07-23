package com.github.phenomics.ontolib.demos.benchmark_parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.phenomics.ontolib.formats.go.GoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.mpo.MpoOntology;
import com.github.phenomics.ontolib.io.obo.OboParser;
import com.github.phenomics.ontolib.io.obo.go.GoOboParser;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.io.obo.mpo.MpoOboParser;

/**
 * App for benchmarking parsing of ontology files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class App {

  /** Sleep to wait for VisualVM. */
  private static final int SLEEP = 30;

  /** Number of repetitions to perform. */
  private static final int REPETITIONS = 5;

  /** Command line arguments. */
  private final String[] args;

  /** Command to executed. */
  private Command command;

  /** OBO file to parse. */
  private File inputFile;

  /** File to write serialized data to, if any. */
  private File outputSerFile;

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
    if (SLEEP > 0) {
      System.err.println("Waiting for " + SLEEP + " seconds for VisualVM...");
      try {
        Thread.sleep(10_000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
    this.parseArgs();

    for (int i = 0; i < REPETITIONS; ++i) {
      switch (command) {
        case PARSE_OBO:
          parseObo();
          break;
        case PARSE_GO_OBO:
          parseGoObo();
          break;
        case PARSE_HPO_OBO:
          parseHpoObo();
          break;
        case PARSE_MPO_OBO:
          parseMpoObo();
          break;
        default:
          printUsageError("I don't know about command " + command);
          break;
      }
    }
  }

  private void parseObo() {
    System.err.println("Parsing OBO...");
    final long startTime = System.nanoTime();

    final OboParser parser = new OboParser();
    try {
      parser.parseFile(inputFile);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    final long endTime = System.nanoTime();
    final double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Parsing OBO took " + duration + " seconds");
  }

  private void parseHpoObo() {
    System.err.println("Parsing HPO OBO...");
    long startTime = System.nanoTime();

    HpoOboParser parser = new HpoOboParser(inputFile);
    HpoOntology hpo;
    try {
      hpo = parser.parse();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac does not understand this is unreachable
    }

    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Parsing HPO OBO took " + duration + " seconds");

    if (outputSerFile != null) {
      writeAndLoadSer(hpo);
    }
  }

  private void parseMpoObo() {
    System.err.println("Parsing MPO OBO...");
    long startTime = System.nanoTime();

    MpoOboParser parser = new MpoOboParser(inputFile);
    MpoOntology mpo;
    try {
      mpo = parser.parse();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac does not understand this is unreachable
    }

    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Parsing MPO OBO took " + duration + " seconds");

    if (outputSerFile != null) {
      writeAndLoadSer(mpo);
    }
  }

  private void parseGoObo() {
    System.err.println("Parsing GO OBO...");
    long startTime = System.nanoTime();

    GoOboParser parser = new GoOboParser(inputFile);
    GoOntology go;
    try {
      go = parser.parse();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac does not understand this is unreachable
    }

    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Parsing GO OBO took " + duration + " seconds");

    if (outputSerFile != null) {
      writeAndLoadSer(go);
    }
  }

  private void writeAndLoadSer(Object o) {
    System.out.println("Writing .ser file " + outputSerFile + "...");
    long startTime = System.nanoTime();
    try (FileOutputStream fout = new FileOutputStream(outputSerFile);
        ObjectOutputStream oos = new ObjectOutputStream(fout)) {
      oos.writeObject(o);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.err.println("Done writing .ser file.");
    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Writing .ser took " + duration + " seconds");

    System.out.println("Loading .ser file " + outputSerFile + "...");
    startTime = System.nanoTime();
    try (FileInputStream fin = new FileInputStream(outputSerFile);
        ObjectInputStream ois = new ObjectInputStream(fin);) {
      ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.err.println("Done loading .ser file.");
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1_000_000_000.0;
    System.out.println("Reading .ser took " + duration + " seconds");
  }

  /**
   * Parse command line arguments.
   */
  private void parseArgs() {
    if (args.length != 2 && args.length != 4) {
      printUsageError("Invalid argument count!");
    }

    for (int i = 0; i < args.length; i += 2) {
      switch (args[i]) {
        case "--obo":
          inputFile = new File(args[i + 1]);
          command = Command.PARSE_OBO;
          break;
        case "--go-obo":
          command = Command.PARSE_GO_OBO;
          inputFile = new File(args[i + 1]);
          break;
        case "--hpo-obo":
          command = Command.PARSE_HPO_OBO;
          inputFile = new File(args[i + 1]);
          break;
        case "--output-ser":
          outputSerFile = new File(args[i + 1]);
          break;
        default:
          printUsageError("Invalid argument " + args[i]);
          return;
      }
    }
  }

  /** Print error and usage, then exit. */
  private void printUsageError(String string) {
    System.err.println("ERROR: " + string + "\n");
    System.err.println("Usage: java -jar app.jar [--output-ser FILE.ser] "
        + "(--obo FILE.obo|--[go|hpo|mpo]-obo FILE.obo)");
    System.exit(1);
  }

  /** Enumeration for describing current command. */
  enum Command {
    /** Generic OBO parsing */
    PARSE_OBO,
    /** Parse GO OBO file */
    PARSE_GO_OBO,
    /** Parse HPO OBO file */
    PARSE_HPO_OBO,
    /** Parse MPO OBO file */
    PARSE_MPO_OBO;
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
