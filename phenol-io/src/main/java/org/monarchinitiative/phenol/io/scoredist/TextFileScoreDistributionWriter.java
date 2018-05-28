package org.monarchinitiative.phenol.io.scoredist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.monarchinitiative.phenol.ontology.scoredist.ObjectScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import com.google.common.base.Joiner;

/**
 * Class for writing out {@link ScoreDistribution} objects to text files.
 *
 * @see TextFileScoreDistributionReader
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TextFileScoreDistributionWriter implements ScoreDistributionWriter {

  /** Path to the file to write to. */
  private final File outputFile;

  /** The FilePrinter to use. */
  private final PrintStream out;

  /**
   * Constructor.
   *
   * @param outputFile Path to output file.
   * @throws FileNotFoundException If {@code outputFile} could not be opened for writing.
   */
  public TextFileScoreDistributionWriter(File outputFile) throws FileNotFoundException {
    this.outputFile = outputFile;
    this.out = new PrintStream(this.outputFile);
    this.out.println("#numTerms\tentrezId\tsampleSize\tdistribution");
  }

  @Override
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
  public void close() {
    this.out.close();
  }
}
