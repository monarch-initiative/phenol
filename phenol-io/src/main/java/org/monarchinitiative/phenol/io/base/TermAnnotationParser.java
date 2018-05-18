package org.monarchinitiative.phenol.io.base;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;

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

  /** @return The {@link File} used for reading. */
  File getFile();
}
