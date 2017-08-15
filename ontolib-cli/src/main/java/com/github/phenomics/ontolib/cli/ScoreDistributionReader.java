package com.github.phenomics.ontolib.cli;

import com.github.phenomics.ontolib.ontology.scoredist.ObjectScoreDistribution;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Helper class for reading in {@link ScoreDistribution} objects from text files.
 *
 * @see ScoreDistributionReader
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ScoreDistributionReader implements Closeable {

  /** Path to the file read from. */
  private final File inputFile;

  /** The {@link BufferedReader} to use. */
  private final BufferedReader reader;

  /** The currently read line. */
  private String nextLine;

  /**
   * Constructor.
   * 
   * @param inputFile Path to input file.
   * @throws IOException In case of problems with file I/O.
   */
  public ScoreDistributionReader(File inputFile) throws IOException {
    this.inputFile = inputFile;
    this.reader = new BufferedReader(new FileReader(this.inputFile));
    readHeader();
  }

  private void readHeader() throws IOException {
    nextLine = reader.readLine();
    if (nextLine == null) {
      throw new IOException("Could not read header from file!");
    }

    final String expected = "#numTerms\tentrezId\tsampleSize\tdistribution";
    if (!expected.equals(nextLine)) {
      throw new IOException(
          "Invalid header, was: \"" + nextLine + "\", expected: \"" + expected + "\"");
    }

    nextLine = reader.readLine();
  }

  /**
   * Read all entries and return mapping from term count to {@link ScoreDistribution} object.
   *
   * @return Resulting score distributions from the file.
   * @throws IOException In the case of problems read reading and parsing.
   */
  public Map<Integer, ScoreDistribution> readAll() throws IOException {
    final Map<Integer, ScoreDistribution> result = new HashMap<>();

    final Map<Integer, Map<Integer, ObjectScoreDistribution>> tmp = new HashMap<>();

    while (nextLine != null) {
      final String[] arr = nextLine.trim().split("\t");
      final int numTerms = Integer.parseInt(arr[0]);
      final int entrezId = Integer.parseInt(arr[1]);
      final int sampleSize = Integer.parseInt(arr[2]);
      final String dist = arr[3];

      final TreeMap<Double, Double> cumFreqs = new TreeMap<>();
      for (String pairStr : dist.split(",")) {
        final String[] pair = pairStr.split(":");
        cumFreqs.put(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
      }

      final ObjectScoreDistribution scoreDist =
          new ObjectScoreDistribution(entrezId, numTerms, sampleSize, cumFreqs);

      if (!tmp.containsKey(numTerms)) {
        tmp.put(numTerms, new TreeMap<>());
      }
      tmp.get(numTerms).put(entrezId, scoreDist);

      nextLine = reader.readLine();
    }
    
    for (Entry<Integer, Map<Integer, ObjectScoreDistribution>> e : tmp.entrySet()) {
      final int numTerms = e.getKey();
      result.put(numTerms, new ScoreDistribution(numTerms, e.getValue()));
    }

    return result;
  }

  @Override
  public void close() throws IOException {
    this.reader.close();
  }

}
