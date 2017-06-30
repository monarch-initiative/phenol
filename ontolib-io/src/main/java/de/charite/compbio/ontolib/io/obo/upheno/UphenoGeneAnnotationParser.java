package de.charite.compbio.ontolib.io.obo.upheno;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.charite.compbio.ontolib.io.base.TermAnnotationParser;
import de.charite.compbio.ontolib.io.base.TermAnnotationParserException;
import de.charite.compbio.ontolib.ontology.data.TermAnnotation;

/**
 * Parser for Upheno gene annotation files.
 *
 * <p>
 * <b>Usage Example</b>
 * </p>
 *
 * <pre>
 * File inputFile = "XXX.txt";
 * try {
 *   UphenoGeneAnnotationFileParser parser = new UphenoGeneAnnotationFileParser(inputFile);
 *   while (parser.hasNext()) {
 *     UphenoAnnotation anno = parser.next();
 *     // ...
 *   }
 * } except (IOException e) {
 *   System.err.println("Problem reading from file.");
 * } except (TermAnnotationException e) {
 *   System.err.println("Problem parsing file.");
 * }
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class UphenoGeneAnnotationParser implements TermAnnotationParser<TermAnnotation> {

  /**
   * The {@link File} to read from.
   */
  private final File file;

  /**
   * The {@link BufferedReader} to use for reading line-wise.
   */
  private final BufferedReader reader;

  /** The next line. */
  private String nextLine;

  /**
   * Create new parser for Upheno gene annotation file.
   *
   * @param file The file to read from.
   *
   * @throws IOException In case of problems with opening and reading from <code>file</code>.
   * @throws TermAnnotationParserException If there are problems with the file's header.
   */
  public UphenoGeneAnnotationParser(File file) throws IOException, TermAnnotationParserException {
    this.file = file;
    this.reader = new BufferedReader(new FileReader(file));
    this.nextLine = reader.readLine();
  }

  @Override
  public boolean hasNext() {
    return nextLine != null;
  }

  @Override
  public TermAnnotation next() throws IOException, TermAnnotationParserException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public File getFile() {
    return file;
  }

}
