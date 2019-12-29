/*package org.monarchinitiative.phenol.io.obo.upheno;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.monarchinitiative.phenol.io.base.TermAnnotationParser;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;

/**
 * Parser for Upheno gene annotation files.
 *
 * <p><b>Usage Example</b>
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
//public final class UphenoGeneAnnotationParser implements TermAnnotationParser<TermAnnotation> {
//
//  /** The {@link File} to read from. */
//  private final File file;
//
//  /** The {@link BufferedReader} to use for reading line-wise. */
//  private final BufferedReader reader;
//
//  /** The next line. */
//  private String nextLine;
//
//  /**
//   * Create new parser for Upheno gene annotation file.
//   *
//   * @param file The file to read from.
//   * @throws IOException In case of problems with opening and reading from <code>file</code>.
//   */
//  public UphenoGeneAnnotationParser(File file) throws IOException {
//    this.file = file;
//    this.reader = new BufferedReader(new FileReader(file));
//    this.nextLine = reader.readLine();
//  }
//
//  @Override
//  public boolean hasNext() {
//    return nextLine != null;
//  }
//
//  @Override
//  public TermAnnotation next() {
//    throw new UnsupportedOperationException();
//  }
//
//  @Override
//  public void close() throws IOException {
//    reader.close();
//  }
//
//  @Override
//  public File getFile() {
//    return file;
//  }
//}
