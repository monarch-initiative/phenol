package com.github.phenomics.ontolib.io.obo.go;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.github.phenomics.ontolib.formats.go.GoGaf21Annotation;
import com.github.phenomics.ontolib.io.base.TermAnnotationParser;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.google.common.collect.ImmutableList;

/**
 * Parser for GO "gene annotation file" (GAF) format.
 *
 * <p>
 * <b>Usage Example</b>
 * </p>
 *
 * <pre>
 * File inputFile = "goa_human.gaf";
 * try {
 *   GoGeneAnnotationFileParser parser = new GoGeneAnnotationFileParser(inputFile);
 *   while (parser.hasNext()) {
 *     GoAnnotation anno = parser.next();
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
public final class GoGeneAnnotationParser implements TermAnnotationParser<GoGaf21Annotation> {

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
   * Create new parser for GO gene annotation file.
   *
   * @param file The file to read from.
   *
   * @throws IOException In case of problems with opening and reading from <code>file</code>.
   * @throws TermAnnotationParserException If there are problems with the file's header.
   */
  public GoGeneAnnotationParser(File file) throws IOException, TermAnnotationParserException {
    this.file = file;
    this.reader = new BufferedReader(new FileReader(file));
    this.nextLine = reader.readLine();
    skipHeaderAndCheckFirst();
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
      throw new TermAnnotationParserException("First line of file had " + arr.length
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
  @Override
  public boolean hasNext() {
    return nextLine != null;
  }

  @Override
  public GoGaf21Annotation next() throws IOException, TermAnnotationParserException {
    skipUntilData();
    final String[] arr = nextLine.split("\t");
    if (arr.length < 15 || arr.length > 17) {
      throw new TermAnnotationParserException(
          "GAF line had " + arr.length + " columns, but expected between 15 and 17 entries. \n Line was:" + nextLine);
    }
    final String db = arr[0];
    final String dbObjectId = arr[1];
    final String dbObjectSymbol = arr[2];
    final String qualifier = arr[3];
    final ImmutableTermId goId = ImmutableTermId.constructWithPrefix(arr[4]);
    final String dbReference = arr[5];
    final String evidenceCode = arr[6];
    final String with = arr[7];
    final String aspect = arr[8];
    final String dbObjectName = arr[9];
    final String dbObjectSynonym = arr[10];
    final String dbObjectType = arr[11];
    final List<String> taxons = ImmutableList.copyOf(arr[12].split("\\|"));
    final String dateStr = arr[13];
    final String assignedBy = arr[14];
    final String annotationExtension = (arr.length < 16) ? null : arr[15];
    final String geneProductFormId = (arr.length < 17) ? null : arr[16];

    nextLine = reader.readLine();

    final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    Date date;
    try {
      date = format.parse(dateStr);
    } catch (ParseException e) {
      throw new TermAnnotationParserException(
          "There was a problem parsing the date value " + dateStr, e);
    }

    return new GoGaf21Annotation(db, dbObjectId, dbObjectSymbol, qualifier, goId, dbReference,
        evidenceCode, with, aspect, dbObjectName, dbObjectSynonym, dbObjectType, taxons, date,
        assignedBy, annotationExtension, geneProductFormId);
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
