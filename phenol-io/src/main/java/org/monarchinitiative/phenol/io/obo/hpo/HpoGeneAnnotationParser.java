package org.monarchinitiative.phenol.io.obo.hpo;

import org.monarchinitiative.phenol.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.io.base.TermAnnotationParser;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parser for "genes to phenotype annotation" files.
 *
 * <p><b>Usage Example</b>
 *
 * <pre>
 * File inputFile = new File("genes_to_phenotype.txt");
 * try {
 *   HpoGeneAnnotationParser parser = new HpoGeneAnnotationParser(inputFile);
 *   while (parser.hasNext()) {
 *     HpoGeneAnnotation anno = parser.next();
 *     // ...
 *   }
 * } catch (IOException e) {
 *    e.printStackTrace();
 *    System.err.println("Problem reading from file.");
 * } catch (TermAnnotationParserException e) {
 *    e.printStackTrace();
 *    System.err.println("Problem reading from file.");
 * }
 * </pre>
 *
 * TODO refactor to use new phenol classes
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
//@Deprecated
public class HpoGeneAnnotationParser implements TermAnnotationParser<HpoGeneAnnotation> {

  /** Expected header string. */
  private static final String EXPECTED_HEADER =
      "#Format: entrez-gene-id<tab>entrez-gene-symbol<tab>HPO-Term-Name<tab>HPO-Term-ID";

  /** The {@link File} to read from. */
  private final File file;

  /** The {@link BufferedReader} to use for reading line-wise. */
  private final BufferedReader reader;

  /** The next line. */
  private String nextLine;

  /**
   * Create new parser for HPO gene annotation files.
   *
   * @param file The file to read from.
   * @throws IOException In case of problems with opening and reading from <code>file</code>.
   * @throws TermAnnotationParserException If there are problems with the file's header.
   */
  public HpoGeneAnnotationParser(File file) throws IOException, TermAnnotationParserException {
    this.file = file;
    this.reader = new BufferedReader(new FileReader(file));
    this.nextLine = reader.readLine();
    checkHeader();
  }

  /**
   * Read first line and check header.
   *
   * @throws TermAnnotationParserException If the header is not as expected.
   * @throws IOException If there is a problem with reading from the file.
   */
  private void checkHeader() throws TermAnnotationParserException, IOException {
    if (!EXPECTED_HEADER.equals(nextLine)) {
      throw new TermAnnotationParserException(
          "First line is not the expected header. \""
              + nextLine
              + "\" vs. \""
              + EXPECTED_HEADER
              + "\" (expected)");
    }
    nextLine = reader.readLine();
  }

  @Override
  public boolean hasNext() {
    return nextLine != null;
  }

  @Override
  public HpoGeneAnnotation next() throws IOException {
    final String[] arr = nextLine.split("\t");
    final int geneId = Integer.parseInt(arr[0]);
    final String geneSymbol = arr[1];
    final String hpoTermName = arr[2];
    final TermId hpoTermId = TermId.constructWithPrefix(arr[3]);

    nextLine = reader.readLine();

    return new HpoGeneAnnotation(geneId, geneSymbol, hpoTermName, hpoTermId);
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
