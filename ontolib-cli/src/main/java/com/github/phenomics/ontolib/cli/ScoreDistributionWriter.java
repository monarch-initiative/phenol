package com.github.phenomics.ontolib.cli;

import com.github.phenomics.ontolib.ontology.scoredist.ObjectScoreDistribution;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;
import com.google.common.base.Joiner;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Helper class for writing out {@link ScoreDistribution} objects to text files.
 *
 * @see ScoreDistributionReader
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ScoreDistributionWriter implements Closeable {

  /** Path to the file to write to. */
  private final File outputFile;

  /** The {@link FilePrinter} to use. */
  private final PrintStream out;

  /**
   * Constructor.
   * 
   * @param outputFile Path to output file.
   * @throws FileNotFoundException If {@code outputFile} could not be opened for writing.
   */
  public ScoreDistributionWriter(File outputFile) throws FileNotFoundException {
    this.outputFile = outputFile;
    this.out = new PrintStream(this.outputFile);
    this.out.println("#numTerms\tentrezId\tsampleSize\tdistribution");
  }

  /**
   * Write out score distribution with resolution of {@code 100}.
   * 
   * @see #write(int, ScoreDistribution, int)
   */
  public void write(int numTerms, ScoreDistribution scoreDistribution) {
    write(numTerms, scoreDistribution, 100);
  }

  /**
   * Write out score distribution.
   *
   * @param numTerms Number of terms that {@code scoreDistribution} was computed for.
   * @param scoreDistribution The actual score distribution.
   * @param resolution The number of points to write out from the resolution; {@code 0} for no
   *        resampling.
   */
  public void write(int numTerms, ScoreDistribution scoreDistribution, int resolution) {
    for (int objectId : scoreDistribution.getObjectIds()) {
      final ObjectScoreDistribution dist = scoreDistribution.getObjectScoreDistribution(objectId);
      final List<Double> scores = dist.observedScores();
      final ArrayList<String> points = new ArrayList<>();
      if (resolution != 0) {
        for (int i = 0; i <= resolution; ++i) {
          final double pos = (((double) scores.size() - 1) / resolution) * i;
          final int left = Math.max(0, (int) Math.floor(pos));
          final int right = Math.min(scores.size() - 1, (int) Math.ceil(pos));
          final double dx = right - pos;
          final double score = scores.get(left) + (1 - dx) * (scores.get(right) - scores.get(left));
          final double pValue = dist.estimatePValue(score);
          points.add(score + ":" + pValue);
        }
      } else {
        for (Entry<Double, Double> e : dist.getCumulativeFrequencies().entrySet()) {
          points.add(e.getKey() + ":" + e.getValue());
        }
      }

      out.print(numTerms);
      out.print("\t");
      out.print(objectId);
      out.print("\t");
      out.print(dist.getSampleSize());
      out.print("\t");
      out.println(Joiner.on(',').join(points));
    }
  }

  @Override
  public void close() throws IOException {
    this.out.close();
  }

}
