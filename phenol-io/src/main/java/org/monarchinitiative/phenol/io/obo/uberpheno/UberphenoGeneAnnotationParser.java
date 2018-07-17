//package org.monarchinitiative.phenol.io.obo.uberpheno;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//
//import org.monarchinitiative.phenol.formats.uberpheno.UberphenoGeneAnnotation;
//import org.monarchinitiative.phenol.io.base.TermAnnotationParser;
//import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
//import org.monarchinitiative.phenol.ontology.data.TermId;
//
//
///**
// * Parser for "genes to phenotype annotation" files.
// *
// * <p><b>Usage Example</b>
// *
// * <pre>
// * File inputFile = "genes_to_phenotype.txt";
// * try {
// *   UberphenoGeneAnnotationParser parser = new UberphenoGeneAnnotationParser(inputFile);
// *   while (parser.hasNext()) {
// *     UberphenoGeneAnnotation anno = parser.next();
// *     // ...
// *   }
// * } except (IOException e) {
// *   System.err.println("Problem reading from file.");
// * } except (TermAnnotationException e) {
// *   System.err.println("Problem parsing file.");
// * }
// * </pre>
// *
// * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
// */
//public class UberphenoGeneAnnotationParser
//    implements TermAnnotationParser<UberphenoGeneAnnotation> {
//
//  /** Expected header string, first line. */
//  private static final String EXPECTED_HEADER1 =
//      "#Entrez Gene ID of human gene ; Gene symbol ; Annotated Uberpheno term ; Evidence";
//
//  /** Expected header string, second line. */
//  private static final String EXPECTED_HEADER2 =
//      "# For annotations stemming from human diseases 'Evidence' contains the Disease-ID "
//          + "(e.g. OMIM or Orphanet-ID";
//
//  /** Expected header string, third line. */
//  private static final String EXPECTED_HEADER3 =
//      "# For annotations stemming from model organisms 'Evidence' contains the organism "
//          + "name and the ID of the gene in the model organism";
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
//   * Create new parser for Uberpheno gene annotation files.
//   *
//   * @param file The file to read from.
//   * @throws IOException In case of problems with opening and reading from <code>file</code>.
//   * @throws TermAnnotationParserException If there are problems with the file's header.
//   */
//  public UberphenoGeneAnnotationParser(File file)
//      throws IOException, TermAnnotationParserException {
//    this.file = file;
//    this.reader = new BufferedReader(new FileReader(file));
//    this.nextLine = reader.readLine();
//    checkHeader();
//  }
//
//  /**
//   * Read first three lines and check header.
//   *
//   * @throws TermAnnotationParserException If the header is not as expected.
//   * @throws IOException If there is a problem with reading from the file.
//   */
//  private void checkHeader() throws TermAnnotationParserException, IOException {
//    if (!EXPECTED_HEADER1.equals(nextLine)) {
//      throw new TermAnnotationParserException(
//          "First line is not the expected header. \""
//              + nextLine
//              + "\" vs. \""
//              + EXPECTED_HEADER1
//              + "\" (expected)");
//    }
//    nextLine = reader.readLine();
//
//    if (!EXPECTED_HEADER2.equals(nextLine)) {
//      throw new TermAnnotationParserException(
//          "Second line is not the expected header. \""
//              + nextLine
//              + "\" vs. \""
//              + EXPECTED_HEADER2
//              + "\" (expected)");
//    }
//    nextLine = reader.readLine();
//
//    if (!EXPECTED_HEADER3.equals(nextLine)) {
//      throw new TermAnnotationParserException(
//          "Third line is not the expected header. \""
//              + nextLine
//              + "\" vs. \""
//              + EXPECTED_HEADER3
//              + "\" (expected)");
//    }
//    nextLine = reader.readLine();
//  }
//
//  @Override
//  public boolean hasNext() {
//    return nextLine != null;
//  }
//
//  @Override
//  public UberphenoGeneAnnotation next() throws IOException, TermAnnotationParserException {
//    final String[] arr = nextLine.split(";");
//    final int geneId = Integer.parseInt(arr[0]);
//    final String geneSymbol = arr[1];
//    final String annotatedTerm = arr[2];
//    final String evidenceDescription = arr[3];
//
//    // Parse and split column "Annotated Uberpheno term".
//    final String[] pair = annotatedTerm.split(" \\(", 2);
//    if (pair.length != 2) {
//      throw new TermAnnotationParserException(
//          "Incorrect format for annotated uberpheno term, ' (' missing");
//    }
//    if (!pair[1].endsWith(")")) {
//      throw new TermAnnotationParserException(
//          "Incorrect format for annotated uberpheno term, trailing ')' missing");
//    }
//    final String termDescription = pair[0];
//    final TermId termId =
//        TermId.constructWithPrefix(pair[1].substring(0, pair[1].length() - 1));
//
//    nextLine = reader.readLine();
//
//    return new UberphenoGeneAnnotation(
//        geneId, geneSymbol, termDescription, termId, evidenceDescription);
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
