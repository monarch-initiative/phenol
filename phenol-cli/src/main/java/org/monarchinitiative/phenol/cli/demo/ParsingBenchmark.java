package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * App for benchmarking parsing of ontology files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class ParsingBenchmark {

    /**
     * Sleep to wait for VisualVM.
     */
    private static final int SLEEP = 30;

    /**
     * Number of repetitions to perform.
     */
    private static final int REPETITIONS = 5;



    /**
     * OBO file to parse.
     */
    private File goFile, hpoFile, mpoFile;

    /**
     * File to write serialized data to, if any.
     */
    private File outputSerFile;

    /**
     * Construct with argument list.
     *

     */
    public ParsingBenchmark(ParsingBenchmark.Options options) {
      this.goFile = new File(options.getGoPath());
      this.mpoFile = new File(options.getMpoPath());
      this.hpoFile = new File(options.getHpoPath());
      this.outputSerFile = new File(options.serializeFileName());
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
      if (goFile != null && goFile.exists()) {
        parseGoObo();
      } else if (hpoFile != null && hpoFile.exists()){
        parseHpoObo();
      } else if (mpoFile != null && mpoFile.exists()) {
        parseMpoObo();
      }
    }



    private void parseHpoObo() {
      System.err.println("Parsing HPO OBO...");
      long startTime = System.nanoTime();
      Ontology hpo = OntologyLoader.loadOntology(hpoFile);

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
      Ontology mpo = OntologyLoader.loadOntology(mpoFile);

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

      Ontology go = OntologyLoader.loadOntology(goFile, "GO");

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
           ObjectInputStream ois = new ObjectInputStream(fin)) {
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




    @Parameters(commandDescription = "Parsing benchmark")
    public static class Options {
      @Parameter(names = {"-g"}, description = "path to go.obo file")
      private String goPath;
      @Parameter(names = {"-h"}, description = "path to hp.obo file")
      private String hpoPath;
      @Parameter(names = {"-m"}, description = "path to mp.obo file")
      private String mpoPath;
      @Parameter(names="-s", description="serialize file?")
      private String outputSerFile="output.ser";

      String getGoPath() {
        return goPath==null?"":goPath;
      }

      String getHpoPath() {
        return hpoPath==null?"":hpoPath;
      }

      String getMpoPath() {
        return mpoPath==null?"":mpoPath;
      }

      String serializeFileName() {
        return outputSerFile;
      }
    }
}

