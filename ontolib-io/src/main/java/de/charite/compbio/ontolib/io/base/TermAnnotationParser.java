package de.charite.compbio.ontolib.io.base;

import de.charite.compbio.ontolib.ontology.data.TermAnnotation;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Interface for classes implementing parsers for files with annotation information for terms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermAnnotationParser<A extends TermAnnotation> extends Closeable {

  /**
   * Returns <code>true</code> if the file contains more element.
   *
   * @return Whether the file contains another annotation.
   */
  boolean hasNext();

  /**
   * Returns the next term annotation.
   *
   * @return The next element in the file.
   * @throws TermAnnotationParserException in case of problems with parsing the file.
   * @throws IOException in case of problems with file I/O.
   */
  A next() throws IOException, TermAnnotationParserException;

  /**
   * @return The {@link File} used for reading.
   */
  public File getFile();

}
