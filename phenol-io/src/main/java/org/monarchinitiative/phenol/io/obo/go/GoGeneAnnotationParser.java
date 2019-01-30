package org.monarchinitiative.phenol.io.obo.go;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;

/**
 * Parser for GO "gene annotation file" (GAF) format.
 *
 * <p><b>Usage Example</b>
 *
 * <pre>
 * File inputFile = "goa_human.gaf";
 * try {
 *   GoGeneAnnotationFileParser parser = new GoGeneAnnotationFileParser(inputFile);
 *   List<GoGaf21Annotation> = parser.getAnnotations();
 *   ...
 *   }
 * } except (PhenolException e) {
 *   System.err.println("Problem reading from file.");
 * }
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public final class GoGeneAnnotationParser  {

  /** The {@link File} to read from. */
  private final File file;

  /** The {@link BufferedReader} to use for reading line-wise. */
  private final BufferedReader reader;

  /** The next line. */
  private String nextLine;

  private final List<GoGaf21Annotation> annotations;

  /**
   * Create new parser for GO gene annotation file.
   *
   * @param file The file to read from.
   * @throws PhenolException In case of problems with opening and reading from <code>file</code>.
   */
  public GoGeneAnnotationParser(File file) throws PhenolException {
    this.file = file;
    try {
      this.reader = new BufferedReader(new FileReader(file));
      this.nextLine = reader.readLine();
      skipHeaderAndCheckFirst();
      ImmutableList.Builder<GoGaf21Annotation> builder = new ImmutableList.Builder<>();
      while (hasNext()) {
        GoGaf21Annotation annot = next();
        builder.add(annot);
      }
      reader.close();
      annotations=builder.build();
    } catch (IOException | TermAnnotationParserException e) {
      String msg=String.format("Could not parse GO annotation file: %s",e.getMessage());
      throw new PhenolException(msg);
    }
  }

  public GoGeneAnnotationParser(String absolutepath)throws PhenolException {
    this(new File(absolutepath));
  }

  /**
   * Skip all header lines and check next data line.
   *
   * @throws TermAnnotationParserException If the first line is not as expected.
   * @throws IOException If there is a problem with reading from the file.
   */
  private void skipHeaderAndCheckFirst() throws TermAnnotationParserException, IOException {
    skipUntilData();
    if (nextLine == null) {
      throw new TermAnnotationParserException("GAF2.1 file contained no data!");
    }
    final String[] arr = nextLine.split("\t");
    if (arr.length < 15 || arr.length > 17) {
      throw new TermAnnotationParserException(
          "First line of file had "
              + arr.length
              + " columns, but expected between 15 and 17 entries.");
    }
  }

  /*
   * Skip comment lines until data
   *
   */

  private void skipUntilData() throws IOException {
    while (nextLine == null || nextLine.startsWith("!")) {
      nextLine = reader.readLine();
    }
  }

  public  List<GoGaf21Annotation> getAnnotations() {
    return this.annotations;
  }

  /**
   * @return Return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  public  List<TermAnnotation> getTermAnnotations() {
    List<TermAnnotation> talist = new ArrayList<>(this.annotations);
    return talist;
  }

  /** Use an iterator paradigm internally to parse the file. */
  private boolean hasNext() {
    return nextLine != null;
  }

  /** Use an iterator paradigm internally to parse the file. */
  private GoGaf21Annotation next() throws IOException, PhenolException {
    skipUntilData();
    final String[] arr = nextLine.split("\t");
    GoGaf21Annotation annot = new GoGaf21Annotation(arr);
    nextLine = reader.readLine();
    return annot;
  }


  public File getFile() {
    return file;
  }
}
