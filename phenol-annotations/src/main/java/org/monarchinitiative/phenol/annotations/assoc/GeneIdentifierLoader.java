package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * An interface for loading {@link GeneIdentifiers} from various sources.
 */
public interface GeneIdentifierLoader {

  /**
   * Load {@link GeneIdentifiers} from the provided <code>reader</code>.
   *
   * @param reader the reader to read.
   * @return gene IDs
   */
  GeneIdentifiers load(Reader reader) throws IOException;

  /**
   * Load {@link GeneIdentifiers} from the input stream using {@link StandardCharsets#UTF_8}.
   *
   * @param is the input stream to read.
   * @return gene IDs.
   */
  default GeneIdentifiers load(InputStream is) throws IOException {
    return load(new InputStreamReader(is, StandardCharsets.UTF_8));
  }

  /**
   * Load {@link GeneIdentifiers} from the provided <code>path</code> using {@link StandardCharsets#UTF_8}.
   *
   * @param path path to the file to read.
   * @return gene IDs.
   */
  default GeneIdentifiers load(Path path) throws IOException {
    try (InputStream is = FileUtils.newInputStream(path)) {
      return load(is);
    }
  }

}
