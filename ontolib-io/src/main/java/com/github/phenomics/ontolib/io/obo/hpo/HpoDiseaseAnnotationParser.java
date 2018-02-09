package com.github.phenomics.ontolib.io.obo.hpo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.github.phenomics.ontolib.formats.hpo.HpoDiseaseAnnotation;
import com.github.phenomics.ontolib.io.base.TermAnnotationParser;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.google.common.base.Enums;
import com.google.common.collect.ImmutableList;

/**
 * Parser for "phenotype annotation" files.
 *
 * <p>
 * <b>Usage Example</b>
 * </p>
 *
 * <pre>
 * File inputFile = "phenotype_annotation.tab";
 * try {
 *   HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(inputFile);
 *   while (parser.hasNext()) {
 *     HpoDiseaseAnnotation anno = parser.next();
 *     // ...
 *   }
 * } except (IOException e) {
 *   System.err.println("Problem reading from file.");
 * } except (TermAnnotationException e) {
 *   System.err.println("Problem parsing file.");
 * }
 * </pre>
 * Note this parser is to be replaced by {@link HpoAnnotation2DiseaseParser} which includes the metadata.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Deprecated
public class HpoDiseaseAnnotationParser implements TermAnnotationParser<HpoDiseaseAnnotation> {

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
   * Create new parser for HPO gene annotation files.
   *
   * @param file The file to read from.
   *
   * @throws IOException In case of problems with opening and reading first line from
   *         <code>file</code>.
   * @throws TermAnnotationParserException If there are problems with the file's first line.
   */
  public HpoDiseaseAnnotationParser(File file) throws IOException, TermAnnotationParserException {
    this.file = file;
    this.reader = new BufferedReader(new FileReader(file));
    this.nextLine = reader.readLine();
    checkFirstLine();
  }

  /**
   * Perform basic checks on the first line of the file.
   *
   * @throws TermAnnotationParserException In case of problems with the first line in the file.
   */
  private void checkFirstLine() throws TermAnnotationParserException {
    final String[] arr = nextLine.split("\t");
    if (arr.length != 14) {
      throw new TermAnnotationParserException(
          "Does not look like HPO disease annotation file. Invalid number of fields in first "
              + "line, expected 14, was " + arr.length);
    }
    if (!Enums.getIfPresent(HpoDiseaseAnnotation.DatabaseSource.class, arr[0]).isPresent()) {
      throw new TermAnnotationParserException(
          "Does not look like HPO disease annotation file. First field value was " + arr[0]
              + " but was expected to be one of "
              + Arrays.toString(HpoDiseaseAnnotation.DatabaseSource.values()));
    }
  }

  @Override
  public boolean hasNext() {
    return nextLine != null;
  }

  @Override
  public HpoDiseaseAnnotation next() throws TermAnnotationParserException, IOException {
    final String[] arr = nextLine.split("\t");
    if (arr.length != 14) {
      throw new TermAnnotationParserException(
          "Does not look like HPO disease annotation file. Invalid number of fields, expected "
              + "14 but was " + arr.length);
    }

    final String db = arr[0];
    final String dbObjectId = arr[1];
    final String dbName = arr[2];
    final String qualifier = arr[3];
    final ImmutableTermId hpoId = ImmutableTermId.constructWithPrefix(arr[4]);
    final String dbReference = arr[5];
    final String evidenceCode = arr[6];
    final String onsetModifier = arr[7];
    final String frequencyModifier = arr[8];
    final String with = arr[9];
    final String aspect = arr[10];
    final String synonym = arr[11];
    final String dateStr = arr[12];
    final String assignedBy = arr[13];

    nextLine = reader.readLine();

    final ImmutableList<String> formatStrings = ImmutableList.of("yyyy.MM.dd", "yyyy-MM-dd");
    Date date = null;
    for (String formatString : formatStrings) {
      final SimpleDateFormat format = new SimpleDateFormat(formatString);
      try {
        date = format.parse(dateStr);
        break;
      } catch (ParseException e) {
        continue; // swallow
      }
    }
    if (date == null) {
      throw new TermAnnotationParserException(
          "There was a problem parsing the date value " + dateStr);
    }

    return new HpoDiseaseAnnotation(db, dbObjectId, dbName, qualifier, hpoId, dbReference,
        evidenceCode, onsetModifier, frequencyModifier, with, aspect, synonym, date, assignedBy);
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
